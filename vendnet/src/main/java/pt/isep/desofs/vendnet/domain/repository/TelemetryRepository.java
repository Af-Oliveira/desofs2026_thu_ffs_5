package pt.isep.desofs.vendnet.domain.repository;

import java.util.List;
import pt.isep.desofs.vendnet.domain.model.telemetry.MachineTelemetry;

public interface TelemetryRepository {
	MachineTelemetry save(MachineTelemetry telemetry);

	List<MachineTelemetry> findByMachineId(Long machineId);
}
