package next.gen.consulting.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateRequestDto {
    @JsonProperty("full_name")
    private String fullName;
    private String phone;
    private String product;
    private String description;
    private UUID consultantId;
}
