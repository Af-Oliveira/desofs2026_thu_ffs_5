package pt.isep.desofs.vendnet.domain.repository;

import java.util.Optional;
import pt.isep.desofs.vendnet.domain.model.slot.Slot;

public interface SlotRepository {
	Optional<Slot> findById(Long id);

	Slot save(Slot slot);
}
