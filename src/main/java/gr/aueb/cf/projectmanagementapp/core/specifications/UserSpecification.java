package gr.aueb.cf.projectmanagementapp.core.specifications;

import gr.aueb.cf.projectmanagementapp.model.Role;
import gr.aueb.cf.projectmanagementapp.model.User;
import gr.aueb.cf.projectmanagementapp.model.static_data.Permission;
import jakarta.persistence.criteria.Join;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Set;

public class UserSpecification {
    private static final Set<String> allowedLikeFields = Set.of("uuid", "username", "lastname");
    private static final Set<String> allowedBooleanFields = Set.of("verified", "enabled", "isDeleted");

    private UserSpecification() {

    }

    public static Specification<User> usersFieldLike(String field, String value) {
        return ((root, query, builder) -> {
            if (field == null || field.isBlank() || !allowedLikeFields.contains(field)) return builder.conjunction();
            if (value == null || value.isBlank()) return builder.conjunction();
            return builder.like(builder.upper(root.get(field)), value.toUpperCase() + "%");
        });
    }

    public static Specification<User> usersBooleanFieldIs(String field, Boolean value) {
        return ((root, query, builder) -> {
            if (field == null || field.isBlank() || !allowedBooleanFields.contains(field)) return builder.conjunction();
            if (value == null) return builder.conjunction();
            return builder.equal(root.get(field), value);
        });
    }

    public static Specification<User> userPermissionIn(List<String> permissionsList) {
        return (root, query, builder) -> {
            if (permissionsList == null || permissionsList.isEmpty()) return builder.conjunction();
            Join<User, Role> roleJoin = root.join("roles");
            Join<User, Permission> permissionJoin = roleJoin.join("permissions");
            return permissionJoin.get("name").in(permissionsList);
        };
    }

}
