package next.gen.consulting.dto.achievement;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.UUID;

@Data
public class CreateAchievementDto {
    @NotBlank(message = "User ID обязателен")
    private UUID userId;
    
    @NotBlank(message = "Описание обязательно")
    private String description;
}
