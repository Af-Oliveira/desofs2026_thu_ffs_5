package pt.isep.desofs.vendnet.infrastructure.os;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ReportDirectoryServiceImplTest {

	@Mock private PathValidator pathValidator;

	private ReportDirectoryServiceImpl service;

	@BeforeEach
	void setUp() {
		service = new ReportDirectoryServiceImpl(pathValidator);
		org.springframework.test.util.ReflectionTestUtils.setField(service, "vendnetRoot", "/var/vendnet");
	}

	@Test
	void createReportDirectory_invalidType_shouldThrow() {
		assertThrows(IllegalArgumentException.class, () -> service.createReportDirectory("invalid"));
	}

	@Test
	void createReportDirectory_pathTraversal_type_shouldThrow() {
		assertThrows(IllegalArgumentException.class, () -> service.createReportDirectory("../../etc"));
	}

	@Test
	void createReportDirectory_validType_sales_shouldContainPath() {
		when(pathValidator.isValidPath(any(), any())).thenReturn(true);
		String result = service.createReportDirectory("sales");
		assertEquals("/var/vendnet/reports/sales", result.substring(0, "/var/vendnet/reports/sales".length()));
	}
}