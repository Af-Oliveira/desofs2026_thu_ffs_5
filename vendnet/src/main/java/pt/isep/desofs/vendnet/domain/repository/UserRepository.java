package pt.isep.desofs.vendnet.domain.repository;

import java.util.List;
import java.util.Optional;
import pt.isep.desofs.vendnet.domain.model.user.User;

public interface UserRepository {
	Optional<User> findByEmail(String email);

	Optional<User> findByUsername(String username);

	Optional<User> findById(Long id);

	List<User> findAll();

	boolean existsByEmail(String email);

	boolean existsByUsername(String username);

	User save(User user);
}
