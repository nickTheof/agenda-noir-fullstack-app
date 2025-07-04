package gr.aueb.cf.projectmanagementapp.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record ProjectUpdateDTO(
        @Schema(description = "The name of the project.", example = "Learn Java Spring")
        @NotBlank(message = "Name is a required field")
        String name,

        @Schema(description = "The description of the project.", example = "Master Spring DATA JPA")
        @NotBlank(message = "Description is a required field")
        String description,

        @Schema(description = "The status of the project", examples = {"OPEN", "ON_GOING", "CLOSED"})
        @NotBlank(message = "Status project is a required field")
        @Pattern(regexp = "^(OPEN|ON_GOING|CLOSED)$", message = "Project status valid values: 'OPEN', 'ON_GOING', 'CLOSED'")
        String status,

        @Schema(description = "The deleted flag status of the project. Supports soft deleting.", examples = {"true", "false"})
        @NotNull(message = "Deleted is a required field")
        Boolean deleted
) {
}
