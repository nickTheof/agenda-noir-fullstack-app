package gr.aueb.cf.projectmanagementapp.dto;

import java.util.List;

public record ProjectFiltersDTO(
        Integer page,
        Integer size,
        String sortBy,
        String orderBy,
        String uuid,
        String name,
        Boolean isDeleted,
        List<String> status

) {
    public ProjectFiltersDTO() {
        this(null, null, null, null, null, null, null, null);
    }
}
