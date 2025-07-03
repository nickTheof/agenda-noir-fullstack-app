package gr.aueb.cf.projectmanagementapp.model;

import gr.aueb.cf.projectmanagementapp.model.static_data.Permission;
import jakarta.persistence.*;
import lombok.*;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "roles",
        indexes = {
                @Index(name = "idx_role_name", columnList = "name")
        })
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Getter(AccessLevel.PROTECTED)
    @ManyToMany(mappedBy = "roles")
    private Set<User> users = new HashSet<>();

    @Getter(AccessLevel.PROTECTED)
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "roles_permissions")
    private Set<Permission> permissions = new HashSet<>();

    public Set<Permission> getAllPermissions() {
        if (permissions == null) permissions = new HashSet<>();
        return Collections.unmodifiableSet(permissions);
    }

    public Set<User> getAllUsers() {
        if (users == null) users = new HashSet<>();
        return Collections.unmodifiableSet(users);
    }

    public void addPermission(Permission permission) {
        if (permission == null) return;
        if (permissions == null) permissions = new HashSet<>();
        permissions.add(permission);
        permission.getRoles().add(this);
    }

    public void removePermission(Permission permission) {
        if (permission == null) return;
        if (permissions == null) return;
        permissions.remove(permission);
        permission.getRoles().remove(this);
    }

    public boolean hasPermission(String permissionName) {
        return permissions.stream()
                .anyMatch(p -> p.getName().equalsIgnoreCase(permissionName));
    }

}
