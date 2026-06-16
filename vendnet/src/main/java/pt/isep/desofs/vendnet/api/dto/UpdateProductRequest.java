package pt.isep.desofs.vendnet.api.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateProductRequest {

	@Size(min = 1, max = 100)
	private String name;

	@Size(max = 500)
	private String description;

	@Positive
	private BigDecimal price;

	private Boolean active;
}
