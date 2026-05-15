package pt.isep.desofs.vendnet.api.controller;

import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pt.isep.desofs.vendnet.api.dto.RestockRequest;
import pt.isep.desofs.vendnet.application.service.AuthService;
import pt.isep.desofs.vendnet.application.service.SlotService;
import pt.isep.desofs.vendnet.domain.model.slot.Slot;

@RestController
@RequestMapping("/api/machines/{machineId}/slots")
@RequiredArgsConstructor
public class SlotController {

	private final SlotService slotService;
	private final AuthService authService;

	@GetMapping
	@PreAuthorize("hasAnyRole('OPERATOR', 'ADMINISTRATOR')")
	public ResponseEntity<List<Slot>> findByMachine(@PathVariable Long machineId) {
		return ResponseEntity.ok(slotService.findByMachineId(machineId));
	}

	@PutMapping("/{slotId}/restock")
	@PreAuthorize("hasAnyRole('OPERATOR', 'ADMINISTRATOR')")
	public ResponseEntity<Slot> restock(
			@PathVariable Long machineId,
			@PathVariable Long slotId,
			@Valid @RequestBody RestockRequest request) {

		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		var user = authService.getCurrentUser(auth.getName());

		Slot updated = slotService.restock(machineId, slotId, request.getQuantity(), user.getId());
		return ResponseEntity.ok(updated);
	}
}
