package pt.isep.desofs.vendnet.domain.model.machine;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "vending_machines")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VendingMachine {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, unique = true, length = 50)
	private String code;

	@Column(nullable = false, length = 200)
	private String location;

	@Column(nullable = false)
	private boolean active;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20)
	@Builder.Default
	private MachineStatus status = MachineStatus.OFFLINE;

	@Column(name = "last_telemetry_at")
	private LocalDateTime lastTelemetryAt;

	@Column(nullable = false, updatable = false)
	private LocalDateTime createdAt;

	@Column(nullable = false)
	private LocalDateTime updatedAt;

	public void checkStatus() {
		if (this.status != MachineStatus.ONLINE && this.status != MachineStatus.MAINTENANCE) {
			throw new pt.isep.desofs.vendnet.domain.exception.MachineOfflineException(
					"Machine is " + this.status + ". Expected ONLINE or MAINTENANCE.");
		}
	}
}
