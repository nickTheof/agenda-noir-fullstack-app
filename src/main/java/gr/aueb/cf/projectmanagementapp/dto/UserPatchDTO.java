package gr.aueb.cf.projectmanagementapp.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record UserPatchDTO(
        @Schema(description = "The activation status of the user", example = "true")
        Boolean enabled,

        @Schema(description = "The verification status of the user", example = "false")
        Boolean verified,

        @Schema(description = "The deleted status of the user", example = "false")
        Boolean deleted
) {
}
