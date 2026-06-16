package pt.isep.desofs.vendnet.infrastructure.os;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class BackupResult {
	private String filename;
	private long size;
	private String checksum;
	private LocalDateTime timestamp;
}
