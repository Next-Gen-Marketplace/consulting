package next.gen.consulting.dto.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdateRequest {

    @Schema(description = "Полное имя пользователя", example = "Иван Иванов")
    @JsonProperty("full_name")
    private String fullName;

    @Schema(description = "Электронная почта", example = "ivan@example.com")
    private String email;

    @Schema(description = "Номер телефона", example = "+77011234567")
    private String phone;

    @Schema(description = "URL аватара", example = "https://example.com/avatar.png")
    @JsonProperty("avatar_url")
    private String avatarUrl;
}
