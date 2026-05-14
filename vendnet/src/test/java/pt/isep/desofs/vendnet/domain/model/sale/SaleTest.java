package pt.isep.desofs.vendnet.domain.model.sale;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import pt.isep.desofs.vendnet.domain.model.machine.VendingMachine;
import pt.isep.desofs.vendnet.domain.model.product.Product;

class SaleTest {

	@Test
	void builder_shouldCreateSaleWithAllFields() {
		LocalDateTime now = LocalDateTime.now();
		VendingMachine machine = VendingMachine.builder().id(1L).code("VM-001").build();
		Product product = Product.builder().id(1L).sku("SKU-001").build();

		Sale sale = Sale.builder()
				.id(1L)
				.machine(machine)
				.product(product)
				.price(new BigDecimal("2.50"))
				.quantity(3)
				.saleDate(now)
				.createdAt(now)
				.build();

		assertEquals(1L, sale.getId());
		assertEquals(machine, sale.getMachine());
		assertEquals(product, sale.getProduct());
		assertEquals(new BigDecimal("2.50"), sale.getPrice());
		assertEquals(3, sale.getQuantity());
		assertEquals(now, sale.getSaleDate());
		assertEquals(now, sale.getCreatedAt());
	}

	@Test
	void setters_shouldModifyFields() {
		Sale sale = new Sale();
		VendingMachine machine = VendingMachine.builder().id(1L).code("VM-001").build();
		Product product = Product.builder().id(1L).sku("SKU-001").build();
		LocalDateTime now = LocalDateTime.now();

		sale.setId(2L);
		sale.setMachine(machine);
		sale.setProduct(product);
		sale.setPrice(new BigDecimal("3.99"));
		sale.setQuantity(5);
		sale.setSaleDate(now);
		sale.setCreatedAt(now);

		assertEquals(2L, sale.getId());
		assertNotNull(sale.getMachine());
		assertNotNull(sale.getProduct());
		assertEquals(new BigDecimal("3.99"), sale.getPrice());
		assertEquals(5, sale.getQuantity());
		assertEquals(now, sale.getSaleDate());
		assertEquals(now, sale.getCreatedAt());
	}

	@Test
	void noArgsConstructor_shouldCreateEmptySale() {
		Sale sale = new Sale();

		assertNull(sale.getId());
		assertNull(sale.getMachine());
		assertNull(sale.getProduct());
		assertNull(sale.getPrice());
		assertEquals(0, sale.getQuantity());
		assertNull(sale.getSaleDate());
		assertNull(sale.getCreatedAt());
	}

	@Test
	void allArgsConstructor_shouldCreateSale() {
		LocalDateTime now = LocalDateTime.now();
		VendingMachine machine = VendingMachine.builder().id(1L).code("VM-001").build();
		Product product = Product.builder().id(1L).sku("SKU-001").build();

		Sale sale = new Sale(
				3L, machine, product, new BigDecimal("1.00"), 1, now, now);

		assertEquals(3L, sale.getId());
		assertEquals(machine, sale.getMachine());
		assertEquals(product, sale.getProduct());
		assertEquals(new BigDecimal("1.00"), sale.getPrice());
		assertEquals(1, sale.getQuantity());
		assertEquals(now, sale.getSaleDate());
		assertEquals(now, sale.getCreatedAt());
	}
}
