package pt.isep.desofs.vendnet.infrastructure.os;

public interface AuditLogRotationService {

    void rotate();

    void compressAfterDays(int days);

    void deleteAfterDays(int days);
}
