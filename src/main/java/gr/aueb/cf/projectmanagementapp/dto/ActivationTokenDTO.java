package gr.aueb.cf.projectmanagementapp.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record ActivationTokenDTO(
        @Schema(description = "The verification token.", example = "baba3f82-7b0f-4440-9893-a2f76169802c")
        @NotBlank(message = "Token is required")
        String token
) {
}
