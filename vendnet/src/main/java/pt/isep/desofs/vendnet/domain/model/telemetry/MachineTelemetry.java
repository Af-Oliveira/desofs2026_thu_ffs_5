package pt.isep.desofs.vendnet.domain.model.telemetry;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pt.isep.desofs.vendnet.domain.model.machine.VendingMachine;

@Entity
@Table(name = "machine_telemetry")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MachineTelemetry {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "machine_id", nullable = false)
	private VendingMachine machine;

	@Column(name = "cpu_usage")
	private BigDecimal cpuUsage;

	@Column(name = "memory_usage")
	private BigDecimal memoryUsage;

	@Column(name = "disk_usage")
	private BigDecimal diskUsage;

	@Column(length = 20)
	private String status;

	@Column(name = "uptime_seconds")
	private Long uptimeSeconds;

	@Column(name = "total_sales_today")
	private Integer totalSalesToday;

	@Column(name = "temperature_celsius")
	private BigDecimal temperatureCelsius;

	@Column(nullable = false)
	private LocalDateTime timestamp;
}
