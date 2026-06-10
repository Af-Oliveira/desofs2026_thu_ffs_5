package pt.isep.desofs.vendnet.domain.model.product;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "products")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Product {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, length = 100)
	private String name;

	@Column(length = 500)
	private String description;

	@Column(nullable = false, precision = 10, scale = 2)
	private BigDecimal price;

	@Column(nullable = false, unique = true, length = 50)
	private String sku;

	@Column(nullable = false, length = 3)
	@Builder.Default
	private String currency = "EUR";

	@Column(nullable = false, length = 50)
	@Builder.Default
	private String category = "GENERAL";

	@Column(length = 500)
	private String imageUrl;

	@Column(length = 64)
	private String imageChecksum;

	@Column(nullable = false)
	private boolean active;

	@Column(nullable = false, updatable = false)
	private LocalDateTime createdAt;

	@Column(nullable = false)
	private LocalDateTime updatedAt;

	public Product(
			Long id,
			String name,
			String description,
			BigDecimal price,
			String sku,
			String imageUrl,
			boolean active,
			LocalDateTime createdAt,
			LocalDateTime updatedAt) {
		this.id = id;
		this.name = name;
		this.description = description;
		this.price = price;
		this.sku = sku;
		this.currency = "EUR";
		this.category = "GENERAL";
		this.imageUrl = imageUrl;
		this.active = active;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
	}
}
