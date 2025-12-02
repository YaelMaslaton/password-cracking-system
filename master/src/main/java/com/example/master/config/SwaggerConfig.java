package com.example.master.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI masterServiceOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Master Service API")
                        .description("Distributed Password Cracking System - Master Service")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Master Service Team")
                                .email("support@example.com")))
                .servers(List.of(
                        new Server().url("http://localhost:8080").description("Local Development Server")
                ));
    }
}