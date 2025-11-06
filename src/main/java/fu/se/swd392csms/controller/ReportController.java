package fu.se.swd392csms.controller;

import fu.se.swd392csms.dto.response.DailyReportResponse;
import fu.se.swd392csms.repository.OrderRepository;
import fu.se.swd392csms.repository.OrderItemRepository;
import fu.se.swd392csms.repository.ProductIngredientRepository;
import fu.se.swd392csms.repository.SalaryRepository;
import fu.se.swd392csms.entity.Order;
import fu.se.swd392csms.entity.OrderItem;
import fu.se.swd392csms.entity.ProductIngredient;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Report Controller
 * Handles daily reports and analytics endpoints
 */
@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
@Tag(name = "Reports", description = "Report management APIs")
public class ReportController {
    
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProductIngredientRepository productIngredientRepository;
    private final SalaryRepository salaryRepository;
    
    /**
     * Get daily reports with optional date range filter
     */
    @GetMapping("/daily")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'MANAGER', 'FINANCE')")
    @Operation(summary = "Get daily reports", description = "Get daily sales reports with optional date range")
    public ResponseEntity<List<DailyReportResponse>> getDailyReports(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        // Default to last 7 days if no dates provided
        if (startDate == null) {
            startDate = LocalDate.now().minusDays(7);
        }
        if (endDate == null) {
            endDate = LocalDate.now();
        }
        
        List<DailyReportResponse> reports = new ArrayList<>();
        
        // Generate report for each day in range
        LocalDate currentDate = startDate;
        while (!currentDate.isAfter(endDate)) {
            LocalDateTime dayStart = currentDate.atStartOfDay();
            LocalDateTime dayEnd = currentDate.atTime(LocalTime.MAX);
            
            var orders = orderRepository.findByOrderDateBetween(dayStart, dayEnd);
            
            int totalOrders = orders.size();
            int completedOrders = (int) orders.stream().filter(o -> "Completed".equals(o.getStatus())).count();
            int cancelledOrders = (int) orders.stream().filter(o -> "Cancelled".equals(o.getStatus())).count();
            
            BigDecimal totalRevenue = orders.stream()
                    .filter(o -> "Completed".equals(o.getStatus()))
                    .map(o -> o.getTotalAmount())
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            
            // Calculate ingredient cost for completed orders
            BigDecimal ingredientCost = calculateIngredientCost(orders);
            
            // Calculate salary cost for this day (prorated daily salary)
            BigDecimal salaryCost = calculateDailySalaryCost(currentDate);
            
            BigDecimal totalCost = ingredientCost.add(salaryCost);
            BigDecimal profit = totalRevenue.subtract(totalCost);
            
            DailyReportResponse report = DailyReportResponse.builder()
                    .reportDate(currentDate)
                    .totalOrders(totalOrders)
                    .completedOrders(completedOrders)
                    .cancelledOrders(cancelledOrders)
                    .totalRevenue(totalRevenue)
                    .totalCost(totalCost)
                    .profit(profit)
                    .build();
            
            reports.add(report);
            currentDate = currentDate.plusDays(1);
        }
        
        return ResponseEntity.ok(reports);
    }
    
    /**
     * Get daily report by specific date
     */
    @GetMapping("/daily/{date}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'MANAGER', 'FINANCE')")
    @Operation(summary = "Get daily report by date", description = "Get sales report for a specific date")
    public ResponseEntity<DailyReportResponse> getDailyReportByDate(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        
        LocalDateTime dayStart = date.atStartOfDay();
        LocalDateTime dayEnd = date.atTime(LocalTime.MAX);
        
        var orders = orderRepository.findByOrderDateBetween(dayStart, dayEnd);
        
        int totalOrders = orders.size();
        int completedOrders = (int) orders.stream().filter(o -> "Completed".equals(o.getStatus())).count();
        int cancelledOrders = (int) orders.stream().filter(o -> "Cancelled".equals(o.getStatus())).count();
        
        BigDecimal totalRevenue = orders.stream()
                .filter(o -> "Completed".equals(o.getStatus()))
                .map(o -> o.getTotalAmount())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        // Calculate ingredient cost for completed orders
        BigDecimal ingredientCost = calculateIngredientCost(orders);
        
        // Calculate salary cost for this day (prorated daily salary)
        BigDecimal salaryCost = calculateDailySalaryCost(date);
        
        BigDecimal totalCost = ingredientCost.add(salaryCost);
        BigDecimal profit = totalRevenue.subtract(totalCost);
        
        DailyReportResponse report = DailyReportResponse.builder()
                .reportDate(date)
                .totalOrders(totalOrders)
                .completedOrders(completedOrders)
                .cancelledOrders(cancelledOrders)
                .totalRevenue(totalRevenue)
                .totalCost(totalCost)
                .profit(profit)
                .build();
        
        return ResponseEntity.ok(report);
    }
    
    /**
     * Calculate ingredient cost for a list of orders
     */
    private BigDecimal calculateIngredientCost(List<Order> orders) {
        BigDecimal totalCost = BigDecimal.ZERO;
        
        // Only calculate for completed orders
        List<Order> completedOrders = orders.stream()
                .filter(o -> "Completed".equals(o.getStatus()))
                .toList();
        
        for (Order order : completedOrders) {
            List<OrderItem> orderItems = orderItemRepository.findByOrderId(order.getId());
            
            for (OrderItem item : orderItems) {
                // Get ingredients for this product
                List<ProductIngredient> productIngredients = 
                        productIngredientRepository.findByProductIdWithIngredient(item.getProduct().getId());
                
                // Calculate cost for each ingredient
                for (ProductIngredient pi : productIngredients) {
                    BigDecimal ingredientCostPerUnit = pi.getIngredient().getPricePerUnit();
                    BigDecimal quantityNeeded = pi.getQuantityRequired().multiply(new BigDecimal(item.getQuantity()));
                    BigDecimal itemIngredientCost = ingredientCostPerUnit.multiply(quantityNeeded);
                    totalCost = totalCost.add(itemIngredientCost);
                }
            }
        }
        
        return totalCost;
    }
    
    /**
     * Calculate prorated daily salary cost
     * Takes the monthly salary and divides by average days in month (30)
     */
    private BigDecimal calculateDailySalaryCost(LocalDate date) {
        int month = date.getMonthValue();
        int year = date.getYear();
        
        // Get paid salaries for this month
        Double totalMonthSalary = salaryRepository.getTotalSalaryPaid(month, year);
        
        if (totalMonthSalary == null || totalMonthSalary == 0) {
            return BigDecimal.ZERO;
        }
        
        // Prorate to daily cost (divide by 30 days)
        return new BigDecimal(totalMonthSalary).divide(new BigDecimal(30), 2, java.math.RoundingMode.HALF_UP);
    }
}
