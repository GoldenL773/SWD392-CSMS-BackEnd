package com.csms.report.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardResponse {
    private BigDecimal todayRevenue;
    private Integer todayOrders;
    private BigDecimal totalCost;
    private BigDecimal profit;
    private Map<String, BigDecimal> last7DaysRevenue;
}
