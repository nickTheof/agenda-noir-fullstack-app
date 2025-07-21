package gr.aueb.cf.projectmanagementapp.core.filters;

import lombok.*;
import org.springframework.lang.Nullable;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProjectFilters extends GenericFilters{
    @Nullable
    String uuid;

    @Nullable
    String name;

    @Nullable
    Boolean isDeleted;

    @Nullable
    List<String> status;

    @Nullable
    String ownerUuid;
}
