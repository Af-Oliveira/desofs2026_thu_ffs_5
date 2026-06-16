package pt.isep.desofs.vendnet.api.controller;

import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pt.isep.desofs.vendnet.api.dto.PurchaseRequest;
import pt.isep.desofs.vendnet.api.dto.PurchaseResponse;
import pt.isep.desofs.vendnet.application.service.AuthService;
import pt.isep.desofs.vendnet.application.service.SaleService;
import pt.isep.desofs.vendnet.domain.model.sale.Sale;

@RestController
@RequestMapping("/api/sales")
@RequiredArgsConstructor
public class SaleController {

	private final SaleService saleService;
	private final AuthService authService;

	@GetMapping("/machine/{machineId}")
	@PreAuthorize("hasAnyRole('OPERATOR', 'ADMINISTRATOR')")
	public ResponseEntity<List<Sale>> findByMachine(@PathVariable Long machineId) {
		return ResponseEntity.ok(saleService.findByMachineId(machineId));
	}

	@GetMapping("/me")
	@PreAuthorize("hasRole('CUSTOMER')")
	public ResponseEntity<List<Sale>> findMySales() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		var userResponse = authService.getCurrentUser(auth.getName());
		return ResponseEntity.ok(saleService.findByUserId(userResponse.getId()));
	}

	@PostMapping("/purchase")
	@PreAuthorize("hasRole('CUSTOMER')")
	public ResponseEntity<PurchaseResponse> purchase(
			@Valid @RequestBody PurchaseRequest request) {
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			var userResponse = authService.getCurrentUser(auth.getName());
			PurchaseResponse response = saleService.purchase(request, userResponse.getId());
			if ("DUPLICATE".equals(response.getStatus())) {
				return ResponseEntity.ok(response);
			}
			if ("PENDING_VERIFICATION".equals(response.getStatus())) {
				return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
			}
			return ResponseEntity.status(HttpStatus.CREATED).body(response);
		}
	}
