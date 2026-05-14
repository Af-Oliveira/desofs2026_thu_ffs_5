package pt.isep.desofs.vendnet.infrastructure.persistence;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pt.isep.desofs.vendnet.domain.model.telemetry.MachineTelemetry;

@Repository
public interface JpaTelemetryRepository
		extends JpaRepository<MachineTelemetry, Long>,
				pt.isep.desofs.vendnet.domain.repository.TelemetryRepository {

	List<MachineTelemetry> findByMachineId(Long machineId);
}
