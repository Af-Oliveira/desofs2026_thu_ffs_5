package pt.isep.desofs.vendnet.api.controller;

import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pt.isep.desofs.vendnet.infrastructure.os.BackupService;
import pt.isep.desofs.vendnet.infrastructure.os.BackupResult;
import pt.isep.desofs.vendnet.infrastructure.os.ReportDirectoryService;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class OperationsController {

	private final BackupService backupService;
	private final ReportDirectoryService reportDirectoryService;

	@PostMapping({"/backups", "/operations/backup"})
	@PreAuthorize("hasRole('ADMINISTRATOR')")
	public ResponseEntity<BackupResult> triggerBackup() {
		BackupResult result = backupService.generateBackup();
		return ResponseEntity.status(HttpStatus.CREATED).body(result);
	}

	@PostMapping("/operations/reports/sales")
	@PreAuthorize("hasRole('ADMINISTRATOR')")
	public ResponseEntity<Map<String, String>> generateSalesReport() {
		String path = reportDirectoryService.createReportDirectory("sales");
		return ResponseEntity.ok(Map.of("status", "report generated", "path", path));
	}
}
