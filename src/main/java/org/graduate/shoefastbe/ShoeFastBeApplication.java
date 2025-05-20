package org.graduate.shoefastbe;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableScheduling;

//@SecurityScheme(
//		name = "bearerAuth",
//		type = SecuritySchemeType.HTTP,
//		bearerFormat = "JWT",
//		scheme = "bearer"
//)
//@OpenAPIDefinition(info = @Info(title = "Library Apis",version = "V.1.0",description = "Documentation for Library Management apis")
//		,security = {@SecurityRequirement(name = "bearerAuth")})
//@SpringBootApplication
//@EnableAspectJAutoProxy
//@EnableScheduling

@SecurityScheme(
		name = "bearerAuth",
		type = SecuritySchemeType.HTTP,
		bearerFormat = "JWT",
		scheme = "bearer"
)
@OpenAPIDefinition(
		info = @Info(
				title = "ShoeFast API Documentation",
				version = "1.0",
				description = "Comprehensive documentation for ShoeFast system APIs, including product management, order processing, and user authentication.",
				contact = @Contact(
						name = "ShoeFast Support",
						email = "support@shoefast.com",
						url = "https://shoefast.com"
				)
		),
		security = {@SecurityRequirement(name = "bearerAuth")}
)
@SpringBootApplication
@EnableAspectJAutoProxy
@EnableScheduling
public class ShoeFastBeApplication {

	public static void main(String[] args) {
		SpringApplication.run(ShoeFastBeApplication.class, args);
	}

}
