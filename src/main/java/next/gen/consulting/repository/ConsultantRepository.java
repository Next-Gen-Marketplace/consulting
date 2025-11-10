package next.gen.consulting.repository;

import next.gen.consulting.model.Consultant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ConsultantRepository extends JpaRepository<Consultant, UUID> {
    Optional<Consultant> findByUserId(UUID userId);

    List<Consultant> findByUserFullNameContainingIgnoreCase(String fullName);
}
