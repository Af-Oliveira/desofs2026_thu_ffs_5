package pt.isep.desofs.vendnet.application.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
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
	void findAllActive_shouldReturnList() {
		Product product = Product.builder()
				.id(1L)
				.sku("SKU-001")
				.name("Coke")
				.active(true)
				.build();
		when(productRepository.findAllByActiveTrue()).thenReturn(List.of(product));

		List<Product> result = productService.findAllActive();

		assertEquals(1, result.size());
		assertEquals("SKU-001", result.get(0).getSku());
	}

	@Test
	void findAllActive_shouldReturnEmptyList() {
		when(productRepository.findAllByActiveTrue()).thenReturn(Collections.emptyList());

		List<Product> result = productService.findAllActive();

		assertTrue(result.isEmpty());
	}

	@Test
	void findBySku_shouldReturnProduct() {
		Product product = Product.builder()
				.id(1L)
				.sku("SKU-001")
				.name("Coke")
				.build();
		when(productRepository.findBySku("SKU-001")).thenReturn(Optional.of(product));

		Product result = productService.findBySku("SKU-001");

		assertEquals("SKU-001", result.getSku());
		assertEquals("Coke", result.getName());
	}

	@Test
	void findBySku_shouldThrowWhenNotFound() {
		when(productRepository.findBySku("UNKNOWN")).thenReturn(Optional.empty());

		assertThrows(IllegalArgumentException.class, () -> productService.findBySku("UNKNOWN"));
	}

	@Test
	void createProduct_shouldSaveAndReturnProduct() {
		Product product = Product.builder()
				.id(1L)
				.name("Coke")
				.sku("SKU-001")
				.price(new BigDecimal("1.50"))
				.build();
		when(productRepository.save(any(Product.class))).thenReturn(product);

		Product result = productService.createProduct("Coke", "Refrigerante", new BigDecimal("1.50"), "SKU-001", null);

		assertEquals("SKU-001", result.getSku());
		assertEquals(new BigDecimal("1.50"), result.getPrice());
		verify(productRepository).save(any(Product.class));
	}

	@Test
	void createProduct_withImage_shouldStoreFileAndSave() {
		MultipartFile image = new org.springframework.mock.web.MockMultipartFile(
				"image", "coke.jpg", "image/jpeg", "fake-image-content".getBytes());
		Product product = Product.builder()
				.id(1L)
				.name("Coke")
				.sku("SKU-001")
				.price(new BigDecimal("1.50"))
				.imageUrl("/products/coke.jpg")
				.build();
		when(fileStorageService.store(any(MultipartFile.class), any(String.class))).thenReturn("/products/coke.jpg");
		when(productRepository.save(any(Product.class))).thenReturn(product);

		Product result = productService.createProduct("Coke", "Desc", new BigDecimal("1.50"), "SKU-001", image);

		assertEquals("/products/coke.jpg", result.getImageUrl());
		verify(fileStorageService).store(any(MultipartFile.class), any(String.class));
	}

	@Test
	void createProduct_withEmptyImage_shouldNotStoreFile() {
		MultipartFile emptyImage = new org.springframework.mock.web.MockMultipartFile(
				"image", "", "image/jpeg", new byte[0]);
		Product product = Product.builder()
				.id(1L)
				.name("Coke")
				.sku("SKU-001")
				.price(new BigDecimal("1.50"))
				.build();
		when(productRepository.save(any(Product.class))).thenReturn(product);

		Product result = productService.createProduct("Coke", "Desc", new BigDecimal("1.50"), "SKU-001", emptyImage);

		assertNotNull(result);
		verify(productRepository).save(any(Product.class));
	}
}
