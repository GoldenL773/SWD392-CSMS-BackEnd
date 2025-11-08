package fu.se.swd392csms.scheduler;

import fu.se.swd392csms.entity.Order;
import fu.se.swd392csms.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    
    /**
     * Auto-cancel orders that are still PENDING 10 minutes past end of shift (12:09 AM)
     * Runs daily at 12:09 AM
     */
    @Scheduled(cron = "0 9 0 * * ?") // Every day at 12:09 AM
    @Transactional
    public void autoCancelExpiredOrders() {
        log.info("Starting auto-cancel scheduler for expired orders...");
        
        try {
            // Get all PENDING orders
            List<Order> pendingOrders = orderRepository.findByStatus("PENDING");
            
            if (pendingOrders.isEmpty()) {
                log.info("No pending orders to check for expiration");
                return;
            }
            
            LocalDateTime now = LocalDateTime.now();
            // Calculate cutoff time: 10 minutes ago (orders created before 12:09 AM - 10 mins = 11:59 PM previous day)
            LocalDateTime cutoffTime = now.minusMinutes(10);
            
            int cancelledCount = 0;
            
            for (Order order : pendingOrders) {
                try {
                    // Cancel if created before cutoff time (10 mins past end of shift)
                    if (order.getOrderDate().isBefore(cutoffTime)) {
                        order.setStatus("CANCELLED");
                        
                        // Add audit note
                        String existingNotes = order.getNotes() != null ? order.getNotes() + "; " : "";
                        order.setNotes(existingNotes + "Auto-cancelled - order expired (10 mins past end of shift)");
                        
                        orderRepository.save(order);
                        cancelledCount++;
                        
                        log.info("Auto-cancelled order ID: {} (created at: {})", 
                                order.getId(), 
                                order.getOrderDate());
                    }
                } catch (Exception e) {
                    log.error("Error auto-cancelling order ID: {}: {}", 
                            order.getId(), 
                            e.getMessage(), e);
                }
            }
            
            log.info("Auto-cancel scheduler completed. Cancelled {} order(s)", cancelledCount);
            
        } catch (Exception e) {
            log.error("Error during auto-cancel scheduler: {}", e.getMessage(), e);
        }
    }
}
