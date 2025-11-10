package next.gen.consulting.mapper;

import next.gen.consulting.dto.consultant.ConsultantDto;
import next.gen.consulting.model.Consultant;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ConsultantMapper {
    
    @Mapping(target = "userId", source = "user.id")
    ConsultantDto toDto(Consultant consultant);
}
