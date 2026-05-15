package pt.isep.desofs.vendnet.infrastructure.persistence;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pt.isep.desofs.vendnet.domain.model.sale.IdempotencyRecord;

@Repository
public interface JpaIdempotencyRepository
		extends JpaRepository<IdempotencyRecord, Long>,
				pt.isep.desofs.vendnet.domain.repository.IdempotencyRepository {

	Optional<IdempotencyRecord> findByIdempotencyKey(String key);
}
