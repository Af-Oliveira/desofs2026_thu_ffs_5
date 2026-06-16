package pt.isep.desofs.vendnet.application.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.security.crypto.password.PasswordEncoder;
import pt.isep.desofs.vendnet.domain.model.machine.VendingMachine;
import pt.isep.desofs.vendnet.domain.model.product.Product;
import pt.isep.desofs.vendnet.domain.model.telemetry.MachineTelemetry;
import pt.isep.desofs.vendnet.domain.model.user.User;
import pt.isep.desofs.vendnet.domain.repository.ProductRepository;
import pt.isep.desofs.vendnet.domain.repository.SaleRepository;
import pt.isep.desofs.vendnet.domain.repository.SlotRepository;
import pt.isep.desofs.vendnet.domain.repository.TelemetryRepository;
import pt.isep.desofs.vendnet.domain.repository.UserRepository;
import pt.isep.desofs.vendnet.domain.repository.VendingMachineRepository;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class BootstrapServiceTest {

	@Mock private UserRepository userRepository;
	@Mock private ProductRepository productRepository;
	@Mock private VendingMachineRepository machineRepository;
	@Mock private SlotRepository slotRepository;
	@Mock private SaleRepository saleRepository;
	@Mock private TelemetryRepository telemetryRepository;
	@Mock private PasswordEncoder passwordEncoder;

	private BootstrapService bootstrapService;

	private Product cola;

	@BeforeEach
	void setUp() {
		bootstrapService = new BootstrapService(userRepository, productRepository,
				machineRepository, slotRepository, saleRepository, telemetryRepository, passwordEncoder);
		cola = Product.builder().id(1L).name("Coca-Cola 330ml").sku("DRK-001").price(new BigDecimal("1.50")).active(true).build();
		when(userRepository.existsByEmail(anyString())).thenReturn(true);
		when(productRepository.findBySku(anyString())).thenReturn(Optional.of(cola));
		when(machineRepository.findByCode(anyString())).thenReturn(Optional.of(
				VendingMachine.builder().id(1L).code("VM-001").build()));
		when(telemetryRepository.findByMachineId(any())).thenReturn(Collections.singletonList(
				MachineTelemetry.builder().id(1L).build()));
	}

	@Test
	void seed_shouldSkipAllUsers_whenAllExist() {
		bootstrapService.seed();
		verify(userRepository, never()).save(any(User.class));
	}

	@Test
	void seed_shouldCreateUserWhenNotExists() {
		when(userRepository.existsByEmail("admin@vendnet.io")).thenReturn(false);
		when(passwordEncoder.encode(anyString())).thenReturn("$2a$12$encoded");
		when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

		bootstrapService.seed();
		verify(userRepository).save(any(User.class));
	}
}
