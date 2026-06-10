package pt.isep.desofs.vendnet.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TelemetryResponse {
	private boolean accepted;
	private int alertsRaised;
}
