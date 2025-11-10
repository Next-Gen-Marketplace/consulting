package next.gen.consulting.dto.notification;

import lombok.Data;

@Data
public class UpdateNotificationDto {
    private String message;
    private Boolean isRead;
}
