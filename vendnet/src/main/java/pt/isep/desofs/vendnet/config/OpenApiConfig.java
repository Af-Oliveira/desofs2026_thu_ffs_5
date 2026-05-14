package pt.isep.desofs.vendnet.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

	@Bean
	public OpenAPI vendNetOpenAPI() {
		return new OpenAPI()
				.info(
						new Info()
								.title("VendNet API")
								.description("REST API for Vending Machine Network management")
								.version("0.0.1")
								.contact(
										new Contact()
												.name("DESOFS Team 5")
												.email("desofs2026_thu_ffs_5@isep.ipp.pt"))
								.license(
										new License()
												.name("Private")
												.url("https://www.isep.ipp.pt")))
				.addSecurityItem(new SecurityRequirement().addList("Bearer"))
				.components(
						new Components()
								.addSecuritySchemes(
										"Bearer",
										new SecurityScheme()
												.type(SecurityScheme.Type.HTTP)
												.scheme("bearer")
												.bearerFormat("JWT")
												.description("Enter your JWT token")));
	}
}
