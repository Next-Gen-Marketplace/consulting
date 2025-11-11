package next.gen.consulting.service;

import lombok.RequiredArgsConstructor;
import next.gen.consulting.dto.auth.RegisterRequest;
import next.gen.consulting.dto.user.UserDto;
import next.gen.consulting.dto.user.UserUpdateRequest;
import next.gen.consulting.exception.BadRequestException;
import next.gen.consulting.exception.ResourceNotFoundException;
import next.gen.consulting.mapper.user.UserMapper;
import next.gen.consulting.model.User;
import next.gen.consulting.model.UserRole;
import next.gen.consulting.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    public UserDto getById(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
        return userMapper.toDto(user);
    }

    public UserDto getByEmail(String email) {
        User user = findByEmail(email);
        return userMapper.toDto(user);
    }

    public UserDto getByPhone(String phone) {
        User user = userRepository.findByPhone(phone)
                .orElseThrow(() -> new ResourceNotFoundException("User", "phone", phone));
        return userMapper.toDto(user);
    }

    public Page<UserDto> getAll(Pageable pageable) {
        return userRepository.findAll(pageable)
                .map(userMapper::toDto);
    }

    @Transactional
    public UserDto create(RegisterRequest registerRequest) {
        validateUniquePhone(registerRequest.getPhone());

        User user = User.builder()
                .passwordHash(passwordEncoder.encode(registerRequest.getPassword()))
                .fullName(registerRequest.getFullName())
                .phone(registerRequest.getPhone())
                .role(UserRole.CLIENT)
                .build();

        User savedUser = userRepository.save(user);
        return userMapper.toDto(savedUser);
    }

    @Transactional
    public UserDto update(UUID id, UserUpdateRequest updateRequest) {
        User user = findById(id);

        if (updateRequest.getEmail() != null && !user.getEmail().equals(updateRequest.getEmail())) {
            validateUniqueEmail(updateRequest.getEmail());
            user.setEmail(updateRequest.getEmail());
        }

        if (updateRequest.getPhone() != null && !user.getPhone().equals(updateRequest.getPhone())) {
            validateUniquePhone(updateRequest.getPhone());
            user.setPhone(updateRequest.getPhone());
        }

        if (updateRequest.getFullName() != null) {
            user.setFullName(updateRequest.getFullName());
        }

        if (updateRequest.getAvatarUrl() != null) {
            user.setAvatarUrl(updateRequest.getAvatarUrl());
        }

        User updatedUser = userRepository.save(user);
        return userMapper.toDto(updatedUser);
    }

    @Transactional
    public UserDto updateRole(UUID id, UserRole role) {
        User user = findById(id);
        user.setRole(role);
        User updatedUser = userRepository.save(user);
        return userMapper.toDto(updatedUser);
    }

    @Transactional
    public void delete(UUID id) {
        User user = findById(id);
        userRepository.delete(user);
    }

    public User findEntityById(UUID id) {
        return findById(id);
    }

    public User findEntityByEmail(String email) {
        return findByEmail(email);
    }

    private User findById(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
    }

    private User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));
    }

    private void validateUniqueEmail(String email) {
        if (userRepository.existsByEmail(email)) {
            throw new BadRequestException("Email уже используется");
        }
    }

    private void validateUniquePhone(String phone) {
        if (userRepository.existsByPhone(phone)) {
            throw new BadRequestException("Телефон уже используется");
        }
    }
}