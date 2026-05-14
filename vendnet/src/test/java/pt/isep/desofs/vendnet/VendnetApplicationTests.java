package pt.isep.desofs.vendnet;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class VendnetApplicationTests {

	@Test
	void contextLoads() {
		// Arrange — done by SpringBootTest bootstrap
		// Act — nothing (Spring context loading is the act)
		// Assert — test passes if the application context loads without exception
	}

}
