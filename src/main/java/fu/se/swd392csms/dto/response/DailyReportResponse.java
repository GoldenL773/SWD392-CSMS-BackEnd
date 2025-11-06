package fu.se.swd392csms.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Daily Report Response DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DailyReportResponse {
    private Long id;
    private LocalDate reportDate;
    private Integer totalOrders;
    private BigDecimal totalRevenue;
    private BigDecimal totalCost;
    private BigDecimal profit;
    private Integer completedOrders;
    private Integer cancelledOrders;
}
