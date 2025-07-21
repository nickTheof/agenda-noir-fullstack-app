package gr.aueb.cf.projectmanagementapp.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Map;

@Schema(description = "API Error details")
public record ApiErrorDTO(
        @Schema(description = "Application Error Code")
        String code,
        @Schema(description = "Error message details")
        String message,
        @Schema(description = "Field validation errors", nullable = true)
        Map<String, String> errors, // null if not a validation error
        @Schema(description = "Timestamp in milliseconds since epoch")
        long timestamp,
        @Schema(description = "Request path")
        String path // optional, can be null
) {
    public ApiErrorDTO(String code, String message, long timestamp, String path) {
        this(code, message, Map.of(), timestamp, path);
    }
}
