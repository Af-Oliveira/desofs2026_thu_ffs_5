package pt.isep.desofs.vendnet.domain.model.sale;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "idempotency_records")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IdempotencyRecord {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, unique = true, length = 256)
	private String idempotencyKey;

	@Column(length = 50)
	private String responseStatus;

	@Column(columnDefinition = "TEXT")
	private String responseBody;

	private Long saleId;

	@Column(nullable = false)
	private LocalDateTime createdAt;
}
