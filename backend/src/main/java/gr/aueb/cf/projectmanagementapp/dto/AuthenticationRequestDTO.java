package gr.aueb.cf.projectmanagementapp.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record AuthenticationRequestDTO(
        @Schema(description = "User's username in email format.", example = "admin@mail.com")
        @NotBlank(message = "Username cannot be empty")
        String username,

        @Schema(description = "User's password.", example = "aA!12345")
        @NotBlank(message = "Password cannot be empty")
        String password
) {
}
