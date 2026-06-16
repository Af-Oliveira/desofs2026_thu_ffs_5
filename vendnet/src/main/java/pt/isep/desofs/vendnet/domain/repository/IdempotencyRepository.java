package pt.isep.desofs.vendnet.domain.repository;

import java.util.Optional;
import pt.isep.desofs.vendnet.domain.model.sale.IdempotencyRecord;

public interface IdempotencyRepository {

	Optional<IdempotencyRecord> findByIdempotencyKey(String idempotencyKey);

	IdempotencyRecord save(IdempotencyRecord idempotencyRecord);
}
