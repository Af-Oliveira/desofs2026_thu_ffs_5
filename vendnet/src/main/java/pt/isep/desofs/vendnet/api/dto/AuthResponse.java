package pt.isep.desofs.vendnet.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthResponse {

	private String token;
	private String accessToken;
	private String refreshToken;
	private Long expiresIn;
	private String email;
	private String username;
	private String name;
	private String role;
	@Builder.Default private boolean mfaRequired = false;

	public AuthResponse(String token, String email, String name, String role, boolean mfaRequired) {
		this.token = token;
		this.accessToken = token;
		this.email = email;
		this.name = name;
		this.role = role;
		this.mfaRequired = mfaRequired;
	}
}
