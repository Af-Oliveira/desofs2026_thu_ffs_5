package pt.isep.desofs.vendnet.api.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
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
import pt.isep.desofs.vendnet.api.dto.CreateMachineRequest;
import pt.isep.desofs.vendnet.api.dto.CreateUserRequest;
import pt.isep.desofs.vendnet.api.dto.MachineResponse;
import pt.isep.desofs.vendnet.api.dto.UpdateUserRequest;
import pt.isep.desofs.vendnet.api.dto.UserResponse;
import pt.isep.desofs.vendnet.application.service.MachineService;
import pt.isep.desofs.vendnet.application.service.SaleService;
import pt.isep.desofs.vendnet.application.service.SlotService;
import pt.isep.desofs.vendnet.application.service.UserManagementService;
import pt.isep.desofs.vendnet.domain.model.machine.VendingMachine;

@ExtendWith(MockitoExtension.class)
class ControllerUnitTests {

	@Mock private UserManagementService userManagementService;
	@Mock private MachineService machineService;
	@Mock private SlotService slotService;
	@Mock private SaleService saleService;

	private AdminController adminController;
	private MachineController machineController;
	private SlotController slotController;
	private PublicController publicController;
	private OperationsController operationsController;

	@BeforeEach
	void setUp() {
		adminController = new AdminController(userManagementService);
		machineController = new MachineController(machineService);
		publicController = new PublicController();
	}

	@Test
	void adminDashboard_shouldReturnMap() {
		when(userManagementService.getDashboard()).thenReturn(Map.of("message", "Welcome", "totalUsers", 5L));
		ResponseEntity<Map<String, Object>> response = adminController.dashboard();
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertNotNull(response.getBody());
		assertEquals("Welcome", response.getBody().get("message"));
	}

	@Test
	void adminListUsers_shouldReturnList() {
		when(userManagementService.listUsers()).thenReturn(List.of());
		ResponseEntity<List<UserResponse>> response = adminController.listUsers();
		assertEquals(HttpStatus.OK, response.getStatusCode());
	}

	@Test
	void adminCreateUser_shouldReturnCreated() {
		LocalDateTime now = LocalDateTime.now();
		when(userManagementService.createUser(anyString(), anyString(), anyString(), anyString()))
				.thenReturn(UserResponse.builder().id(1L).email("test@test.com").name("Test").role("ROLE_CUSTOMER").createdAt(now).build());
		CreateUserRequest request = new CreateUserRequest("test@test.com", "Pass@1234", "Test", "ROLE_CUSTOMER");
		ResponseEntity<UserResponse> response = adminController.createUser(request);
		assertEquals(HttpStatus.CREATED, response.getStatusCode());
		assertEquals("test@test.com", response.getBody().getEmail());
	}

	@Test
	void adminUpdateUser_shouldReturnOk() {
		LocalDateTime now = LocalDateTime.now();
		when(userManagementService.updateUser(anyLong(), any(), any(), any()))
				.thenReturn(UserResponse.builder().id(1L).email("test@test.com").name("Updated").role("ROLE_OPERATOR").createdAt(now).build());
		UpdateUserRequest request = new UpdateUserRequest("Updated", "ROLE_OPERATOR", "ACTIVE");
		ResponseEntity<UserResponse> response = adminController.updateUser(1L, request);
		assertEquals(HttpStatus.OK, response.getStatusCode());
	}

	@Test
	void adminReports_shouldReturnOk() {
		ResponseEntity<Map<String, String>> response = adminController.reports();
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertTrue(response.getBody().containsKey("message"));
	}

	@Test
	void machineFindAll_shouldReturnOk() {
		when(machineService.findAll()).thenReturn(List.of());
		ResponseEntity<List<VendingMachine>> response = machineController.findAll();
		assertEquals(HttpStatus.OK, response.getStatusCode());
	}

	@Test
	void machineFindByCode_shouldReturnOk() {
		VendingMachine machine = VendingMachine.builder().id(1L).code("VM-001").location("Lisbon").active(true).build();
		when(machineService.findByCode("VM-001")).thenReturn(machine);
		ResponseEntity<VendingMachine> response = machineController.findByCode("VM-001");
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals("VM-001", response.getBody().getCode());
	}

	@Test
	void machineCreate_shouldReturnCreated() {
		VendingMachine machine = VendingMachine.builder().id(1L).code("VM-NEW").location("Porto").active(true).build();
		when(machineService.createMachine("VM-NEW", "Porto")).thenReturn(machine);
		CreateMachineRequest request = new CreateMachineRequest("VM-NEW", "Porto");
		ResponseEntity<VendingMachine> response = machineController.create(request);
		assertEquals(HttpStatus.CREATED, response.getStatusCode());
	}

	@Test
	void publicInfo_shouldReturnInfo() {
		ResponseEntity<Map<String, String>> response = publicController.info();
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals("VendNet", response.getBody().get("app"));
	}
}