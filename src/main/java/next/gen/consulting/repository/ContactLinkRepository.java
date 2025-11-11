package next.gen.consulting.repository;

import next.gen.consulting.model.ContactLink;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ContactLinkRepository extends JpaRepository<ContactLink, UUID> {
    List<ContactLink> findByConsultantId(UUID consultantId);

    @Query("SELECT cl FROM ContactLink cl WHERE cl.consultant.user.id = :userId")
    List<ContactLink> findByUserId(UUID userId);
}
