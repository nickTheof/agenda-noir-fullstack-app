package gr.aueb.cf.projectmanagementapp.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record UserReadOnlyDTO(
        @Schema(description = "The user's ID", example = "1")
        Long id,

        @Schema(description = "The user's UUID", example = "baba3f82-7b0f-4440-9893-a2f76169802c")
        String uuid,

        @Schema(description = "User's username in email format.", example = "admin@mail.com")
        String username,

        @Schema(description = "User's firstname.", example = "Nickolas")
        String firstname,

        @Schema(description = "User's lastname.", example = "Cage")
        String lastname,

        @Schema(description = "User's enablement status.", example = "True")
        Boolean enabled,

        @Schema(description = "User's verification status.", example = "True")
        Boolean verified,

        @Schema(description = "User's soft deleted status.", example = "False")
        Boolean isDeleted,

        @Schema(description = "Count of consecutive fail attempts of user login", example = "0")
        Integer loginConsecutiveFailAttempts
) {
}
