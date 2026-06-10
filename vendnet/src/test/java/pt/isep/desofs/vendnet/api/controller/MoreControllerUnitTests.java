package pt.isep.desofs.vendnet.api.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import pt.isep.desofs.vendnet.api.dto.PurchaseRequest;
import pt.isep.desofs.vendnet.api.dto.PurchaseResponse;
import pt.isep.desofs.vendnet.api.dto.TelemetryRequest;
import pt.isep.desofs.vendnet.api.dto.TelemetryResponse;
import pt.isep.desofs.vendnet.api.dto.UserResponse;
import pt.isep.desofs.vendnet.application.service.AuthService;
import pt.isep.desofs.vendnet.application.service.MachineService;
import pt.isep.desofs.vendnet.application.service.ProductService;
import pt.isep.desofs.vendnet.application.service.SaleService;
import pt.isep.desofs.vendnet.application.service.SlotService;
import pt.isep.desofs.vendnet.application.service.UserManagementService;
import pt.isep.desofs.vendnet.application.service.TelemetryService;
import pt.isep.desofs.vendnet.domain.model.machine.VendingMachine;
import pt.isep.desofs.vendnet.domain.model.product.Product;
import pt.isep.desofs.vendnet.domain.model.sale.Sale;
import pt.isep.desofs.vendnet.domain.model.slot.Slot;
import pt.isep.desofs.vendnet.domain.model.telemetry.MachineTelemetry;
import pt.isep.desofs.vendnet.infrastructure.os.BackupResult;
import pt.isep.desofs.vendnet.infrastructure.os.BackupService;
import pt.isep.desofs.vendnet.infrastructure.os.ReportDirectoryService;
import pt.isep.desofs.vendnet.infrastructure.payment.PaymentGatewayService;
import pt.isep.desofs.vendnet.config.BootstrapReadyIndicator;

@ExtendWith(MockitoExtension.class)
class MoreControllerUnitTests {

	@Mock private SaleService saleService;
	@Mock private AuthService authService;
	@Mock private MachineService machineService;
	@Mock private SlotService slotService;
	@Mock private ProductService productService;
	@Mock private PaymentGatewayService paymentGatewayService;
	@Mock private BackupService backupService;
	@Mock private ReportDirectoryService reportDirectoryService;
	@Mock private TelemetryService telemetryService;
	@Mock private BootstrapReadyIndicator bootstrapReadyIndicator;
	@Mock private UserManagementService userManagementService;

	@Test
	void saleController_findByMachine_shouldReturnOk() {
		SaleController controller = new SaleController(saleService, authService);
		when(saleService.findByMachineId(1L)).thenReturn(List.of());
		ResponseEntity<List<Sale>> response = controller.findByMachine(1L);
		assertEquals(HttpStatus.OK, response.getStatusCode());
	}

	@Test
	void slotController_findByMachine_shouldReturnOk() {
		SlotController controller = new SlotController(slotService, authService);
		when(slotService.findByMachineId(1L)).thenReturn(List.of());
		ResponseEntity<List<Slot>> response = controller.findByMachine(1L);
		assertEquals(HttpStatus.OK, response.getStatusCode());
	}

	@Test
	void productController_findAllActive_shouldReturnOk() {
		ProductController controller = new ProductController(productService);
		when(productService.findAllActive()).thenReturn(List.of());
		ResponseEntity<List<Product>> response = controller.findAllActive();
		assertEquals(HttpStatus.OK, response.getStatusCode());
	}

	@Test
	void productController_findBySku_shouldReturnOk() {
		ProductController controller = new ProductController(productService);
		Product p = Product.builder().id(1L).sku("SKU-001").name("Coke").price(new BigDecimal("1.50")).active(true).build();
		when(productService.findBySku("SKU-001")).thenReturn(p);
		ResponseEntity<Product> response = controller.findBySku("SKU-001");
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals("SKU-001", response.getBody().getSku());
	}

	@Test
	void productController_update_shouldReturnOk() {
		ProductController controller = new ProductController(productService);
		Product existing = Product.builder().id(1L).sku("SKU-001").name("Coke").price(new BigDecimal("1.50")).active(true).build();
		when(productService.updateProduct(anyLong(), any(Product.class))).thenReturn(existing);
		ResponseEntity<Product> response = controller.update(1L, new pt.isep.desofs.vendnet.api.dto.UpdateProductRequest());
		assertEquals(HttpStatus.OK, response.getStatusCode());
	}

	@Test
	void paymentWebhook_nullSignature_shouldReturn401() {
		PaymentWebhookController controller = new PaymentWebhookController(paymentGatewayService);
		ResponseEntity<Map<String, String>> response = controller.handleWebhook("body", null);
		assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
	}

	@Test
	void paymentWebhook_invalidSignature_shouldReturn401() {
		PaymentWebhookController controller = new PaymentWebhookController(paymentGatewayService);
		when(paymentGatewayService.verifyWebhookSignature("body", "badsig")).thenReturn(false);
		ResponseEntity<Map<String, String>> response = controller.handleWebhook("body", "badsig");
		assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
	}

	@Test
	void paymentWebhook_validSignature_shouldReturnOk() {
		PaymentWebhookController controller = new PaymentWebhookController(paymentGatewayService);
		when(paymentGatewayService.verifyWebhookSignature("body", "validsig")).thenReturn(true);
		when(paymentGatewayService.parseWebhook("body")).thenReturn(Map.of("event", "completed"));
		ResponseEntity<Map<String, String>> response = controller.handleWebhook("body", "validsig");
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals("ok", response.getBody().get("status"));
	}

	@Test
	void telemetryController_ingest_shouldReturnOk() {
		MachineTelemetryController controller = new MachineTelemetryController(telemetryService);
		TelemetryRequest telemetry = TelemetryRequest.builder()
				.serialNumber("VM-001")
				.temperature(new BigDecimal("22.5"))
				.statusCode("ONLINE")
				.timestamp(LocalDateTime.now())
				.build();
		when(telemetryService.ingest(any(TelemetryRequest.class), anyString()))
				.thenReturn(TelemetryResponse.builder().accepted(true).alertsRaised(0).build());
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setAttribute("X509_CN", "VM-001");
		ResponseEntity<TelemetryResponse> response = controller.ingest(telemetry, request);
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals(0, response.getBody().getAlertsRaised());
	}

	@Test
	void operationsController_backup_shouldReturnOk() {
		OperationsController controller = new OperationsController(backupService, reportDirectoryService);
		when(backupService.generateBackup())
				.thenReturn(
						BackupResult.builder()
								.filename("vendnet_backup.sql.enc")
								.size(128L)
								.checksum("abc")
								.timestamp(LocalDateTime.now())
								.build());
		ResponseEntity<BackupResult> response = controller.triggerBackup();
		assertEquals(HttpStatus.CREATED, response.getStatusCode());
		assertEquals("vendnet_backup.sql.enc", response.getBody().getFilename());
	}

	@Test
	void operationsController_salesReport_shouldReturnOk() {
		OperationsController controller = new OperationsController(backupService, reportDirectoryService);
		when(reportDirectoryService.createReportDirectory("sales")).thenReturn("/var/vendnet/reports/sales/2026/05/17");
		ResponseEntity<Map<String, String>> response = controller.generateSalesReport();
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals("report generated", response.getBody().get("status"));
	}

	@Test
	void healthController_up_shouldReturnOk() {
		HealthController controller = new HealthController();
		org.springframework.test.util.ReflectionTestUtils.setField(controller, "bootstrapReady", bootstrapReadyIndicator);
		when(bootstrapReadyIndicator.isReady()).thenReturn(true);
		ResponseEntity<String> response = controller.health();
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals("UP", response.getBody());
	}

	@Test
	void healthController_seeding_shouldReturn503() {
		HealthController controller = new HealthController();
		org.springframework.test.util.ReflectionTestUtils.setField(controller, "bootstrapReady", bootstrapReadyIndicator);
		when(bootstrapReadyIndicator.isReady()).thenReturn(false);
		ResponseEntity<String> response = controller.health();
		assertEquals(HttpStatus.SERVICE_UNAVAILABLE, response.getStatusCode());
	}

	@Test
	void pingController_ready_shouldReturnOk() {
		PingController controller = new PingController();
		org.springframework.test.util.ReflectionTestUtils.setField(controller, "bootstrapReady", bootstrapReadyIndicator);
		when(bootstrapReadyIndicator.isReady()).thenReturn(true);
		ResponseEntity<Map<String, String>> response = controller.ping();
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals("ok", response.getBody().get("status"));
	}

	@Test
	void pingController_seeding_shouldReturn503() {
		PingController controller = new PingController();
		org.springframework.test.util.ReflectionTestUtils.setField(controller, "bootstrapReady", bootstrapReadyIndicator);
		when(bootstrapReadyIndicator.isReady()).thenReturn(false);
		ResponseEntity<Map<String, String>> response = controller.ping();
		assertEquals(HttpStatus.SERVICE_UNAVAILABLE, response.getStatusCode());
		assertEquals("seeding", response.getBody().get("status"));
	}

	@Test
	void userController_me_shouldReturnOk() {
		UserController controller = new UserController(authService);
		UserResponse userResponse = UserResponse.builder().id(1L).email("test@test.com").name("Test").role("ROLE_CUSTOMER").createdAt(LocalDateTime.now()).build();
		when(authService.getCurrentUser(anyString())).thenReturn(userResponse);
		org.springframework.security.core.Authentication auth = new org.springframework.security.authentication.UsernamePasswordAuthenticationToken("test@test.com", null, java.util.List.of());
		org.springframework.security.core.context.SecurityContextHolder.getContext().setAuthentication(auth);

		try {
			ResponseEntity<UserResponse> response = controller.me();
			assertEquals(HttpStatus.OK, response.getStatusCode());
			assertEquals("test@test.com", response.getBody().getEmail());
		} finally {
			org.springframework.security.core.context.SecurityContextHolder.clearContext();
		}
	}

	@Test
	void userController_claims_shouldReturnOk() {
		UserController controller = new UserController(authService);
		org.springframework.security.core.Authentication auth = new org.springframework.security.authentication.UsernamePasswordAuthenticationToken("test@test.com", null, java.util.List.of(new org.springframework.security.core.authority.SimpleGrantedAuthority("ROLE_CUSTOMER")));
		org.springframework.security.core.context.SecurityContextHolder.getContext().setAuthentication(auth);

		try {
			ResponseEntity<pt.isep.desofs.vendnet.api.dto.ClaimsResponse> response = controller.claims();
			assertEquals(HttpStatus.OK, response.getStatusCode());
			assertEquals("test@test.com", response.getBody().getSubject());
			assertEquals("ROLE_CUSTOMER", response.getBody().getRole());
		} finally {
			org.springframework.security.core.context.SecurityContextHolder.clearContext();
		}
	}
}
