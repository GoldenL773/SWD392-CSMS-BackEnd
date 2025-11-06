package fu.se.swd392csms.controller;

import fu.se.swd392csms.dto.response.DailyReportResponse;
import fu.se.swd392csms.repository.OrderRepository;
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
            
            DailyReportResponse report = DailyReportResponse.builder()
                    .reportDate(currentDate)
                    .totalOrders(totalOrders)
                    .completedOrders(completedOrders)
                    .cancelledOrders(cancelledOrders)
                    .totalRevenue(totalRevenue)
                    .totalCost(BigDecimal.ZERO) // TODO: Calculate from ingredients
                    .profit(totalRevenue) // TODO: revenue - cost
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
        
        DailyReportResponse report = DailyReportResponse.builder()
                .reportDate(date)
                .totalOrders(totalOrders)
                .completedOrders(completedOrders)
                .cancelledOrders(cancelledOrders)
                .totalRevenue(totalRevenue)
                .totalCost(BigDecimal.ZERO)
                .profit(totalRevenue)
                .build();
        
        return ResponseEntity.ok(report);
    }
}
