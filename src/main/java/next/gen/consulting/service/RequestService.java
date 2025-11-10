package next.gen.consulting.service;

import lombok.RequiredArgsConstructor;
import next.gen.consulting.dto.request.CreateRequestDto;
import next.gen.consulting.dto.request.RequestDto;
import next.gen.consulting.dto.request.UpdateRequestDto;
import next.gen.consulting.exception.ResourceNotFoundException;
import next.gen.consulting.mapper.RequestMapper;
import next.gen.consulting.model.Consultant;
import next.gen.consulting.model.Request;
import next.gen.consulting.model.RequestStatus;
import next.gen.consulting.model.User;
import next.gen.consulting.repository.ConsultantRepository;
import next.gen.consulting.repository.RequestRepository;
import next.gen.consulting.repository.UserRepository;
import next.gen.consulting.service.request.RequestActionChain;
import next.gen.consulting.service.request.RequestActionContext;
import next.gen.consulting.service.request.RequestActionType;
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
public class RequestService {

    private final RequestRepository requestRepository;
    private final UserRepository userRepository;
    private final ConsultantRepository consultantRepository;
    private final RequestMapper requestMapper;
    private final RequestActionChain requestActionChain;

    public RequestDto getById(UUID id) {
        Request request = findById(id);
        return requestMapper.toDto(request);
    }

    public Page<RequestDto> getAll(Pageable pageable) {
        return requestRepository.findAll(pageable)
                .map(requestMapper::toDto);
    }

    public List<RequestDto> getByClientId(UUID clientId, Pageable pageable) {
        return requestRepository.findByClientId(clientId, pageable).stream()
                .map(requestMapper::toDto)
                .toList();
    }

    public Page<RequestDto> getConsultantRequests(UUID userId, Pageable pageable) {
        return requestRepository.findByConsultantUserId(userId, pageable)
                .map(requestMapper::toDto);
    }

    public List<RequestDto> getByStatus(RequestStatus status, Pageable pageable) {
        return requestRepository.findByStatus(status, pageable).stream()
                .map(requestMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public RequestDto create(CreateRequestDto requestDto, UUID id) {
        User client = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));

        Request request = Request.builder()
                .fullName(requestDto.getFullName())
                .phone(requestDto.getPhone())
                .product(requestDto.getProduct())
                .description(requestDto.getDescription())
                .client(client)
                .status(RequestStatus.PENDING)
                .build();

        Request savedRequest = requestRepository.save(request);
        RequestDto dto = requestMapper.toDto(savedRequest);

        requestActionChain.process(RequestActionContext.builder()
                .actionType(RequestActionType.CREATED)
                .request(dto)
                .actorId(id)
                .build());

        return dto;
    }

    @Transactional
    public RequestDto update(UUID id, UpdateRequestDto requestDto) {
        Request request = findById(id);

        if (requestDto.getFullName() != null) {
            request.setFullName(requestDto.getFullName());
        }
        if (requestDto.getPhone() != null) {
            request.setPhone(requestDto.getPhone());
        }
        if (requestDto.getDescription() != null) {
            request.setDescription(requestDto.getDescription());
        }
        if (requestDto.getProduct() != null) {
            request.setComment(requestDto.getPhone());
        }
        if (requestDto.getConsultantId() != null) {
            Consultant consultant = consultantRepository.findById(requestDto.getConsultantId())
                    .orElseThrow(() -> new ResourceNotFoundException("Consultant", "id", requestDto.getConsultantId()));
            request.setConsultant(consultant);
        }

        Request updatedRequest = requestRepository.save(request);
        RequestDto dto = requestMapper.toDto(updatedRequest);

        requestActionChain.process(RequestActionContext.builder()
                .actionType(RequestActionType.UPDATED)
                .request(dto)
                .build());

        return dto;
    }

    public List<RequestDto> getMyRequests(UUID id, Pageable pageable) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
        return getByClientId(user.getId(), pageable);
    }

    @Transactional
    public RequestDto updateStatus(UUID id, RequestStatus status, UUID consultantId, String comment) {
        Request request = findById(id);
        RequestStatus previousStatus = request.getStatus();
        request.setStatus(status);

        if (consultantId != null) {
            Consultant consultant = consultantRepository.findById(consultantId)
                    .orElseThrow(() -> new ResourceNotFoundException("Consultant", "id", consultantId));
            request.setConsultant(consultant);
        }

        if (comment != null) {
            request.setComment(comment);
        }

        Request updatedRequest = requestRepository.save(request);
        RequestDto dto = requestMapper.toDto(updatedRequest);

        requestActionChain.process(RequestActionContext.builder()
                .actionType(RequestActionType.STATUS_CHANGED)
                .request(dto)
                .previousStatus(previousStatus)
                .actorId(consultantId)
                .build());

        return dto;
    }

    @Transactional
    public void delete(UUID id) {
        Request request = findById(id);
        RequestDto dto = requestMapper.toDto(request);
        requestRepository.delete(request);

        requestActionChain.process(RequestActionContext.builder()
                .actionType(RequestActionType.DELETED)
                .request(dto)
                .build());
    }

    private Request findById(UUID id) {
        return requestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Request", "id", id));
    }
}
