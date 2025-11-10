package next.gen.consulting.dto.contactLink;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateContactLinkDto {

    @JsonProperty("service_name")
    @NotBlank(message = "Название сервиса обязательно")
    private String serviceName;

    @NotBlank(message = "Ссылка обязательна")
    private String link;

}
