package com.csms.report.service;

import com.csms.report.client.EmployeeClient;
import com.csms.report.client.InventoryClient;
import com.csms.report.client.OrderClient;
import com.csms.report.dto.DashboardResponse;
import com.csms.report.dto.OrderResponse;
import com.csms.report.dto.SalaryResponse;
import com.csms.report.dto.TransactionResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReportService {

    private final OrderClient orderClient;
    private final EmployeeClient employeeClient;
    private final InventoryClient inventoryClient;

    @Cacheable(value = "dashboardData", key = "'today'")
    public DashboardResponse getDashboardData() {
        log.info("Fetching fresh dashboard data (not cached)");
        
        List<OrderResponse> allOrders = orderClient.getAllOrders();
        List<SalaryResponse> allSalaries = employeeClient.getAllSalaries();
        List<TransactionResponse> allTransactions = inventoryClient.getAllTransactions();

        LocalDate today = LocalDate.now();

        // Today's Orders & Revenue
        List<OrderResponse> todayOrders = allOrders.stream()
                .filter(o -> o.getOrderDate().toLocalDate().isEqual(today))
                .filter(o -> "COMPLETED".equals(o.getStatus()))
                .collect(Collectors.toList());

        BigDecimal todayRevenue = todayOrders.stream()
                .map(OrderResponse::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Costs (Salaries + Inventory Imports)
        BigDecimal totalSalaryCost = allSalaries.stream()
                .map(SalaryResponse::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalInventoryCost = allTransactions.stream()
                .filter(t -> "IMPORT".equals(t.getTransactionType()))
                .map(TransactionResponse::getTotalValue)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalCost = totalSalaryCost.add(totalInventoryCost);

        // Total Revenue for profit calculation
        BigDecimal overallRevenue = allOrders.stream()
                .filter(o -> "COMPLETED".equals(o.getStatus()))
                .map(OrderResponse::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal profit = overallRevenue.subtract(totalCost);

        // Last 7 days revenue
        Map<String, BigDecimal> last7Days = new HashMap<>();
        for (int i = 6; i >= 0; i--) {
            LocalDate d = today.minusDays(i);
            BigDecimal dailyRev = allOrders.stream()
                    .filter(o -> o.getOrderDate().toLocalDate().isEqual(d))
                    .filter(o -> "COMPLETED".equals(o.getStatus()))
                    .map(OrderResponse::getTotalAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            last7Days.put(d.toString(), dailyRev);
        }

        return DashboardResponse.builder()
                .todayRevenue(todayRevenue)
                .todayOrders(todayOrders.size())
                .totalCost(totalCost)
                .profit(profit)
                .last7DaysRevenue(last7Days)
                .build();
    }
}
