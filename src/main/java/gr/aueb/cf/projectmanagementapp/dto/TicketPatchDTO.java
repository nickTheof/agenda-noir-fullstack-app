package gr.aueb.cf.projectmanagementapp.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.time.LocalDate;

public record TicketPatchDTO(
        @Schema(description = "The updated title of the ticket.")
        String title,

        @Schema(description = "The updated description of the ticket.")
        String description,

        @Schema(description = "The updated priority of the ticket.", examples = {"LOW", "MEDIUM", "HIGH", "CRITICAL"})
        @Pattern(regexp = "^(LOW|MEDIUM|HIGH|CRITICAL)$")
        String priority,

        @Schema(description = "The updated status of the ticket.", examples = {"OPEN", "ON_GOING", "CLOSED"})
        @Pattern(regexp = "^(OPEN|ON_GOING|CLOSED)$")
        String status,

        @Schema(description = "The updated expiry date of the ticket.")
        @Future(message = "ExpiryDate should be a date in the future.")
        @JsonFormat(pattern = "yyyy-MM-dd")
        LocalDate expiryDate
) {
}
