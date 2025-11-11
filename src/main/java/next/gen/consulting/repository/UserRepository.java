package next.gen.consulting.repository;

import next.gen.consulting.model.Consultant;
import next.gen.consulting.model.User;
import next.gen.consulting.model.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByEmail(String email);
    Optional<User> findByPhone(String phone);
    boolean existsByEmail(String email);
    boolean existsByPhone(String phone);
    List<User> findAllByRole(UserRole role);

    @Query("""
        SELECT u FROM User u
        WHERE u.role = 'ADMIN' OR u.role = 'CONSULTANT'
    """)
    List<User> findAllByRole_AdminOrConsultant();
}
