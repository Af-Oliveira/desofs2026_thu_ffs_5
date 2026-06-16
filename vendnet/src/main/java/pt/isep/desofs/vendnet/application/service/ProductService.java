package pt.isep.desofs.vendnet.application.service;

import java.io.IOException;
import java.math.BigDecimal;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.HexFormat;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import pt.isep.desofs.vendnet.domain.model.product.Product;
import pt.isep.desofs.vendnet.domain.repository.ProductRepository;
import pt.isep.desofs.vendnet.infrastructure.file.FileStorageService;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductService {

	private final ProductRepository productRepository;
	private final FileStorageService fileStorageService;

	@PreAuthorize("hasAnyRole('CUSTOMER', 'OPERATOR', 'ADMINISTRATOR')")
	public List<Product> findAllActive() {
		return productRepository.findAllByActiveTrue();
	}

	@PreAuthorize("hasAnyRole('CUSTOMER', 'OPERATOR', 'ADMINISTRATOR')")
	public Product findBySku(String sku) {
		return productRepository
				.findBySku(sku)
				.orElseThrow(() -> new IllegalArgumentException("Product not found: " + sku));
	}

	@PreAuthorize("hasRole('ADMINISTRATOR')")
	public Product createProduct(
			String name, String description, BigDecimal price, String sku, MultipartFile image) {
		return createProduct(name, description, price, "EUR", "GENERAL", sku, image);
	}

	@PreAuthorize("hasRole('ADMINISTRATOR')")
	public Product createProduct(
			String name,
			String description,
			BigDecimal price,
			String currency,
			String category,
			String sku,
			MultipartFile image) {
		if (name == null || name.isBlank() || name.length() > 100 || containsHtml(name)) {
			throw new IllegalArgumentException("Invalid product name");
		}
		if (description != null && description.length() > 500) {
			throw new IllegalArgumentException("Invalid product description");
		}
		if (price == null || price.compareTo(BigDecimal.ZERO) <= 0 || price.scale() > 2) {
			throw new IllegalArgumentException("Invalid product price");
		}
		if (currency == null || !currency.matches("^[A-Z]{3}$")) {
			throw new IllegalArgumentException("Invalid currency");
		}
		if (category == null || !category.matches("^[A-Z_]{2,50}$")) {
			throw new IllegalArgumentException("Invalid category");
		}
			String imageUrl = null;
			String imageChecksum = null;
			if (image != null && !image.isEmpty()) {
				imageChecksum = checksum(image);
				imageUrl = fileStorageService.store(image, "products");
			}

		LocalDateTime now = LocalDateTime.now();
		Product product =
				Product.builder()
						.name(name)
							.description(description)
							.price(price)
							.sku(sku)
							.currency(currency)
							.category(category)
							.imageUrl(imageUrl)
							.imageChecksum(imageChecksum)
							.active(true)
						.createdAt(now)
						.updatedAt(now)
						.build();

			return productRepository.save(product);
		}

	private boolean containsHtml(String value) {
		String lower = value.toLowerCase();
		return lower.contains("<script") || lower.contains("</") || lower.contains(">");
	}

	private String checksum(MultipartFile image) {
		try {
			return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(image.getBytes()));
		} catch (NoSuchAlgorithmException | IOException e) {
			throw new IllegalArgumentException("Unable to checksum image", e);
		}
	}

	@PreAuthorize("hasRole('ADMINISTRATOR')")
	public Product updateProduct(Long id, Product updated) {
		Product existing =
				productRepository
						.findById(id)
						.orElseThrow(
								() -> new IllegalArgumentException("Product not found: " + id));

		if (updated.getName() != null) {
			existing.setName(updated.getName());
		}
		if (updated.getDescription() != null) {
			existing.setDescription(updated.getDescription());
		}
		if (updated.getPrice() != null) {
			existing.setPrice(updated.getPrice());
		}
		if (updated.getSku() != null) {
			existing.setSku(updated.getSku());
		}
		if (updated.getImageUrl() != null) {
			existing.setImageUrl(updated.getImageUrl());
		}
		existing.setActive(updated.isActive());

		existing.setUpdatedAt(LocalDateTime.now());
		return productRepository.save(existing);
	}
}
