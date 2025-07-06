package gr.aueb.cf.projectmanagementapp.core.filters;

import lombok.*;
import org.springframework.lang.Nullable;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TicketFilters extends GenericFilters{
    @Nullable
    String uuid;

    @Nullable
    String title;

    @Nullable
    List<String> status;

    @Nullable
    List<String> priority;

    @Nullable
    LocalDate expiryDate;

    @Nullable
    String projectUuid;

    @Nullable
    String ownerUuid;
}
