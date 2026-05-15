package pt.isep.desofs.vendnet.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pt.isep.desofs.vendnet.domain.model.slot.Slot;

@Repository
public interface JpaSlotRepository
		extends JpaRepository<Slot, Long>, pt.isep.desofs.vendnet.domain.repository.SlotRepository {
}
