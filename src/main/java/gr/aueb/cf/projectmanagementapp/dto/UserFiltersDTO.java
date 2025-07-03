package gr.aueb.cf.projectmanagementapp.dto;

import java.util.List;

public record UserFiltersDTO(
        Integer page,
        Integer size,
        String sortBy,
        String orderBy,
        String uuid,
        String username,
        String lastname,
        Boolean enabled,
        Boolean verified,
        Boolean isDeleted,
        List<String> permissions

) {
    public UserFiltersDTO() {
        this(null, null, null, null, null, null, null, null, null, null, null);
    }
}
