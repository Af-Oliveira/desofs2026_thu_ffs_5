package pt.isep.desofs.vendnet.api.controller;

import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pt.isep.desofs.vendnet.api.dto.CreateMachineRequest;
import pt.isep.desofs.vendnet.api.dto.UpdateMachineRequest;
import pt.isep.desofs.vendnet.application.service.MachineService;
import pt.isep.desofs.vendnet.domain.model.machine.VendingMachine;

@RestController
@RequestMapping("/api/machines")
@RequiredArgsConstructor
public class MachineController {

	private final MachineService machineService;

	@GetMapping
	@PreAuthorize("hasAnyRole('CUSTOMER', 'OPERATOR', 'ADMINISTRATOR')")
	public ResponseEntity<List<VendingMachine>> findAll() {
		return ResponseEntity.ok(machineService.findAll());
	}

	@GetMapping("/{code}")
	@PreAuthorize("hasAnyRole('CUSTOMER', 'OPERATOR', 'ADMINISTRATOR')")
	public ResponseEntity<VendingMachine> findByCode(@PathVariable String code) {
		return ResponseEntity.ok(machineService.findByCode(code));
	}

	@PostMapping
	@PreAuthorize("hasRole('ADMINISTRATOR')")
	public ResponseEntity<VendingMachine> create(@Valid @RequestBody CreateMachineRequest request) {
		VendingMachine machine =
				machineService.createMachine(request.getCode(), request.getLocation());
		return ResponseEntity.status(HttpStatus.CREATED).body(machine);
	}

	@PutMapping("/{id}")
	@PreAuthorize("hasRole('ADMINISTRATOR')")
	public ResponseEntity<VendingMachine> update(
			@PathVariable Long id, @Valid @RequestBody UpdateMachineRequest request) {
		VendingMachine machine =
				machineService.updateMachine(
						id, request.getCode(), request.getLocation(), request.getActive());
		return ResponseEntity.ok(machine);
	}
}
