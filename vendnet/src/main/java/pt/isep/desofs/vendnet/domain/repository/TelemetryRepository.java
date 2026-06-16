package pt.isep.desofs.vendnet.domain.repository;

import java.util.List;
import java.time.LocalDateTime;
import pt.isep.desofs.vendnet.domain.model.telemetry.MachineTelemetry;

public interface TelemetryRepository {
	MachineTelemetry save(MachineTelemetry telemetry);

	List<MachineTelemetry> findByMachineId(Long machineId);

	long countByMachineIdAndTimestampAfter(Long machineId, LocalDateTime since);
}
