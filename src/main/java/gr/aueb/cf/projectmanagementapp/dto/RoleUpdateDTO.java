package gr.aueb.cf.projectmanagementapp.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record RoleUpdateDTO(
        @Schema(description = "The role's name", example = "USER_MANAGER")
        @NotBlank(message = "The name of the role is a required field")
        String name,

        @Schema(description = "The list including all the permissions of the role", example = "['READ_USER', 'CREATE_ROLE']")
        @NotEmpty(message = "At least one permission is required")
        List<String> permissions
) {
}
