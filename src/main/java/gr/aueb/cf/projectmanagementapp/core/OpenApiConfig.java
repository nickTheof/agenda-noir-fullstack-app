package gr.aueb.cf.projectmanagementapp.core;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.tags.Tag;

@OpenAPIDefinition(
        info = @Info(title = "My Project Management REST API", version = "1.0"),
        tags = { //TODO: Add the remaining OPEN API tags for swagger
                @Tag(name = "Authentication", description = "User authentication endpoints"),
                @Tag(name = "Users", description = "User management endpoints"),
                @Tag(name = "Roles", description = "Role management endpoints"),
        }
)
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT"
)
public class OpenApiConfig {
}
