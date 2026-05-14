package pt.isep.desofs.vendnet.domain.repository;

import pt.isep.desofs.vendnet.domain.model.user.User;

import java.util.Optional;

public interface UserRepository {
    Optional<User> findByEmail(String email);
    Optional<User> findById(Long id);
    boolean existsByEmail(String email);
    User save(User user);
}
