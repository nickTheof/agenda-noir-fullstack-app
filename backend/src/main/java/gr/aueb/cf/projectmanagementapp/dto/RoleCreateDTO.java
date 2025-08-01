package gr.aueb.cf.projectmanagementapp.dto;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record RoleCreateDTO(
        @Schema(description = "The role's name", example = "USER_MANAGER")
        @NotBlank(message = "The name of the role is a required field")
        String name,

        @ArraySchema(
                schema = @Schema(
                        description = "The list including all the permissions of the role",
                        example = "READ_USER"
                ),
                arraySchema = @Schema(
                        example = "[\"READ_USER\", \"CREATE_ROLE\"]"
                )
        )
        @NotEmpty(message = "At least one permission is required")
        List<String> permissions
) {
}
