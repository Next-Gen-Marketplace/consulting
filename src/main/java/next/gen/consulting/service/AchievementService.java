package next.gen.consulting.service;

import lombok.RequiredArgsConstructor;
import next.gen.consulting.dto.achievement.AchievementDto;
import next.gen.consulting.dto.achievement.CreateAchievementDto;
import next.gen.consulting.dto.achievement.UpdateAchievementDto;
import next.gen.consulting.exception.ResourceNotFoundException;
import next.gen.consulting.mapper.achievement.AchievementMapper;
import next.gen.consulting.model.Achievement;
import next.gen.consulting.model.User;
import next.gen.consulting.repository.AchievementRepository;
import next.gen.consulting.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AchievementService {

    private final AchievementRepository achievementRepository;
    private final UserRepository userRepository;
    private final AchievementMapper achievementMapper;

    public AchievementDto getById(UUID id) {
        Achievement achievement = findById(id);
        return achievementMapper.toDto(achievement);
    }

    public List<AchievementDto> getByUserId(UUID userId) {
        return achievementRepository.findByUserId(userId).stream()
                .map(achievementMapper::toDto)
                .collect(Collectors.toList());
    }

    public Page<AchievementDto> getAll(Pageable pageable) {
        return achievementRepository.findAll(pageable)
                .map(achievementMapper::toDto);
    }

    @Transactional
    public AchievementDto create(CreateAchievementDto achievementDto) {
        User user = userRepository.findById(achievementDto.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", achievementDto.getUserId()));

        Achievement achievement = Achievement.builder()
                .user(user)
                .description(achievementDto.getDescription())
                .build();

        Achievement savedAchievement = achievementRepository.save(achievement);
        return achievementMapper.toDto(savedAchievement);
    }

    @Transactional
    public AchievementDto update(UUID id, UpdateAchievementDto achievementDto) {
        Achievement achievement = findById(id);
        achievement.setDescription(achievementDto.getDescription());
        Achievement updatedAchievement = achievementRepository.save(achievement);
        return achievementMapper.toDto(updatedAchievement);
    }

    @Transactional
    public void delete(UUID id) {
        Achievement achievement = findById(id);
        achievementRepository.delete(achievement);
    }

    private Achievement findById(UUID id) {
        return achievementRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Achievement", "id", id));
    }
}
