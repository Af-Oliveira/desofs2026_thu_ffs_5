package pt.isep.desofs.vendnet.api.view;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApiError {

	private int status;
	private String error;
	private String message;
	private LocalDateTime timestamp;
}
