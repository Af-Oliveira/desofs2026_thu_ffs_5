package pt.isep.desofs.vendnet.api.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TelemetryRequest {

	@NotBlank private String serialNumber;

	@NotNull
	@DecimalMin("-40.0")
	@DecimalMax("80.0")
	private BigDecimal temperature;

	private Map<@NotBlank String, @NotNull Integer> stockLevels;

	@NotBlank private String statusCode;

	private List<String> errorCodes;

	@NotNull @PastOrPresent private LocalDateTime timestamp;
}
