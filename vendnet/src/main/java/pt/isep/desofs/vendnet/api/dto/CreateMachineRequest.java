package pt.isep.desofs.vendnet.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateMachineRequest {

	@NotBlank
	@Size(min = 3, max = 50)
	private String code;

	@NotBlank
	@Size(max = 200)
	private String location;
}
