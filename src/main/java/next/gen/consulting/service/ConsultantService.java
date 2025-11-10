package next.gen.consulting.service;

import lombok.RequiredArgsConstructor;
import next.gen.consulting.dto.consultant.ConsultantDto;
import next.gen.consulting.dto.consultant.CreateConsultantDto;
import next.gen.consulting.dto.consultant.UpdateConsultantDto;
import next.gen.consulting.exception.BadRequestException;
import next.gen.consulting.exception.ResourceNotFoundException;
import next.gen.consulting.mapper.ConsultantMapper;
import next.gen.consulting.model.Consultant;
import next.gen.consulting.model.User;
import next.gen.consulting.model.UserRole;
import next.gen.consulting.repository.ConsultantRepository;
import next.gen.consulting.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ConsultantService {

    private final ConsultantRepository consultantRepository;
    private final UserRepository userRepository;
    private final ConsultantMapper consultantMapper;

    public ConsultantDto getById(UUID id) {
        Consultant consultant = findById(id);
        return consultantMapper.toDto(consultant);
    }

    public ConsultantDto getByUserId(UUID userId) {
        Consultant consultant = consultantRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Consultant", "userId", userId));
        return consultantMapper.toDto(consultant);
    }

    public List<ConsultantDto> search(String userName) {
        String query = userName == null ? "" : userName.trim();

        List<Consultant> consultants = query.isEmpty()
                ? consultantRepository.findAll()
                : consultantRepository.findByUserFullNameContainingIgnoreCase(query);

        return consultants.stream()
                .map(consultantMapper::toDto)
                .toList();
    }

    public Page<ConsultantDto> getAll(Pageable pageable) {
        return consultantRepository.findAll(pageable)
                .map(consultantMapper::toDto);
    }

    @Transactional
    public ConsultantDto create(CreateConsultantDto consultantDto) {
        User user = userRepository.findById(consultantDto.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", consultantDto.getUserId()));

        if (UserRole.CONSULTANT.equals(user.getRole())) {
            throw new BadRequestException("Пользователь уже является консультантом");
        }

        Consultant consultant = Consultant.builder()
                .user(user)
                .specialization(consultantDto.getSpecialization())
                .experience(consultantDto.getExperience())
                .build();

        Consultant savedConsultant = consultantRepository.save(consultant);
        return consultantMapper.toDto(savedConsultant);
    }

    @Transactional
    public ConsultantDto update(UUID id, UpdateConsultantDto consultantDto) {
        Consultant consultant = findById(id);

        if (consultantDto.getSpecialization() != null) {
            consultant.setSpecialization(consultantDto.getSpecialization());
        }
        if (consultantDto.getExperience() != null) {
            consultant.setExperience(consultantDto.getExperience());
        }

        Consultant updatedConsultant = consultantRepository.save(consultant);
        return consultantMapper.toDto(updatedConsultant);
    }

    @Transactional
    public void delete(UUID id) {
        Consultant consultant = findById(id);
        consultantRepository.delete(consultant);
    }

    private Consultant findById(UUID id) {
        return consultantRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Consultant", "id", id));
    }
}
