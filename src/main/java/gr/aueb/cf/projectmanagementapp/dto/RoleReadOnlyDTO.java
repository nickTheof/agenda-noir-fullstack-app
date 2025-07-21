package gr.aueb.cf.projectmanagementapp.dto;

import java.util.Set;

public record RoleReadOnlyDTO(
        Long id,
        String name,
        Set<PermissionReadOnlyDTO> permissions
) {
}
