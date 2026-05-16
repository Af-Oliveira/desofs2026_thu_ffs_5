package pt.isep.desofs.vendnet.e2e;

import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;

/**
 * Boots the application with in-memory H2 and {@code bootstrap} seed data, then exercises it only
 * through HTTP (Rest Assured). Test code does not depend on domain beans or repositories.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles({"test", "bootstrap"})
public abstract class AbstractHttpBlackBoxE2E {

	@LocalServerPort
	protected int port;

	@BeforeEach
	void configureRestAssured() {
		RestAssured.baseURI = "http://localhost";
		RestAssured.port = port;
		RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
	}
}
