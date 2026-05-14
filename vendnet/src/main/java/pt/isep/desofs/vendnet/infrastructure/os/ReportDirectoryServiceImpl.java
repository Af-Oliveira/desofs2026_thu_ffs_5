package pt.isep.desofs.vendnet.infrastructure.os;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReportDirectoryServiceImpl implements ReportDirectoryService {

    private final PathValidator pathValidator;

    @Override
    public String createReportDirectory(String reportType) {
        log.info("Report directory creation triggered (type={}) — not yet implemented", reportType);
        return "/var/vendnet/reports/" + reportType + "/pending";
    }

    @Override
    public void cleanupOldReports(int retentionDays) {
        log.info("Report cleanup triggered ({} days) — not yet implemented", retentionDays);
    }
}
