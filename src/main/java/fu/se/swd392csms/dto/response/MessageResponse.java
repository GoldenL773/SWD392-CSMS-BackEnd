package fu.se.swd392csms.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Generic Message Response DTO
 * Used for simple success/error messages
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageResponse {
    
    private String message;
}
