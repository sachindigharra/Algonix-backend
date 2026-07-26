package com.algonix.server.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {

        final String securitySchemeName = "basicAuth";

        return new OpenAPI()
                .info(
                        new Info()
                                .title("Algonix API")
                                .version("v1")
                                .description("Algonix Backend APIs")
                                .contact(
                                        new Contact()
                                                .name("Sachin Sharma")
                                )
                );
    }
}
