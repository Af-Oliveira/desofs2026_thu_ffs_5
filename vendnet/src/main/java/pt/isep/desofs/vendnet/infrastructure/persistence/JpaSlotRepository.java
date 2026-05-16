package pt.isep.desofs.vendnet.infrastructure.persistence;

import jakarta.persistence.LockModeType;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pt.isep.desofs.vendnet.domain.model.slot.Slot;

@Repository
public interface JpaSlotRepository
		extends JpaRepository<Slot, Long>, pt.isep.desofs.vendnet.domain.repository.SlotRepository {

	@Query("SELECT s FROM Slot s WHERE s.machine.id = :machineId")
	List<Slot> findByMachineId(@Param("machineId") Long machineId);

	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query("SELECT s FROM Slot s WHERE s.machine.id = :machineId AND s.id = :slotId")
	Optional<Slot> findByMachineIdAndId(@Param("machineId") Long machineId, @Param("slotId") Long slotId);

	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query("SELECT s FROM Slot s WHERE s.machine.id = :machineId AND s.product.id = :productId")
	Optional<Slot> findByMachineIdAndProductId(@Param("machineId") Long machineId, @Param("productId") Long productId);

	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query("SELECT s FROM Slot s WHERE s.machine.id = :machineId AND s.product.sku = :productSku ORDER BY s.id")
	List<Slot> lockSlotsForPurchase(
			@Param("machineId") Long machineId, @Param("productSku") String productSku);
}
