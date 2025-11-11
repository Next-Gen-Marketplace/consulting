package next.gen.consulting.mapper.contactLink;

import next.gen.consulting.dto.contactLink.ContactLinkDto;
import next.gen.consulting.model.ContactLink;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ContactLinkMapper {
    
    @Mapping(target = "consultantId", source = "consultant.id")
    ContactLinkDto toDto(ContactLink contactLink);
}
