package pt.isep.desofs.vendnet.application.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;
import pt.isep.desofs.vendnet.domain.model.product.Product;
import pt.isep.desofs.vendnet.domain.repository.ProductRepository;
import pt.isep.desofs.vendnet.infrastructure.file.FileStorageService;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

	@Mock
	private ProductRepository productRepository;

	@Mock
	private FileStorageService fileStorageService;

	private ProductService productService;

	@BeforeEach
	void setUp() {
		productService = new ProductService(productRepository, fileStorageService);
	}

	@Test
	void findAllActive_shouldReturnActiveProducts() {
		Product p = Product.builder().id(1L).sku("SKU-001").name("Coke").active(true)
				.price(new BigDecimal("1.50")).createdAt(LocalDateTime.now()).updatedAt(LocalDateTime.now()).build();
		when(productRepository.findAllByActiveTrue()).thenReturn(java.util.List.of(p));
		assertEquals(1, productService.findAllActive().size());
	}

	@Test
	void findBySku_shouldReturnProduct() {
		Product p = Product.builder().id(1L).sku("SKU-001").name("Coke").build();
		when(productRepository.findBySku("SKU-001")).thenReturn(Optional.of(p));
		assertEquals("SKU-001", productService.findBySku("SKU-001").getSku());
	}

	@Test
	void findBySku_shouldThrowWhenNotFound() {
		when(productRepository.findBySku("UNKNOWN")).thenReturn(Optional.empty());
		assertThrows(IllegalArgumentException.class, () -> productService.findBySku("UNKNOWN"));
	}

	@Test
	void createProduct_withoutImage_shouldSaveProduct() {
		Product saved = Product.builder().id(1L).name("Coke").description("Soda").price(new BigDecimal("1.50"))
				.sku("SKU-001").active(true).createdAt(LocalDateTime.now()).updatedAt(LocalDateTime.now()).build();
		when(productRepository.save(any(Product.class))).thenReturn(saved);
		Product result = productService.createProduct("Coke", "Soda", new BigDecimal("1.50"), "SKU-001", null);
		assertNotNull(result);
		verify(productRepository).save(any(Product.class));
		verify(fileStorageService, never()).store(any(), anyString());
	}

	@Test
	void createProduct_withImage_shouldStoreFileAndSaveProduct() {
		MultipartFile image = new org.springframework.mock.web.MockMultipartFile("image", "coke.jpg", "image/jpeg", "data".getBytes());
		Product saved = Product.builder().id(1L).name("Coke").sku("SKU-001").imageUrl("/products/coke.jpg")
				.price(new BigDecimal("1.50")).active(true).createdAt(LocalDateTime.now()).updatedAt(LocalDateTime.now()).build();
		when(fileStorageService.store(any(MultipartFile.class), anyString())).thenReturn("/products/coke.jpg");
		when(productRepository.save(any(Product.class))).thenReturn(saved);
		Product result = productService.createProduct("Coke", "Soda", new BigDecimal("1.50"), "SKU-001", image);
		assertNotNull(result);
		verify(fileStorageService).store(any(MultipartFile.class), anyString());
	}

	@Test
	void updateProduct_shouldUpdateAllFields() {
		Product existing = Product.builder().id(1L).name("Coke").description("Soda").price(new BigDecimal("1.50"))
				.sku("SKU-001").active(true).createdAt(LocalDateTime.now()).updatedAt(LocalDateTime.now()).build();
		Product updated = Product.builder().name("Pepsi").description("Cola").price(new BigDecimal("2.00")).active(false).build();
		when(productRepository.findById(1L)).thenReturn(Optional.of(existing));
		when(productRepository.save(any(Product.class))).thenAnswer(inv -> inv.getArgument(0));
		Product result = productService.updateProduct(1L, updated);
		assertEquals("Pepsi", result.getName());
		assertEquals(new BigDecimal("2.00"), result.getPrice());
		assertEquals(false, result.isActive());
	}

	@Test
	void updateProduct_shouldPartialUpdate() {
		Product existing = Product.builder().id(1L).name("Coke").description("Soda").price(new BigDecimal("1.50"))
				.sku("SKU-001").active(true).createdAt(LocalDateTime.now()).updatedAt(LocalDateTime.now()).build();
		Product updated = Product.builder().name("Pepsi").build();
		when(productRepository.findById(1L)).thenReturn(Optional.of(existing));
		when(productRepository.save(any(Product.class))).thenAnswer(inv -> inv.getArgument(0));
		Product result = productService.updateProduct(1L, updated);
		assertEquals("Pepsi", result.getName());
	}

	@Test
	void updateProduct_shouldThrowWhenNotFound() {
		when(productRepository.findById(999L)).thenReturn(Optional.empty());
		Product update = new Product();
		assertThrows(IllegalArgumentException.class, () -> productService.updateProduct(999L, update));
	}

	@Test
	void createProduct_invalidName_shouldThrow() {
		BigDecimal price = new BigDecimal("1.00");
		String oversizedName = "a".repeat(101);

		assertInvalidProduct(null, "desc", price, "EUR", "GENERAL", "SKU-1");
		assertInvalidProduct("   ", "desc", price, "EUR", "GENERAL", "SKU-2");
		assertInvalidProduct(oversizedName, "desc", price, "EUR", "GENERAL", "SKU-3");
		assertInvalidProduct("<script>alert(1)</script>", "desc", price, "EUR", "GENERAL", "SKU-4");
	}

	@Test
	void createProduct_invalidDescription_shouldThrow() {
		BigDecimal price = new BigDecimal("1.00");
		String oversizedDescription = "x".repeat(501);

		assertInvalidProduct("Coke", oversizedDescription, price, "EUR", "GENERAL", "SKU-5");
	}

	@Test
	void createProduct_invalidPrice_shouldThrow() {
		BigDecimal tooPrecisePrice = new BigDecimal("1.999");

		assertInvalidProduct("Coke", "desc", null, "EUR", "GENERAL", "SKU-6");
		assertInvalidProduct("Coke", "desc", BigDecimal.ZERO, "EUR", "GENERAL", "SKU-7");
		assertInvalidProduct("Coke", "desc", tooPrecisePrice, "EUR", "GENERAL", "SKU-8");
	}

	@Test
	void createProduct_invalidCurrencyOrCategory_shouldThrow() {
		BigDecimal price = new BigDecimal("1.00");

		assertInvalidProduct("Coke", "desc", price, "EURO", "GENERAL", "SKU-9");
		assertInvalidProduct("Coke", "desc", price, "EUR", "bad", "SKU-10");
	}

	@Test
	void updateProduct_shouldUpdateOptionalFieldsWhenProvided() {
		Product existing =
				Product.builder()
						.id(1L)
						.name("Coke")
						.description("Soda")
						.price(new BigDecimal("1.50"))
						.sku("SKU-001")
						.imageUrl("/old.png")
						.active(true)
						.createdAt(LocalDateTime.now())
						.updatedAt(LocalDateTime.now())
						.build();
		Product updated =
				Product.builder()
						.description("New desc")
						.sku("SKU-002")
						.imageUrl("/new.png")
						.active(false)
						.build();
		when(productRepository.findById(1L)).thenReturn(Optional.of(existing));
		when(productRepository.save(any(Product.class))).thenAnswer(inv -> inv.getArgument(0));

		Product result = productService.updateProduct(1L, updated);

		assertEquals("New desc", result.getDescription());
		assertEquals("SKU-002", result.getSku());
		assertEquals("/new.png", result.getImageUrl());
		assertEquals(false, result.isActive());
	}

	private void assertInvalidProduct(
			String name,
			String description,
			BigDecimal price,
			String currency,
			String category,
			String sku) {
		assertThrows(
				IllegalArgumentException.class,
				() -> productService.createProduct(name, description, price, currency, category, sku, null));
	}
}
