package next.gen.consulting.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateRequestDto {
    @NotBlank(message = "ФИО обязательно")
    @JsonProperty("full_name")
    private String fullName;

    @NotBlank(message = "Телефон обязателен")
    private String phone;

    @NotBlank(message = "Товар обязателен")
    private String product;
    
    @NotBlank(message = "Описание обязательно")
    private String description;
}
