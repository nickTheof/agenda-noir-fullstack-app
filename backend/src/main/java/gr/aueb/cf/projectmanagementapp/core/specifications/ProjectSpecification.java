package gr.aueb.cf.projectmanagementapp.core.specifications;

import gr.aueb.cf.projectmanagementapp.model.Project;
import gr.aueb.cf.projectmanagementapp.model.User;
import jakarta.persistence.criteria.Join;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Set;

public class ProjectSpecification {
    private static final Set<String> allowedLikeFields = Set.of("uuid", "name");
    private static final Set<String> allowedBooleanFields = Set.of("isDeleted");

    private ProjectSpecification() {}

    public static Specification<Project> projectsFieldLike(String field, String value) {
        return ((root, query, builder) -> {
            if (field == null || field.isBlank() || !allowedLikeFields.contains(field)) return builder.conjunction();
            if (value == null || value.isBlank()) return builder.conjunction();
            return builder.like(builder.upper(root.get(field)), value.toUpperCase() + "%");
        });
    }

    public static Specification<Project> projectsBooleanFieldIs(String field, Boolean value) {
        return ((root, query, builder) -> {
            if (field == null || field.isBlank() || !allowedBooleanFields.contains(field)) return builder.conjunction();
            if (value == null) return builder.conjunction();
            return builder.equal(root.get(field), value);
        });
    }

    public static Specification<Project> projectStatusIn(List<String> statusList) {
        return (root, query, builder) -> {
            if (statusList == null || statusList.isEmpty()) return builder.conjunction();
            return root.get("status").in(statusList);
        };
    }

    public static Specification<Project> projectsOwnerIs(String ownerUuid) {
        return (root, query, builder) -> {
            if (ownerUuid == null || ownerUuid.isBlank()) return builder.conjunction();
            Join<Project, User> userJoin = root.join("owner");
            return builder.equal(userJoin.get("uuid"), ownerUuid);
        };
    }
}
