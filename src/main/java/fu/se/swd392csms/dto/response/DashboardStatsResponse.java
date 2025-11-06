package fu.se.swd392csms.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Dashboard Statistics Response DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardStatsResponse {
    private Integer totalOrders;
    private Integer pendingOrders;
    private Integer completedOrders;
    private BigDecimal todayRevenue;
    private BigDecimal monthRevenue;
    private Integer lowStockItems;
    private Integer activeEmployees;
    private Integer totalProducts;
}
