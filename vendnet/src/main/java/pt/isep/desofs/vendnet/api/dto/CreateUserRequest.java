package pt.isep.desofs.vendnet.api.dto;

import jakarta.validation.constraints.Email;
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
public class CreateUserRequest {

	@NotBlank
	@Email
	private String email;

	@NotBlank
	@Size(min = 6)
	private String password;

	@NotBlank
	@Size(min = 2)
	private String name;

	@NotBlank
	private String role;
}
