package next.gen.consulting.repository;

import next.gen.consulting.model.Request;
import next.gen.consulting.model.RequestStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface RequestRepository extends JpaRepository<Request, UUID> {
    List<Request> findByClientId(UUID clientId);
    List<Request> findByConsultantId(UUID consultantId);
    List<Request> findByStatus(RequestStatus status);
    Page<Request> findByClientId(UUID clientId, Pageable pageable);
    Page<Request> findByStatus(RequestStatus status, Pageable pageable);
    Page<Request> findByConsultantUserId(UUID userId, Pageable pageable);
}
