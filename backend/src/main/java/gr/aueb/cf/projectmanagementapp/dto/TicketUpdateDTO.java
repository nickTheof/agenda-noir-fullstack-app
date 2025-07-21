package gr.aueb.cf.projectmanagementapp.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.time.LocalDate;

public record TicketUpdateDTO(
        @Schema(description = "The updated title of the ticket.")
        @NotBlank(message = "Title is a required field.")
        String title,

        @Schema(description = "The updated description of the ticket.")
        @NotBlank(message = "Description is a required field.")
        String description,

        @Schema(description = "The updated priority of the ticket.", examples = {"LOW", "MEDIUM", "HIGH", "CRITICAL"})
        @NotBlank(message = "Priority is a required field.")
        @Pattern(regexp = "^(LOW|MEDIUM|HIGH|CRITICAL)$")
        String priority,

        @Schema(description = "The updated status of the ticket.", examples = {"OPEN", "ON_GOING", "CLOSED"})
        @NotBlank(message = "Status is a required field.")
        @Pattern(regexp = "^(OPEN|ON_GOING|CLOSED)$")
        String status,

        @Schema(description = "The updated expiry date of the ticket.")
        @NotNull(message = "ExpiryDate is a required field.")
        @Future(message = "ExpiryDate should be a date in the future.")
        @JsonFormat(pattern = "yyyy-MM-dd")
        LocalDate expiryDate
) {
}
