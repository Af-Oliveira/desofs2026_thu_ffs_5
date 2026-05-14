package pt.isep.desofs.vendnet.api.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pt.isep.desofs.vendnet.application.service.SaleService;
import pt.isep.desofs.vendnet.domain.model.sale.Sale;

@RestController
@RequestMapping("/api/sales")
@RequiredArgsConstructor
public class SaleController {

	private final SaleService saleService;

	@GetMapping("/machine/{machineId}")
	@PreAuthorize("hasAnyRole('OPERATOR', 'ADMINISTRATOR')")
	public ResponseEntity<List<Sale>> findByMachine(@PathVariable Long machineId) {
		return ResponseEntity.ok(saleService.findByMachineId(machineId));
	}
}
