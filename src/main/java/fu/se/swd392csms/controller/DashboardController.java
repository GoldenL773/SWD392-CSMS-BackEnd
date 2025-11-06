package fu.se.swd392csms.controller;

import fu.se.swd392csms.dto.response.DashboardStatsResponse;
import fu.se.swd392csms.repository.EmployeeRepository;
import fu.se.swd392csms.repository.IngredientRepository;
import fu.se.swd392csms.repository.OrderRepository;
import fu.se.swd392csms.repository.ProductRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * Dashboard Controller
 * Handles dashboard statistics and overview data
 */
@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
@Tag(name = "Dashboard", description = "Dashboard statistics APIs")
public class DashboardController {
    
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final EmployeeRepository employeeRepository;
    private final IngredientRepository ingredientRepository;
    
    /**
     * Get dashboard statistics
     */
    @GetMapping("/stats")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'MANAGER', 'STAFF', 'FINANCE')")
    @Operation(summary = "Get dashboard stats", description = "Get overview statistics for dashboard")
    public ResponseEntity<DashboardStatsResponse> getDashboardStats() {
        
        // Today's date range
        LocalDateTime todayStart = LocalDate.now().atStartOfDay();
        LocalDateTime todayEnd = LocalDate.now().atTime(LocalTime.MAX);
        
        // Month's date range
        LocalDateTime monthStart = LocalDate.now().withDayOfMonth(1).atStartOfDay();
        LocalDateTime monthEnd = LocalDate.now().atTime(LocalTime.MAX);
        
        // Count orders by status
        int totalOrders = (int) orderRepository.count();
        int pendingOrders = (int) orderRepository.countByStatus("Pending");
        int completedOrders = (int) orderRepository.countByStatus("Completed");
        
        // Calculate revenue
        Double todayRevenueDouble = orderRepository.getTotalRevenue(todayStart, todayEnd);
        Double monthRevenueDouble = orderRepository.getTotalRevenue(monthStart, monthEnd);
        
        BigDecimal todayRevenue = todayRevenueDouble != null ? BigDecimal.valueOf(todayRevenueDouble) : BigDecimal.ZERO;
        BigDecimal monthRevenue = monthRevenueDouble != null ? BigDecimal.valueOf(monthRevenueDouble) : BigDecimal.ZERO;
        
        // Count low stock items (quantity <= minimum stock)
        int lowStockItems = ingredientRepository.findAll().stream()
                .filter(i -> i.getQuantity().compareTo(i.getMinimumStock()) <= 0)
                .toList()
                .size();
        
        // Count active employees
        int activeEmployees = employeeRepository.findByStatus("Active").size();
        
        // Count total products
        int totalProducts = (int) productRepository.count();
        
        DashboardStatsResponse stats = DashboardStatsResponse.builder()
                .totalOrders(totalOrders)
                .pendingOrders(pendingOrders)
                .completedOrders(completedOrders)
                .todayRevenue(todayRevenue)
                .monthRevenue(monthRevenue)
                .lowStockItems(lowStockItems)
                .activeEmployees(activeEmployees)
                .totalProducts(totalProducts)
                .build();
        
        return ResponseEntity.ok(stats);
    }
}
