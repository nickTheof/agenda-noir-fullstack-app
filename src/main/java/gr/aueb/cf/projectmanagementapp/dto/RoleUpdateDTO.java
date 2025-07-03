package gr.aueb.cf.projectmanagementapp.dto;

import java.util.List;

public record RoleUpdateDTO(
        String name,
        List<String> permissions
) {
}
