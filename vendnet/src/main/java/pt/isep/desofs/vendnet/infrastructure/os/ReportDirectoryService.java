package pt.isep.desofs.vendnet.infrastructure.os;

public interface ReportDirectoryService {

	String createReportDirectory(String reportType);

	void cleanupOldReports(int retentionDays);
}
