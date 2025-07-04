package gr.aueb.cf.projectmanagementapp.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDate;

public record TicketReadOnlyDTO(
        Long id,
        String uuid,
        String title,
        String description,
        String priority,
        String status,
        @JsonFormat(pattern = "yyyy-MM-dd")
        LocalDate expiryDate
) {
}
