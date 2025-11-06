package fu.se.swd392csms.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Attendance Request DTO
 * Used for recording employee attendance
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceRequest {
    
    @NotNull(message = "Employee ID is required")
    private Long employeeId;
    
    @NotNull(message = "Check-in time is required")
    private LocalDateTime checkInTime;
    
    private LocalDateTime checkOutTime;
    
    private String status;
    
    private String notes;
}
