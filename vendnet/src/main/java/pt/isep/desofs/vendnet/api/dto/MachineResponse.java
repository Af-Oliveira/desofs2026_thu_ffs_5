package pt.isep.desofs.vendnet.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MachineResponse {

	private Long id;
	private String code;
	private String location;
	private String status;
	private boolean active;
	private String lastTelemetryAt;
	private String createdAt;
}
