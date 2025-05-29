package com.backup.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.Paths;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.parameters.RequestBody;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.tags.Tag;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    private static final String SECURITY_SCHEME_NAME = "Bearer Authentication";

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("ERP System API")
                        .description("Enterprise Resource Planning System API for the Government of Rwanda")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("ERP System Team")
                                .email("support@erp-system.rw")
                                .url("https://www.erp-system.rw"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0")))
                .addSecurityItem(new SecurityRequirement().addList(SECURITY_SCHEME_NAME))
                .components(new Components()
                        .addSecuritySchemes(SECURITY_SCHEME_NAME, new SecurityScheme()
                                .name(SECURITY_SCHEME_NAME)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")))
                .tags(java.util.List.of(
                        new Tag().name("Authentication").description("Authentication management APIs"),
                        new Tag().name("Employee Management").description("APIs for managing employees"),
                        new Tag().name("Employment Management").description("APIs for managing employee employments"),
                        new Tag().name("Deduction Management").description("APIs for managing salary deductions"),
                        new Tag().name("Pay Slip Management").description("APIs for managing employee pay slips"),
                        new Tag().name("Message Management").description("APIs for managing notification messages")
                ));
    }

    @Bean
    public OpenApiCustomizer customOpenAPI() {
        return openApi -> {
            // Add global response codes
            openApi.getPaths().forEach((path, pathItem) -> {
                pathItem.readOperations().forEach(operation -> {
                    ApiResponses responses = operation.getResponses();
                    if (responses == null) {
                        responses = new ApiResponses();
                        operation.setResponses(responses);
                    }

                    // Add common response codes if they don't exist
                    if (!responses.containsKey("401")) {
                        responses.addApiResponse("401", new ApiResponse()
                                .description("Unauthorized - JWT token is missing or invalid"));
                    }
                    if (!responses.containsKey("403")) {
                        responses.addApiResponse("403", new ApiResponse()
                                .description("Forbidden - You don't have permission to access this resource"));
                    }
                    if (!responses.containsKey("500")) {
                        responses.addApiResponse("500", new ApiResponse()
                                .description("Internal Server Error - Something went wrong on the server"));
                    }
                });
            });
        };
    }
}
