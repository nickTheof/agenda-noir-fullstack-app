package gr.aueb.cf.projectmanagementapp.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record ChangePasswordDTO(
        @Schema(description = "The current authenticated user's old password.", example = "aA!12345")
        @NotBlank(message = "Old password is required field")
        String oldPassword,

        @Schema(description = "The current authenticated user's new password.", example = "aA!12344")
        @NotBlank(message = "Password cannot be empty")
        @Size(min = 8, message = "Password must be at least 8 characters long")
        @Pattern(regexp = ".*[a-z].*", message = "Password must contain at least one lowercase letter")
        @Pattern(regexp = ".*[A-Z].*", message = "Password must contain at least one uppercase letter")
        @Pattern(regexp = ".*\\d.*", message = "Password must contain at least one digit")
        @Pattern(regexp = ".*[@#$%!^&*].*", message = "Password must contain at least one special character (@#$%!^&*)")
        String newPassword
) {
}
