package pt.isep.desofs.vendnet.api.controller;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import pt.isep.desofs.vendnet.application.service.ProductService;
import pt.isep.desofs.vendnet.domain.model.product.Product;

@RestController
@RequestMapping("/api/admin/products")
@RequiredArgsConstructor
public class AdminProductController {

	private final ProductService productService;

	@PostMapping(consumes = {"multipart/form-data"})
	@PreAuthorize("hasRole('ADMINISTRATOR')")
	public ResponseEntity<Product> createWithImage(
			@RequestParam("name") @NotBlank String name,
			@RequestParam("description") String description,
			@RequestParam("price") @NotNull BigDecimal price,
			@RequestParam("currency") @NotBlank String currency,
			@RequestParam("category") @NotBlank String category,
			@RequestParam("sku") @NotBlank String sku,
			@RequestParam("image") MultipartFile image) {
		Product product =
				productService.createProduct(name, description, price, currency, category, sku, image);
		return ResponseEntity.status(HttpStatus.CREATED).body(product);
	}
}
