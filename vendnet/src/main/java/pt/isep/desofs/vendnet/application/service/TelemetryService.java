package pt.isep.desofs.vendnet.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pt.isep.desofs.vendnet.domain.model.telemetry.MachineTelemetry;
import pt.isep.desofs.vendnet.domain.repository.TelemetryRepository;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TelemetryService {

    private final TelemetryRepository telemetryRepository;

    public MachineTelemetry save(MachineTelemetry telemetry) {
        return telemetryRepository.save(telemetry);
    }

    public List<MachineTelemetry> findByMachineId(Long machineId) {
        return telemetryRepository.findByMachineId(machineId);
    }
}
