package pt.isep.desofs.vendnet.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {

	@NotBlank
	@Size(min = 3, max = 30)
	@Pattern(regexp = "^[A-Za-z0-9]+$")
	private String username;

	@NotBlank
	@Size(min = 6, max = 100)
	private String password;

	public String getEmail() {
		return username;
	}

	public void setEmail(String email) {
		this.username = email;
	}
}
