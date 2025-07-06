package gr.aueb.cf.projectmanagementapp.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDate;
import java.util.List;

public record TicketFiltersDTO(
        Integer page,
        Integer size,
        String sortBy,
        String orderBy,
        String uuid,
        String title,
        List<String> status,
        List<String> priority,
        @JsonFormat(pattern = "yyyy-MM-dd")
        LocalDate expiryDate

) {
    public TicketFiltersDTO() {
        this(null, null, null, null, null, null, null, null, null);
    }
}
