package gr.aueb.cf.projectmanagementapp.model.static_data;

import gr.aueb.cf.projectmanagementapp.core.enums.Action;
import gr.aueb.cf.projectmanagementapp.core.enums.Resource;
import gr.aueb.cf.projectmanagementapp.model.Role;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "permissions",
        indexes = {
                @Index(name = "idx_permission_name", columnList = "name"),
                @Index(name = "idx_permission_resource_action", columnList = "resource,action")
        }
)
public class Permission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Resource resource;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Action action;

    @ManyToMany(mappedBy = "permissions")
    private Set<Role> roles = new HashSet<>();

    @PrePersist
    protected void onCreate() {
        this.name = this.action.name() + "_" + this.resource.name();
    }

}
