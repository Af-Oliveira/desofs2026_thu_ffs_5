package pt.isep.desofs.vendnet.application.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pt.isep.desofs.vendnet.domain.model.audit.AuditLog;
import pt.isep.desofs.vendnet.domain.model.machine.VendingMachine;
import pt.isep.desofs.vendnet.domain.model.slot.Slot;
import pt.isep.desofs.vendnet.domain.repository.AuditLogRepository;
import pt.isep.desofs.vendnet.domain.repository.SlotRepository;
import pt.isep.desofs.vendnet.domain.repository.VendingMachineRepository;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class SlotService {

	private final SlotRepository slotRepository;
	private final VendingMachineRepository machineRepository;
	private final AuditLogRepository auditLogRepository;

	@PreAuthorize("hasAnyRole('OPERATOR', 'ADMINISTRATOR')")
	public List<Slot> findByMachineId(Long machineId) {
		return slotRepository.findByMachineId(machineId);
	}

	@PreAuthorize("hasAnyRole('OPERATOR', 'ADMINISTRATOR')")
	public Slot restock(Long machineId, Long slotId, int quantity, Long operatorId) {
		VendingMachine machine = machineRepository
				.findById(machineId)
				.orElseThrow(
						() -> new IllegalArgumentException(
								"Machine not found: " + machineId));

		machine.checkStatus();

		Slot slot = slotRepository
				.findByMachineIdAndId(machineId, slotId)
				.orElseThrow(
						() -> new IllegalArgumentException(
								"Slot not found: "
										+ slotId
										+ " for machine "
										+ machineId));

		slot.addStock(quantity);
		slot.setUpdatedAt(LocalDateTime.now());

		Slot saved = slotRepository.save(slot);

		AuditLog audit = AuditLog.builder()
				.eventType("RESTOCK")
				.principal(String.valueOf(operatorId))
				.details("Restocked slot " + slotId + " in machine " + machineId)
				.resource("Slot")
				.action("RESTOCK")
				.outcome("SUCCESS")
				.timestamp(LocalDateTime.now())
				.build();
		auditLogRepository.save(audit);

		log.info(
				"Slot restocked: machine={}, slot={}, position={}, newStock={}/{}",
				machineId,
				slotId,
				saved.getPosition(),
				saved.getCurrentStock(),
				saved.getCapacity());

		return saved;
	}
}
