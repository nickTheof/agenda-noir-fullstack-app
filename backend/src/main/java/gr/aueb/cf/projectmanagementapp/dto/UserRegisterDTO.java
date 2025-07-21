package gr.aueb.cf.projectmanagementapp.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UserRegisterDTO(
        @Schema(description = "User's username in email format.", example = "admin@mail.com")
        @NotBlank(message = "Username is a required field")
        @Email(message = "Username must have a valid email format.")
        String username,

        @Schema(description = "User's firstname.", example = "Nickolas")
        @NotBlank(message = "Firstname is a required field")
        String firstname,

        @Schema(description = "User's lastname.", example = "Cage")
        @NotBlank(message = "Firstname is a required field")
        String lastname,

        @Schema(description = "User's password.", example = "aA!12345")
        @NotBlank(message = "Password cannot be empty")
        @Size(min = 8, message = "Password must be at least 8 characters long")
        @Pattern(regexp = ".*[a-z].*", message = "Password must contain at least one lowercase letter")
        @Pattern(regexp = ".*[A-Z].*", message = "Password must contain at least one uppercase letter")
        @Pattern(regexp = ".*\\d.*", message = "Password must contain at least one digit")
        @Pattern(regexp = ".*[@#$%!^&*].*", message = "Password must contain at least one special character (@#$%!^&*)")
        String password
) {
}
