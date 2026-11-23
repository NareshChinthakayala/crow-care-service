package com.crown.care.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Swagger UI: http://localhost:4041/swagger-ui.html
 * Log in via /api/auth/admin/login or /api/auth/employee/login, copy the accessToken,
 * click "Authorize" and paste it (without the "Bearer " prefix).
 */
@Configuration
public class SwaggerConfig {

    private static final String BEARER = "bearerAuth";

    @Bean
    public OpenAPI crownCareOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("CrownCare API")
                        .description("African Hair Studio management - admin & stylist REST APIs")
                        .version("v1"))
                .addSecurityItem(new SecurityRequirement().addList(BEARER))
                .components(new Components().addSecuritySchemes(BEARER,
                        new SecurityScheme()
                                .name(BEARER)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")));
    }
}
