package next.gen.consulting.mapper;

import next.gen.consulting.dto.achievement.AchievementDto;
import next.gen.consulting.model.Achievement;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface AchievementMapper {
    
    @Mapping(target = "userId", source = "user.id")
    AchievementDto toDto(Achievement achievement);
}
