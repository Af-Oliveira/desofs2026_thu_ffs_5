package pt.isep.desofs.vendnet.domain.repository;

import java.util.List;
import java.util.Optional;
import pt.isep.desofs.vendnet.domain.model.slot.Slot;

public interface SlotRepository {
	Optional<Slot> findById(Long id);

<<<<<<< Updated upstream
	List<Slot> findByMachineId(Long machineId);

	Optional<Slot> findByMachineIdAndId(Long machineId, Long slotId);

	Optional<Slot> findByMachineIdAndProductId(Long machineId, Long productId);
=======
	List<Slot> lockSlotsForPurchase(Long machineId, String productSku);
>>>>>>> Stashed changes

	Slot save(Slot slot);
}
