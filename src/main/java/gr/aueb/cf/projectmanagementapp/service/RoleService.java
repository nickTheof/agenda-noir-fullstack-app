package gr.aueb.cf.projectmanagementapp.service;

import gr.aueb.cf.projectmanagementapp.core.exceptions.AppObjectAlreadyExistsException;
import gr.aueb.cf.projectmanagementapp.core.exceptions.AppObjectDeletionConflictException;
import gr.aueb.cf.projectmanagementapp.core.exceptions.AppObjectInvalidArgumentException;
import gr.aueb.cf.projectmanagementapp.core.exceptions.AppObjectNotFoundException;
import gr.aueb.cf.projectmanagementapp.dto.RoleCreateDTO;
import gr.aueb.cf.projectmanagementapp.dto.RoleReadOnlyDTO;
import gr.aueb.cf.projectmanagementapp.dto.RoleUpdateDTO;
import gr.aueb.cf.projectmanagementapp.mapper.Mapper;
import gr.aueb.cf.projectmanagementapp.model.Role;
import gr.aueb.cf.projectmanagementapp.model.static_data.Permission;
import gr.aueb.cf.projectmanagementapp.repository.PermissionRepository;
import gr.aueb.cf.projectmanagementapp.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoleService implements IRoleService {
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final Mapper mapper;

    @Override
    public List<RoleReadOnlyDTO> findAllRoles() {
        List<Role> roles =  roleRepository.findAll();
        return roles.stream().map(mapper::mapToRoleReadOnlyDTO).collect(Collectors.toList());
    }

    @Override
    public RoleReadOnlyDTO findRoleById(Long id) throws AppObjectNotFoundException {
        Role role = roleRepository.findById(id).orElseThrow(() -> new AppObjectNotFoundException("Role", "Role with id " + id + " not found"));
        return mapper.mapToRoleReadOnlyDTO(role);
    }

    @Transactional
    @Override
    public RoleReadOnlyDTO createRole(RoleCreateDTO dto) throws AppObjectAlreadyExistsException, AppObjectInvalidArgumentException {
        if (roleRepository.findByName(dto.name()).isPresent()) throw new AppObjectAlreadyExistsException("Role", "Role with name " + dto
                .name() + " already exists");
        // Verify all permissions exist
        Set<Permission> permissions = permissionRepository
                .findByNameIn(dto.permissions());

        if (permissions.size() != dto.permissions().size()) {
            throw new AppObjectInvalidArgumentException("Permissions", "Invalid permission were given.");
        }
        Role newRole = new Role(null, dto.name(), null, permissions);
        Role savedRole = roleRepository.save(newRole);
        return mapper.mapToRoleReadOnlyDTO(savedRole);
    }

    @Transactional
    @Override
    public RoleReadOnlyDTO updateRole(Long id, RoleUpdateDTO dto) throws AppObjectAlreadyExistsException, AppObjectInvalidArgumentException, AppObjectNotFoundException {
        Optional<Role> fetchedRole = roleRepository.findById(id);
        if (fetchedRole.isEmpty()) throw new AppObjectNotFoundException("Role", "Role with id " + id + " not found");
        Optional<Role> roleByName = roleRepository.findByName(dto.name());
        if (roleByName.isPresent() && !roleByName.get().getId().equals(id)) throw new AppObjectAlreadyExistsException("Role", "Role with name " + dto.name() + " already exists");
        // Verify all permissions exist
        Set<Permission> permissions = permissionRepository
                .findByNameIn(dto.permissions());

        if (permissions.size() != dto.permissions().size()) {
            throw new AppObjectInvalidArgumentException("Permissions", "Invalid permission were given.");
        }
        fetchedRole.get().setName(dto.name());
        fetchedRole.get().setPermissions(permissions);
        Role updatedRole = roleRepository.save(fetchedRole.get());
        return mapper.mapToRoleReadOnlyDTO(updatedRole);
    }

    @Transactional
    @Override
    public void deleteRole(Long id) throws AppObjectNotFoundException, AppObjectDeletionConflictException {
        Role role = roleRepository.findById(id).orElseThrow(() -> new AppObjectNotFoundException("Role", "Role with id " + id + " not found"));
        if (!role.getAllUsers().isEmpty()) throw new AppObjectDeletionConflictException("Role", "Role with id " + id + " cannot be deleted as there are users that are assigned with the specified role.");
        roleRepository.delete(role);
    }
}
