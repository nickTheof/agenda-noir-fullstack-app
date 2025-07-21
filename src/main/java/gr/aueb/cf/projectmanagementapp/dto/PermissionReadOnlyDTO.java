package gr.aueb.cf.projectmanagementapp.dto;

public record PermissionReadOnlyDTO(
        Long id,
        String name,
        String resource,
        String action
) {
}
