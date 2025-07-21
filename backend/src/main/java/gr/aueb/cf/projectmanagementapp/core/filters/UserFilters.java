package gr.aueb.cf.projectmanagementapp.core.filters;

import lombok.*;
import org.springframework.lang.Nullable;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserFilters extends GenericFilters {
    @Nullable
    String uuid;

    @Nullable
    String username;

    @Nullable
    String lastname;

    @Nullable
    Boolean enabled;

    @Nullable
    Boolean isDeleted;

    @Nullable
    Boolean verified;

    @Nullable
    List<String> permissions;
}
