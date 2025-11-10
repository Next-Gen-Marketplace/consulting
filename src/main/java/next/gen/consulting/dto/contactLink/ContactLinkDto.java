package next.gen.consulting.dto.contactLink;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContactLinkDto {
    private UUID id;
    private UUID consultantId;
    private String serviceName;
    private String link;
    private LocalDateTime createdAt;
}
