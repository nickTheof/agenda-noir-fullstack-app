package gr.aueb.cf.projectmanagementapp.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;

public record UserUpdateDTO(
        @Schema(description = "The username of the user in email format.", example = "admin@mail.com")
        @Email(message = "Username must have a valid email format")
        String username,

        @Schema(description = "The firstname of the user.", example = "Nick")
        String firstname,

        @Schema(description = "The lastname of the user.", example = "Cage")
        String lastname,

        @Schema(description = "The password of the user.", example = "aA!12345")
        @Pattern(regexp = "^(?=.*?[a-z])(?=.*?[A-Z])(?=.*?[0-9])(?=.*?[@#$%!^&*]).{8,}$", message = "Invalid Password")
        String password,

        @Schema(description = "The activation status of the user", example = "true")
        Boolean enabled,

        @Schema(description = "The verification status of the user", example = "false")
        Boolean verified,

        @Schema(description = "The deleted status of the user", example = "false")
        Boolean deleted
) {
    public UserUpdateDTO(String password) {
        this(null, null, null, password, null, null, null);
    }
}
