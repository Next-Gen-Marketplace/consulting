package next.gen.consulting.dto.achievement;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateAchievementDto {
    @NotBlank(message = "Описание обязательно")
    private String description;
}
