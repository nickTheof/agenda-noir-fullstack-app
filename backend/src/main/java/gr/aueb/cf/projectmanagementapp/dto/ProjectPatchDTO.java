package gr.aueb.cf.projectmanagementapp.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;

public record ProjectPatchDTO(
        @Schema(description = "The name of the project.", example = "Learn Java Spring")
        String name,

        @Schema(description = "The description of the project.", example = "Master Spring DATA JPA")
        String description,

        @Schema(description = "The status of the project", examples = {"OPEN", "ON_GOING", "CLOSED"})
        @Pattern(regexp = "^(OPEN|ON_GOING|CLOSED)$", message = "Project status valid values: 'OPEN', 'ON_GOING', 'CLOSED'")
        String status,

        @Schema(description = "The deleted flag status of the project. Supports soft deleting.", examples = {"true", "false"})

        Boolean deleted
) {
}
