package gr.aueb.cf.projectmanagementapp.dto;

public record ProjectReadOnlyDTO(
        Long id,
        String uuid,
        String name,
        String description,
        String ownerUuid,
        String status,
        Boolean deleted
) {
}
