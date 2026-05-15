package pt.isep.desofs.vendnet.domain.model.slot;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pt.isep.desofs.vendnet.domain.model.machine.VendingMachine;
import pt.isep.desofs.vendnet.domain.model.product.Product;

@Entity
@Table(name = "slots")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Slot {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, length = 10)
	private String position;

	@Column(nullable = false)
	private int capacity;

	@Column(nullable = false)
	private int currentStock;

	@Version
	private Long version;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "machine_id", nullable = false)
	private VendingMachine machine;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "product_id", nullable = false)
	private Product product;

	@Column(nullable = false, updatable = false)
	private LocalDateTime createdAt;

	@Column(nullable = false)
	private LocalDateTime updatedAt;

	public void reserveUnit() {
		if (this.currentStock <= 0) {
			throw new IllegalStateException("Slot is empty, cannot reserve");
		}
		this.currentStock--;
	}

	public void releaseReservation() {
		if (this.currentStock < this.capacity) {
			this.currentStock++;
		}
	}

	public void addStock(int quantity) {
		if (quantity <= 0) {
			throw new IllegalArgumentException("Quantity must be positive");
		}
		int newStock = this.currentStock + quantity;
		if (newStock > this.capacity) {
			throw new IllegalArgumentException(
					"Exceeds slot capacity: " + newStock + " > " + this.capacity);
		}
		this.currentStock = newStock;
	}
}
