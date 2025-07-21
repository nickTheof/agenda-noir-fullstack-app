package gr.aueb.cf.projectmanagementapp.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record AuthenticationResponseDTO(
        @Schema(description = "The JWT Bearer token", example = "eyJhbGciOiJIUzI1NiJ9...")
        String token
) {
}


