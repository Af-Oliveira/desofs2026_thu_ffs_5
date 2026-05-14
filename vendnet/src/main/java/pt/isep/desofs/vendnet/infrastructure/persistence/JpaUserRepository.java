package pt.isep.desofs.vendnet.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pt.isep.desofs.vendnet.domain.model.user.User;

import java.util.Optional;

@Repository
public interface JpaUserRepository extends JpaRepository<User, Long>, pt.isep.desofs.vendnet.domain.repository.UserRepository {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);
}
