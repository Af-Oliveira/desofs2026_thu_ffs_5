package pt.isep.desofs.vendnet.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PurchaseRequest {

	@NotNull
	private Long productId;

	@NotNull
	private Long machineId;

	@NotBlank
	private String paymentToken;

	@NotBlank
	private String idempotencyKey;
}
