package gr.aueb.cf.projectmanagementapp.dto;

import java.util.List;

public record RoleCreateDTO(
        String name,
        List<String> permissions
) {
}
