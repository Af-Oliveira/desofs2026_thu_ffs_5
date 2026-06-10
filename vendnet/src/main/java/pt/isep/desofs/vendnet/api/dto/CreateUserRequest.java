package pt.isep.desofs.vendnet.api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
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
	@Size(min = 3, max = 30)
	@Pattern(regexp = "^[A-Za-z0-9]+$")
	private String username;

	@NotBlank
	@Email
	private String email;

	@NotBlank
	@Size(min = 12)
	@Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^A-Za-z0-9]).+$")
	private String password;

	@NotBlank
	@Size(min = 2)
	private String fullName;

	@NotBlank
	private String role;

	public CreateUserRequest(String email, String password, String name, String role) {
		this.username = email != null && email.contains("@") ? email.substring(0, email.indexOf('@')) : email;
		this.email = email;
		this.password = password;
		this.fullName = name;
		this.role = role;
	}

	public String getName() {
		return fullName;
	}
}
