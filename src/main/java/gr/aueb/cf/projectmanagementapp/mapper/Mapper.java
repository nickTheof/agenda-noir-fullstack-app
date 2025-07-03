package gr.aueb.cf.projectmanagementapp.mapper;

import gr.aueb.cf.projectmanagementapp.core.filters.UserFilters;
import gr.aueb.cf.projectmanagementapp.dto.*;
import gr.aueb.cf.projectmanagementapp.model.Role;
import gr.aueb.cf.projectmanagementapp.model.User;
import gr.aueb.cf.projectmanagementapp.model.static_data.Permission;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.stream.Collectors;


@Component
@RequiredArgsConstructor
public class Mapper {
    private final PasswordEncoder passwordEncoder;

    public User mapToUser(UserRegisterDTO dto) {
        User user = new User();
        user.setUsername(dto.username());
        user.setPassword(passwordEncoder.encode(dto.password()));
        user.setFirstname(dto.firstname());
        user.setLastname(dto.lastname());
        return user;
    }

    public UserReadOnlyDTO mapToUserReadOnlyDTO(User user) {
        return new UserReadOnlyDTO(user.getId(), user.getUuid(), user.getUsername(), user.getFirstname(), user.getLastname(), user.getEnabled(), user.getVerified(), user.getIsDeleted(), user.getLoginConsecutiveFailAttempts());
    }

    public User mapToUser(UserUpdateDTO dto, User user) {
        if (dto.username() != null) {
            user.setUsername(dto.username());
        }
        if (dto.password() != null) {
            user.updatePassword(passwordEncoder.encode(dto.password()));
        }
        if (dto.firstname() != null) {
            user.setFirstname(dto.firstname());
        }
        if (dto.lastname() != null) {
            user.setLastname(dto.lastname());
        }
        if (dto.enabled() != null) {
            user.setEnabled(dto.enabled());
        }
        if (dto.verified() != null) {
            user.setVerified(dto.verified());
        }
        if (dto.deleted() != null) {
            if (dto.deleted()) {
                if (!user.getIsDeleted()) {
                    user.setIsDeleted(true);
                    user.setDeletedAt(LocalDateTime.now());
                }
            } else {
                if (user.getIsDeleted()) {
                    user.setIsDeleted(false);
                    user.setDeletedAt(null);
                }
            }

        }
        return user;
    }

    public User mapToUser(UserPatchDTO dto, User user) {
        if (dto.verified() != null) {
            user.setVerified(dto.verified());
        }
        if (dto.deleted() != null) {
            if (dto.deleted()) {
                if (!user.getIsDeleted()) {
                    user.setIsDeleted(true);
                    user.setDeletedAt(LocalDateTime.now());
                }
            } else {
                if (user.getIsDeleted()) {
                    user.setIsDeleted(false);
                    user.setDeletedAt(null);
                }
            }
        }
        if (dto.enabled() != null) {
            user.setEnabled(dto.enabled());
        }
        return user;
    }

    public UserFilters mapToUserFilters(UserFiltersDTO dto) {
        UserFilters userFilters = new UserFilters();
        if (dto.page() != null) {
            userFilters.setPage(dto.page());
        }
        if (dto.size() != null) {
            userFilters.setSize(dto.size());
        }
        if (dto.sortBy() != null) {
            userFilters.setSortBy(dto.sortBy());
        }
        if (dto.orderBy() != null) {
            userFilters.setOrderBy(Sort.Direction.valueOf(dto.orderBy()));
        }
        if (dto.username() != null) {
            userFilters.setUsername(dto.username());
        }
        if (dto.uuid() != null) {
            userFilters.setUuid(dto.uuid());
        }
        if (dto.lastname() != null) {
            userFilters.setLastname(dto.lastname());
        }
        if (dto.enabled() != null) {
            userFilters.setEnabled(dto.enabled());
        }
        if (dto.isDeleted() != null) {
            userFilters.setIsDeleted(dto.isDeleted());
        }
        if (dto.verified() != null) {
            userFilters.setVerified(dto.verified());
        }
        if (dto.permissions() != null) {
            userFilters.setPermissions(dto.permissions());
        }

        return userFilters;
    }

    public PermissionReadOnlyDTO mapToPermissionReadOnlyDTO(Permission permission) {
        return new PermissionReadOnlyDTO(
                permission.getId(), permission.getName(), permission.getResource().name(), permission.getAction().name()
        );
    }

    public RoleReadOnlyDTO mapToRoleReadOnlyDTO(Role role) {
        return new RoleReadOnlyDTO(
                role.getId(),
                role.getName(),
                role.getAllPermissions().stream().map(this::mapToPermissionReadOnlyDTO).collect(Collectors.toSet())
        );
    }
}
