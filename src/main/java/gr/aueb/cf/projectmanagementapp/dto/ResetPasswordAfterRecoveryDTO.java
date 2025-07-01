package gr.aueb.cf.projectmanagementapp.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record ResetPasswordAfterRecoveryDTO(
        @Schema(description = "The verification token.", example = "baba3f82-7b0f-4440-9893-a2f76169802c")
        @NotBlank(message = "token cannot be empty")
        String token,

        @Schema(description = "The user's new password.", example = "aA!12345")
        @NotBlank(message = "Password cannot be empty")
        @Size(min = 8, message = "Password must be at least 8 characters long")
        @Pattern(regexp = ".*[a-z].*", message = "Password must contain at least one lowercase letter")
        @Pattern(regexp = ".*[A-Z].*", message = "Password must contain at least one uppercase letter")
        @Pattern(regexp = ".*\\d.*", message = "Password must contain at least one digit")
        @Pattern(regexp = ".*[@#$%!^&*].*", message = "Password must contain at least one special character (@#$%!^&*)")
        String newPassword
) {
}
