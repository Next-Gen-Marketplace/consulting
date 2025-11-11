package next.gen.consulting.mapper.user;

import next.gen.consulting.dto.user.UserDto;
import next.gen.consulting.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface UserMapper {
    
    UserDto toDto(User user);
}
