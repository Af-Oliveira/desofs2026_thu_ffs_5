package pt.isep.desofs.vendnet.application.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import pt.isep.desofs.vendnet.domain.model.telemetry.MachineTelemetry;
import pt.isep.desofs.vendnet.domain.repository.TelemetryRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class TelemetryService {

	private final TelemetryRepository telemetryRepository;

	@PreAuthorize("permitAll()")
	public MachineTelemetry save(MachineTelemetry telemetry) {
		return telemetryRepository.save(telemetry);
	}

	@PreAuthorize("hasAnyRole('OPERATOR', 'ADMINISTRATOR')")
	public List<MachineTelemetry> findByMachineId(Long machineId) {
		return telemetryRepository.findByMachineId(machineId);
	}
}
