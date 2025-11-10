package next.gen.consulting.controller.consultant;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import next.gen.consulting.dto.consultant.ConsultantDto;
import next.gen.consulting.dto.consultant.CreateConsultantDto;
import next.gen.consulting.dto.consultant.UpdateConsultantDto;
import next.gen.consulting.service.ConsultantService;
import next.gen.consulting.service.CustomUserPrincipal;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/consultants")
@RequiredArgsConstructor
public class ConsultantController {

    private final ConsultantService consultantService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ConsultantDto> createConsultant(@Valid @RequestBody CreateConsultantDto createRequest) {
        ConsultantDto savedConsultant = consultantService.create(createRequest);
        return ResponseEntity.ok(savedConsultant);
    }

    @GetMapping
    public ResponseEntity<Page<ConsultantDto>> getAllConsultants(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        Page<ConsultantDto> consultants = consultantService.getAll(PageRequest.of(page, size));
        return ResponseEntity.ok(consultants);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ConsultantDto> getConsultantById(@PathVariable UUID id) {
        return ResponseEntity.ok(consultantService.getById(id));
    }

    @GetMapping("/{name}")
    public ResponseEntity<List<ConsultantDto>> getConsultantById(@PathVariable String name) {
        return ResponseEntity.ok(consultantService.search(name));
    }

    @GetMapping("/by-user/{userId}")
    public ResponseEntity<ConsultantDto> getConsultantByUserId(@PathVariable UUID userId) {
        return ResponseEntity.ok(consultantService.getByUserId(userId));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('CONSULTANT')")
    public ResponseEntity<ConsultantDto> updateConsultant(
            @AuthenticationPrincipal CustomUserPrincipal principal,
            @RequestBody UpdateConsultantDto updateRequest) {
        return ResponseEntity.ok(consultantService.update(principal.getId(), updateRequest));
    }

    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteConsultant(@PathVariable UUID id) {
        consultantService.delete(id);
        return ResponseEntity.ok("Консультант удален");
    }
}