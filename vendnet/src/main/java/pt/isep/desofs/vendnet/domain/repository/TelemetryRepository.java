package pt.isep.desofs.vendnet.domain.repository;

import pt.isep.desofs.vendnet.domain.model.telemetry.MachineTelemetry;

import java.util.List;

public interface TelemetryRepository {
    MachineTelemetry save(MachineTelemetry telemetry);
    List<MachineTelemetry> findByMachineId(Long machineId);
}
