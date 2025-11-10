package next.gen.consulting.dto.notification;

import io.swagger.v3.oas.annotations.Hidden;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.UUID;

@Data
@Hidden
public class CreateNotificationDto {
    @NotBlank(message = "User ID обязателен")
    private UUID userId;
    
    @NotBlank(message = "Сообщение обязательно")
    private String message;
}
