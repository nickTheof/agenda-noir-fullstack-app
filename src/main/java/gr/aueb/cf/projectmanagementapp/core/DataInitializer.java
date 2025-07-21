package gr.aueb.cf.projectmanagementapp.core;

import gr.aueb.cf.projectmanagementapp.core.enums.Action;
import gr.aueb.cf.projectmanagementapp.core.enums.Resource;
import gr.aueb.cf.projectmanagementapp.model.Role;
import gr.aueb.cf.projectmanagementapp.model.User;
import gr.aueb.cf.projectmanagementapp.model.static_data.Permission;
import gr.aueb.cf.projectmanagementapp.repository.PermissionRepository;
import gr.aueb.cf.projectmanagementapp.repository.RoleRepository;
import gr.aueb.cf.projectmanagementapp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {
    private static final Logger LOGGER = LoggerFactory.getLogger(DataInitializer.class);

    private final PermissionRepository permissionRepository;
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${superuser.email}")
    private String superUserEmail;

    @Value("${superuser.password}")
    private String superUserPassword;

    @Value("${superuser.firstname}")
    private String superUserFirstname;

    @Value("${superuser.lastname}")
    private String superUserLastname;

    @Override
    @Transactional
    public void run(String... args) {
        try {
            initializeData();
        } catch (Exception e) {
            LOGGER.error("Data initialization failed", e);
        }
    }

    private void initializeData() {
        if (permissionRepository.count() == 0) {
            LOGGER.info("Initializing application data...");

            List<Permission> permissions = createPermissions();
            List<Permission> savedPermissions = permissionRepository.saveAll(permissions);
            LOGGER.info("Created {} permissions", permissions.size());

            Role superAdminRole = createSuperAdminRole(savedPermissions);
            Role savedRole = roleRepository.save(superAdminRole);
            LOGGER.info("Created Super Admin role");

            User superAdminUser = createSuperAdminUser(savedRole);
            userRepository.save(superAdminUser);
            LOGGER.info("Created Super Admin user: {}", superAdminUser.getUsername());
        } else {
            LOGGER.info("Data already initialized - skipping");
        }
    }

    private List<Permission> createPermissions() {
        return List.of(
                new Permission(null, "READ_USER", Resource.USER, Action.READ, new HashSet<>()),
                new Permission(null, "UPDATE_USER", Resource.USER, Action.UPDATE, new HashSet<>()),
                new Permission(null, "DELETE_USER", Resource.USER, Action.DELETE, new HashSet<>()),
                new Permission(null, "CREATE_USER", Resource.USER, Action.CREATE, new HashSet<>()),

                new Permission(null, "READ_ROLE", Resource.ROLE, Action.READ, new HashSet<>()),
                new Permission(null, "CREATE_ROLE", Resource.ROLE, Action.CREATE, new HashSet<>()),
                new Permission(null, "UPDATE_ROLE", Resource.ROLE, Action.UPDATE, new HashSet<>()),
                new Permission(null, "DELETE_ROLE", Resource.ROLE, Action.DELETE, new HashSet<>()),

                new Permission(null, "CREATE_PROJECT", Resource.PROJECT, Action.CREATE, new HashSet<>()),
                new Permission(null, "READ_PROJECT", Resource.PROJECT, Action.READ, new HashSet<>()),
                new Permission(null, "UPDATE_PROJECT", Resource.PROJECT, Action.UPDATE, new HashSet<>()),
                new Permission(null, "DELETE_PROJECT", Resource.PROJECT, Action.DELETE, new HashSet<>()),

                new Permission(null, "CREATE_TICKET", Resource.TICKET, Action.CREATE, new HashSet<>()),
                new Permission(null, "READ_TICKET", Resource.TICKET, Action.READ, new HashSet<>()),
                new Permission(null, "UPDATE_TICKET", Resource.TICKET, Action.UPDATE, new HashSet<>()),
                new Permission(null, "DELETE_TICKET", Resource.TICKET, Action.DELETE, new HashSet<>())
        );
    }

    private Role createSuperAdminRole(List<Permission> permissions) {
        Role role = new Role(null, "Super Admin", new HashSet<>(), new HashSet<>());
        for (Permission permission : permissions) {
            role.addPermission(permission);
        }
        return role;
    }

    private User createSuperAdminUser(Role role) {
        User superUser =  User.builder()
                .username(superUserEmail)
                .password(passwordEncoder.encode(superUserPassword))
                .firstname(superUserFirstname)
                .lastname(superUserLastname)
                .verified(true)
                .enabled(true)
                .build();
        superUser.addRole(role);
        return superUser;
    }
}