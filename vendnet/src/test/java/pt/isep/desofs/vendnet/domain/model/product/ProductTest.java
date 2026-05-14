package pt.isep.desofs.vendnet.domain.model.product;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;

class ProductTest {

	@Test
	void builder_shouldCreateProductWithAllFields() {
		LocalDateTime now = LocalDateTime.now();

		Product product = Product.builder()
				.id(1L)
				.name("Coca-Cola")
				.description("Refrigerante 330ml")
				.price(new BigDecimal("1.50"))
				.sku("COKE-001")
				.imageUrl("/images/coke.jpg")
				.active(true)
				.createdAt(now)
				.updatedAt(now)
				.build();

		assertEquals(1L, product.getId());
		assertEquals("Coca-Cola", product.getName());
		assertEquals("Refrigerante 330ml", product.getDescription());
		assertEquals(new BigDecimal("1.50"), product.getPrice());
		assertEquals("COKE-001", product.getSku());
		assertEquals("/images/coke.jpg", product.getImageUrl());
		assertTrue(product.isActive());
		assertEquals(now, product.getCreatedAt());
		assertEquals(now, product.getUpdatedAt());
	}

	@Test
	void setters_shouldModifyFields() {
		Product product = new Product();
		LocalDateTime now = LocalDateTime.now();

		product.setId(2L);
		product.setName("Pepsi");
		product.setDescription("Refrigerante 500ml");
		product.setPrice(new BigDecimal("2.00"));
		product.setSku("PEPSI-001");
		product.setImageUrl("/images/pepsi.jpg");
		product.setActive(false);
		product.setCreatedAt(now);
		product.setUpdatedAt(now);

		assertEquals(2L, product.getId());
		assertEquals("Pepsi", product.getName());
		assertEquals("Refrigerante 500ml", product.getDescription());
		assertEquals(new BigDecimal("2.00"), product.getPrice());
		assertEquals("PEPSI-001", product.getSku());
		assertEquals("/images/pepsi.jpg", product.getImageUrl());
		assertFalse(product.isActive());
		assertEquals(now, product.getCreatedAt());
		assertEquals(now, product.getUpdatedAt());
	}

	@Test
	void noArgsConstructor_shouldCreateEmptyProduct() {
		Product product = new Product();

		assertNull(product.getId());
		assertNull(product.getName());
		assertNull(product.getDescription());
		assertNull(product.getPrice());
		assertNull(product.getSku());
		assertNull(product.getImageUrl());
		assertFalse(product.isActive());
		assertNull(product.getCreatedAt());
		assertNull(product.getUpdatedAt());
	}

	@Test
	void allArgsConstructor_shouldCreateProduct() {
		LocalDateTime now = LocalDateTime.now();

		Product product = new Product(
				3L, "Fanta", "Laranja 330ml", new BigDecimal("1.20"),
				"FANTA-001", "/images/fanta.jpg", true, now, now);

		assertEquals(3L, product.getId());
		assertEquals("Fanta", product.getName());
		assertEquals("Laranja 330ml", product.getDescription());
		assertEquals(new BigDecimal("1.20"), product.getPrice());
		assertEquals("FANTA-001", product.getSku());
		assertEquals("/images/fanta.jpg", product.getImageUrl());
		assertTrue(product.isActive());
		assertEquals(now, product.getCreatedAt());
		assertEquals(now, product.getUpdatedAt());
	}

	@Test
	void price_shouldSupportBigDecimal() {
		Product product = Product.builder()
				.name("Test")
				.sku("TEST-001")
				.price(new BigDecimal("9.99"))
				.active(true)
				.createdAt(LocalDateTime.now())
				.updatedAt(LocalDateTime.now())
				.build();

		assertEquals(new BigDecimal("9.99"), product.getPrice());
	}
}
