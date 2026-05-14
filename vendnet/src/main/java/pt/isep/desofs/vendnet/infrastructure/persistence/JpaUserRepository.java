package pt.isep.desofs.vendnet.infrastructure.persistence;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pt.isep.desofs.vendnet.domain.model.user.User;

@Repository
public interface JpaUserRepository
		extends JpaRepository<User, Long>, pt.isep.desofs.vendnet.domain.repository.UserRepository {

	Optional<User> findByEmail(String email);

	boolean existsByEmail(String email);
}
