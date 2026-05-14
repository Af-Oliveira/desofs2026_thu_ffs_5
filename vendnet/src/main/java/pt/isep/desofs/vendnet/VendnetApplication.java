package pt.isep.desofs.vendnet;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Slf4j
@SpringBootApplication
public class VendnetApplication {

	public static void main(String[] args) {
		SpringApplication.run(VendnetApplication.class, args);
		log.info("========================================");
		log.info("  VendNet is running and ready!");
		log.info("  API:     http://localhost:8080");
		log.info("  Swagger: http://localhost:8080/swagger-ui/index.html");
		log.info("  Ping:    http://localhost:8080/api/health/ping");
		log.info("========================================");
	}

}
