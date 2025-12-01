package org.example.flyora_backend.configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.*;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI flyoraOpenAPI(@Value("${openapi.title}") String title,
                                 @Value("${openapi.description}") String description,
                                 @Value("${openapi.version}") String version,
                                 @Value("${openapi.server}") String serverUrl)
    {

        return new OpenAPI()
                .info(new Info()
                        .title(title)
                        .description(description)
                        .version(version)
                        .license(new License().name("Apache 2.0").url("https://www.apache.org/licenses/LICENSE-2.0"))
                        .contact(new Contact()
                                .name("Flyora Dev Team")
//                                .email("support@flyora.com")
                                .url("https://flyora.com")
                        )
                )
                .servers(List.of(new Server().url(serverUrl)))
                //authorize
                .components(
                        new Components()
                                .addSecuritySchemes(
                                        "bearerAuth",
                                        new SecurityScheme()
                                                .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")))
                .security(List.of(new SecurityRequirement().addList("bearerAuth")));


    }

//    @Bean
//    public GroupOpenApi groupOpenApi(){
//        return GroupedOpenApi.builder()
//                .group("api-group-1")
//                .packagesToScan("org.example.flyora_backend.controller")
//                .build();
//    }
}
