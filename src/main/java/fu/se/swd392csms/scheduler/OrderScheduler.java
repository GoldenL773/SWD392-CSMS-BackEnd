package fu.se.swd392csms.scheduler;

import fu.se.swd392csms.entity.Order;
import fu.se.swd392csms.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Order Scheduler
 * Handles automatic order cancellation for expired orders
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OrderScheduler {
    
    private final OrderRepository orderRepository;
    
    @Value("${orders.auto-cancel.threshold-minutes:60}")
    private int autoCancelThresholdMinutes;

    /**
     * Auto-cancel orders that are still PENDING beyond a configurable threshold (default 60 minutes)
     * Runs every minute for timely cancellation.
     */
    @Scheduled(fixedRate = 60000)
    @Transactional
    public void autoCancelExpiredOrders() {
        final LocalDateTime now = LocalDateTime.now();
        final LocalDateTime threshold = now.minusMinutes(autoCancelThresholdMinutes);
        log.info("[OrderScheduler] Running auto-cancel. Threshold: {} minute(s) ago at {}", autoCancelThresholdMinutes, threshold);

        try {
            // Query only the orders that need cancellation at DB level
            List<Order> expiredPendingOrders = orderRepository.findPendingOrdersOlderThan(threshold);

            if (expiredPendingOrders.isEmpty()) {
                log.info("[OrderScheduler] No expired pending orders found.");
                return;
            }

            int cancelledCount = 0;
            for (Order order : expiredPendingOrders) {
                try {
                    order.setStatus("CANCELLED");
                    orderRepository.save(order);
                    cancelledCount++;
                    log.info("[OrderScheduler] Auto-cancelled order ID: {} (created at: {})", order.getId(), order.getOrderDate());
                } catch (Exception e) {
                    log.error("[OrderScheduler] Error auto-cancelling order ID: {}: {}", order.getId(), e.getMessage(), e);
                }
            }

            log.info("[OrderScheduler] Completed. Cancelled {} order(s).", cancelledCount);
        } catch (Exception e) {
            log.error("[OrderScheduler] Error during auto-cancel run: {}", e.getMessage(), e);
        }
    }
}
