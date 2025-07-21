package gr.aueb.cf.projectmanagementapp.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record UserRoleInsertDTO(
        @Schema(description = "A list of roles to update the user")
        @NotEmpty(message = "At least one roleName is required")
        List<String> roleNames
) {
}
