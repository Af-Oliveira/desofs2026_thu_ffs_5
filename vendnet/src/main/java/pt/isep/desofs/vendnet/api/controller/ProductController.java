package pt.isep.desofs.vendnet.api.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import pt.isep.desofs.vendnet.domain.model.product.Product;
import pt.isep.desofs.vendnet.application.service.ProductService;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<Product>> findAllActive() {
        return ResponseEntity.ok(productService.findAllActive());
    }

    @GetMapping("/{sku}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Product> findBySku(@PathVariable String sku) {
        return ResponseEntity.ok(productService.findBySku(sku));
    }

    @PostMapping(consumes = {"multipart/form-data"})
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    public ResponseEntity<Product> createWithImage(
            @RequestParam("name") @NotBlank String name,
            @RequestParam("description") String description,
            @RequestParam("price") @NotNull BigDecimal price,
            @RequestParam("sku") @NotBlank String sku,
            @RequestParam("image") MultipartFile image) {

        Product product = productService.createProduct(name, description, price, sku, image);
        return ResponseEntity.ok(product);
    }
}
