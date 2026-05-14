package pt.isep.desofs.vendnet.api.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pt.isep.desofs.vendnet.infrastructure.os.BackupService;
import pt.isep.desofs.vendnet.infrastructure.os.ReportDirectoryService;

import java.util.Map;

@RestController
@RequestMapping("/api/admin/operations")
@RequiredArgsConstructor
public class OperationsController {

    private final BackupService backupService;
    private final ReportDirectoryService reportDirectoryService;

    @PostMapping("/backup")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, String>> triggerBackup() {
        backupService.generateBackup();
        return ResponseEntity.ok(Map.of("status", "backup initiated"));
    }

    @PostMapping("/reports/sales")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, String>> generateSalesReport() {
        String path = reportDirectoryService.createReportDirectory("sales");
        return ResponseEntity.ok(Map.of("status", "report generated", "path", path));
    }
}
