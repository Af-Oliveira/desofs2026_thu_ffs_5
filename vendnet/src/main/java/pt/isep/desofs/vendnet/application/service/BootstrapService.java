package pt.isep.desofs.vendnet.application.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pt.isep.desofs.vendnet.domain.model.machine.MachineStatus;
import pt.isep.desofs.vendnet.domain.model.machine.VendingMachine;
import pt.isep.desofs.vendnet.domain.model.product.Product;
import pt.isep.desofs.vendnet.domain.model.sale.Sale;
import pt.isep.desofs.vendnet.domain.model.slot.Slot;
import pt.isep.desofs.vendnet.domain.model.telemetry.MachineTelemetry;
import pt.isep.desofs.vendnet.domain.model.user.AccountStatus;
import pt.isep.desofs.vendnet.domain.model.user.Role;
import pt.isep.desofs.vendnet.domain.model.user.User;
import pt.isep.desofs.vendnet.domain.repository.ProductRepository;
import pt.isep.desofs.vendnet.domain.repository.SaleRepository;
import pt.isep.desofs.vendnet.domain.repository.SlotRepository;
import pt.isep.desofs.vendnet.domain.repository.TelemetryRepository;
import pt.isep.desofs.vendnet.domain.repository.UserRepository;
import pt.isep.desofs.vendnet.domain.repository.VendingMachineRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class BootstrapService {

	private static final String ONLINE_STATUS = "ONLINE";

	private final UserRepository userRepository;
	private final ProductRepository productRepository;
	private final VendingMachineRepository machineRepository;
	private final SlotRepository slotRepository;
	private final SaleRepository saleRepository;
	private final TelemetryRepository telemetryRepository;
	private final PasswordEncoder passwordEncoder;

	public void seed() {
		log.info("=== Bootstrapping seed data ===");
		LocalDateTime now = LocalDateTime.now();

		Product cola = seedProduct("Coca-Cola 330ml", "Refreshing cola drink, 330ml can", new BigDecimal("1.50"), "DRK-001", now);
		Product water = seedProduct("Water 500ml", "Natural mineral water, 500ml bottle", new BigDecimal("1.00"), "DRK-002", now);
		Product juice = seedProduct("Orange Juice 250ml", "Fresh orange juice, 250ml", new BigDecimal("1.80"), "DRK-003", now);
		Product chips = seedProduct("Potato Chips", "Classic salted potato chips, 50g", new BigDecimal("1.20"), "SNK-001", now);
		Product chocolate = seedProduct("Chocolate Bar", "Milk chocolate bar, 100g", new BigDecimal("1.50"), "SNK-002", now);
		Product nuts = seedProduct("Mixed Nuts", "Roasted mixed nuts, 75g", new BigDecimal("2.00"), "SNK-003", now);
		Product coffee = seedProduct("Hot Coffee", "Freshly brewed hot coffee, 200ml", new BigDecimal("0.80"), "HOT-001", now);
		Product hotChocolate = seedProduct("Hot Chocolate", "Creamy hot chocolate, 200ml", new BigDecimal("1.00"), "HOT-002", now);

		VendingMachine vm1 = seedMachine("VM-LIS-001", "Lisbon Airport - Terminal 1", now);
		VendingMachine vm2 = seedMachine("VM-LIS-002", "Lisbon - Oriente Station", now);
		VendingMachine vm3 = seedMachine("VM-PTO-001", "Porto - Campanha Station", now);
		VendingMachine vm4 = seedMachine("VM-FAR-001", "Faro - Downtown", now);

		seedUser("admin", "admin@vendnet.io", "Admin@123456", "System Administrator", Role.ROLE_ADMINISTRATOR, now);
		seedUser("operator", "operator@vendnet.io", "Operator@123456", "Machine Operator", Role.ROLE_OPERATOR, now);
		seedUser("customer", "customer@vendnet.io", "Customer@123456", "Regular Customer", Role.ROLE_CUSTOMER, now);

		seedSlot("A1", 20, 20, vm1, cola, now);
		seedSlot("A2", 20, 20, vm1, water, now);
		seedSlot("A3", 15, 15, vm1, juice, now);
		seedSlot("B1", 30, 28, vm1, chips, now);
		seedSlot("B2", 25, 22, vm1, chocolate, now);
		seedSlot("C1", 20, 18, vm1, nuts, now);

		seedSlot("A1", 20, 20, vm2, cola, now);
		seedSlot("A2", 20, 19, vm2, water, now);
		seedSlot("B1", 30, 30, vm2, chips, now);
		seedSlot("B2", 25, 24, vm2, chocolate, now);
		seedSlot("H1", 40, 38, vm2, coffee, now);
		seedSlot("H2", 35, 33, vm2, hotChocolate, now);

		seedSlot("A1", 20, 20, vm3, cola, now);
		seedSlot("A2", 20, 20, vm3, water, now);
		seedSlot("B1", 30, 30, vm3, chips, now);
		seedSlot("C1", 20, 19, vm3, nuts, now);

		seedSlot("A1", 20, 20, vm4, cola, now);
		seedSlot("A2", 20, 20, vm4, water, now);
		seedSlot("B1", 30, 30, vm4, chips, now);
		seedSlot("B2", 25, 24, vm4, chocolate, now);

		seedSale(vm1, cola, cola.getPrice(), 1, now.minusHours(2));
		seedSale(vm1, water, water.getPrice(), 2, now.minusHours(3));
		seedSale(vm1, chips, chips.getPrice(), 1, now.minusHours(1));
		seedSale(vm2, coffee, coffee.getPrice(), 3, now.minusMinutes(30));
		seedSale(vm2, hotChocolate, hotChocolate.getPrice(), 1, now.minusMinutes(45));
		seedSale(vm3, cola, cola.getPrice(), 1, now.minusDays(1));
		seedSale(vm4, nuts, nuts.getPrice(), 1, now.minusHours(5));

			seedTelemetry(vm1, ONLINE_STATUS, now.minusMinutes(5));
			seedTelemetry(vm2, ONLINE_STATUS, now.minusMinutes(5));
			seedTelemetry(vm3, ONLINE_STATUS, now.minusMinutes(5));
			seedTelemetry(vm4, "MAINTENANCE", now.minusMinutes(5));

		log.info("=== Bootstrapping complete ===");
	}

	private void seedUser(String username, String email, String password, String name, Role role, LocalDateTime now) {
		if (userRepository.existsByEmail(email)) {
			Optional<User> existingUser = userRepository.findByEmail(email);
			if (existingUser.isEmpty()) {
				log.warn("User '{}' reported as existing but could not be loaded — skipping repair", email);
				return;
			}
			User existing = existingUser.get();
			existing.setUsername(username);
			existing.setPassword(passwordEncoder.encode(password));
			existing.setName(name);
			existing.setRole(role);
			existing.setAccountStatus(AccountStatus.ACTIVE);
			existing.setFailedAttempts(0);
			existing.setLockTime(null);
			existing.setLastFailedAttemptTime(null);
			existing.setUpdatedAt(now);
			userRepository.save(existing);
			log.info("Updated seed user: {} ({})", email, role);
			return;
		}
		if (userRepository.existsByUsername(username)) {
			log.info("User '{}' already exists — skipping", username);
			return;
		}
		User user = User.builder()
				.username(username)
				.email(email)
				.password(passwordEncoder.encode(password))
				.name(name)
				.role(role)
				.accountStatus(AccountStatus.ACTIVE)
				.createdAt(now)
				.updatedAt(now)
				.build();
		userRepository.save(user);
		log.info("Created user: {} ({})", email, role);
	}

	private Product seedProduct(String name, String description, BigDecimal price, String sku, LocalDateTime now) {
		if (productRepository.findBySku(sku).isPresent()) {
			log.info("Product '{}' already exists — skipping", sku);
			return productRepository.findBySku(sku).get();
		}
		Product product = Product.builder()
				.name(name)
				.description(description)
					.price(price)
					.sku(sku)
					.currency("EUR")
					.category(categoryForSku(sku))
					.active(true)
				.createdAt(now)
				.updatedAt(now)
				.build();
		Product saved = productRepository.save(product);
		log.info("Created product: {} ({})", sku, name);
		return saved;
	}

	private String categoryForSku(String sku) {
		if (sku.startsWith("DRK")) {
			return "DRINK";
		}
		if (sku.startsWith("SNK")) {
			return "SNACK";
		}
		return "HOT";
	}

	private VendingMachine seedMachine(String code, String location, LocalDateTime now) {
		if (machineRepository.findByCode(code).isPresent()) {
			log.info("Machine '{}' already exists — skipping", code);
			return machineRepository.findByCode(code).get();
		}
		VendingMachine machine = VendingMachine.builder()
				.code(code)
				.location(location)
				.active(true)
				.status(MachineStatus.ONLINE)
				.createdAt(now)
				.updatedAt(now)
				.build();
		VendingMachine saved = machineRepository.save(machine);
		log.info("Created machine: {} ({})", code, location);
		return saved;
	}

	private Slot seedSlot(String position, int capacity, int currentStock,
				VendingMachine machine, Product product, LocalDateTime now) {
		for (Slot existing : slotRepository.findByMachineId(machine.getId())) {
			if (existing.getPosition().equals(position)
					&& existing.getProduct().getId().equals(product.getId())) {
				log.info(
						"Slot '{}' for machine '{}' already exists — skipping",
						position,
						machine.getCode());
				return existing;
			}
		}
		Slot slot = Slot.builder()
				.position(position)
				.capacity(capacity)
				.currentStock(currentStock)
				.machine(machine)
				.product(product)
				.createdAt(now)
				.updatedAt(now)
				.build();
		Slot saved = slotRepository.save(slot);
		log.info("Created slot: {} / {} -> {} (stock {}/{})",
				machine.getCode(), position, product.getSku(), currentStock, capacity);
		return saved;
	}

	private void seedSale(VendingMachine machine, Product product, BigDecimal price, int quantity, LocalDateTime saleDate) {
		for (Sale existing : saleRepository.findByMachineId(machine.getId())) {
			if (existing.getProduct().getId().equals(product.getId()) && existing.getQuantity() == quantity) {
				log.info(
						"Sale seed for '{}' / '{}' already exists — skipping",
						machine.getCode(),
						product.getSku());
				return;
			}
		}
		Sale sale = Sale.builder()
				.machine(machine)
				.product(product)
				.price(price.multiply(BigDecimal.valueOf(quantity)))
				.quantity(quantity)
				.totalAmount(price.multiply(BigDecimal.valueOf(quantity)))
				.unitPrice(price)
				.saleDate(saleDate)
				.createdAt(saleDate)
				.build();
		saleRepository.save(sale);
		log.info("Created sale: {} -> {} x{} @ {}",
				machine.getCode(), product.getSku(), quantity, price);
	}

	private void seedTelemetry(VendingMachine machine, String status, LocalDateTime now) {
		List<MachineTelemetry> existing = telemetryRepository.findByMachineId(machine.getId());
		if (!existing.isEmpty()) {
			log.info("Telemetry for '{}' already exists — skipping", machine.getCode());
			return;
		}
		MachineTelemetry telemetry = MachineTelemetry.builder()
				.machine(machine)
				.cpuUsage(new BigDecimal("35.5"))
				.memoryUsage(new BigDecimal("62.0"))
				.diskUsage(new BigDecimal("45.0"))
				.status(status)
				.uptimeSeconds(86400L * 30)
				.totalSalesToday(150)
				.temperatureCelsius(new BigDecimal("22.5"))
				.timestamp(now)
				.build();
		telemetryRepository.save(telemetry);
		log.info("Created telemetry for: {} ({})", machine.getCode(), status);
	}
}
