package pt.isep.desofs.vendnet.api.controller;

import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pt.isep.desofs.vendnet.api.dto.CustomerPurchaseRequest;
import pt.isep.desofs.vendnet.application.service.CustomerPurchaseService;
import pt.isep.desofs.vendnet.domain.model.sale.Sale;

@RestController
@RequestMapping("/api/customer")
@RequiredArgsConstructor
public class CustomerPurchaseController {

	private final CustomerPurchaseService customerPurchaseService;

	@PostMapping("/purchases")
	@PreAuthorize("hasRole('CUSTOMER')")
	public ResponseEntity<Sale> purchase(Authentication auth, @Valid @RequestBody CustomerPurchaseRequest request) {
		Sale sale = customerPurchaseService.purchase(auth.getName(), request);
		return ResponseEntity.status(HttpStatus.CREATED).body(sale);
	}

	@GetMapping("/purchases")
	@PreAuthorize("hasRole('CUSTOMER')")
	public ResponseEntity<List<Sale>> myPurchases(Authentication auth) {
		return ResponseEntity.ok(customerPurchaseService.findMyPurchases(auth.getName()));
	}
}
