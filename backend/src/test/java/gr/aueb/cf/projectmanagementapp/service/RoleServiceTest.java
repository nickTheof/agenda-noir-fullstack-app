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
import gr.aueb.cf.projectmanagementapp.model.User;
import gr.aueb.cf.projectmanagementapp.model.static_data.Permission;
import gr.aueb.cf.projectmanagementapp.repository.PermissionRepository;
import gr.aueb.cf.projectmanagementapp.repository.RoleRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RoleServiceTest {

    @Mock
    private RoleRepository roleRepository;
    @Mock
    private PermissionRepository permissionRepository;
    @Mock
    private Mapper mapper;

    @InjectMocks
    private RoleService roleService;

    @Test
    void testFindAllRolesShouldReturnMappedDTOs() {
        Role role = new Role(1L, "ADMIN", null, Set.of());
        when(roleRepository.findAll()).thenReturn(List.of(role));
        RoleReadOnlyDTO dto = new RoleReadOnlyDTO(1L, "ADMIN", Set.of());
        when(mapper.mapToRoleReadOnlyDTO(role)).thenReturn(dto);

        List<RoleReadOnlyDTO> result = roleService.findAllRoles();

        assertEquals(1, result.size());
        assertEquals("ADMIN", result.getFirst().name());
    }

    @Test
    void testFindRoleByIdWhenExistsShouldReturnDTO() throws AppObjectNotFoundException {
        Role role = new Role(1L, "ADMIN", null, Set.of());
        when(roleRepository.findById(1L)).thenReturn(Optional.of(role));
        RoleReadOnlyDTO dto = new RoleReadOnlyDTO(1L, "ADMIN", Set.of());
        when(mapper.mapToRoleReadOnlyDTO(role)).thenReturn(dto);

        RoleReadOnlyDTO result = roleService.findRoleById(1L);

        assertEquals("ADMIN", result.name());
    }

    @Test
    void testFindRoleByIdWhenNotFoundShouldThrow() {
        when(roleRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(AppObjectNotFoundException.class, () -> roleService.findRoleById(1L));
    }

    @Test
    void testCreateRoleWhenNameExistsShouldThrowAlreadyExists() {
        RoleCreateDTO dto = new RoleCreateDTO("ADMIN", List.of("READ_USER"));

        when(roleRepository.findByName("ADMIN")).thenReturn(Optional.of(new Role()));

        assertThrows(AppObjectAlreadyExistsException.class, () -> roleService.createRole(dto));
    }

    @Test
    void testCreateRoleWhenPermissionsMismatchShouldThrowInvalidArgument() {
        RoleCreateDTO dto = new RoleCreateDTO("ADMIN", List.of("READ_USER", "CREATE_USER"));

        when(roleRepository.findByName("ADMIN")).thenReturn(Optional.empty());
        when(permissionRepository.findByNameIn(dto.permissions())).thenReturn(Set.of(new Permission()));

        assertThrows(AppObjectInvalidArgumentException.class, () -> roleService.createRole(dto));
    }

    @Test
    void testCreateRoleSuccessfulCreationShouldReturnDTO() throws Exception {
        RoleCreateDTO dto = new RoleCreateDTO("ADMIN", List.of("READ_USER", "CREATE_USER"));
        Set<Permission> permissions = Set.of(new Permission(), new Permission());

        when(roleRepository.findByName("ADMIN")).thenReturn(Optional.empty());
        when(permissionRepository.findByNameIn(dto.permissions())).thenReturn(permissions);
        when(roleRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(mapper.mapToRoleReadOnlyDTO(any())).thenReturn(new RoleReadOnlyDTO(1L, "ADMIN", Set.of()));

        RoleReadOnlyDTO result = roleService.createRole(dto);

        assertEquals("ADMIN", result.name());
    }

    @Test
    void testUpdateRoleWhenNotFoundShouldThrow() {
        RoleUpdateDTO dto = new RoleUpdateDTO("ADMIN", List.of("READ_USER"));
        when(roleRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(AppObjectNotFoundException.class, () -> roleService.updateRole(1L, dto));
    }

    @Test
    void testUpdateRoleWhenDuplicateNameShouldThrowAlreadyExists() {
        Role existing = new Role(1L, "ADMIN", null, Set.of());
        Role conflict = new Role(2L, "MANAGER", null, Set.of());
        RoleUpdateDTO dto = new RoleUpdateDTO("MANAGER", List.of("READ_USER"));

        when(roleRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(roleRepository.findByName("MANAGER")).thenReturn(Optional.of(conflict));

        assertThrows(AppObjectAlreadyExistsException.class, () -> roleService.updateRole(1L, dto));
    }

    @Test
    void testUpdateRoleWhenPermissionsMismatchShouldThrowInvalidArgument() {
        Role existing = new Role(1L, "ADMIN", null, Set.of());
        RoleUpdateDTO dto = new RoleUpdateDTO("ADMIN", List.of("READ_USER", "CREATE_USER"));

        when(roleRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(roleRepository.findByName("ADMIN")).thenReturn(Optional.of(existing));
        when(permissionRepository.findByNameIn(dto.permissions())).thenReturn(Set.of(new Permission()));

        assertThrows(AppObjectInvalidArgumentException.class, () -> roleService.updateRole(1L, dto));
    }

    @Test
    void testUpdateRoleSuccessfulUpdateShouldReturnDTO() throws Exception {
        Role existing = new Role(1L, "ADMIN", null, Set.of());
        RoleUpdateDTO dto = new RoleUpdateDTO("ADMIN", List.of("READ_USER"));
        Set<Permission> permissions = Set.of(new Permission());

        when(roleRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(roleRepository.findByName("ADMIN")).thenReturn(Optional.of(existing));
        when(permissionRepository.findByNameIn(dto.permissions())).thenReturn(permissions);
        when(roleRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(mapper.mapToRoleReadOnlyDTO(any())).thenReturn(new RoleReadOnlyDTO(1L, "ADMIN", Set.of()));

        RoleReadOnlyDTO result = roleService.updateRole(1L, dto);

        assertEquals("ADMIN", result.name());
    }

    @Test
    void testDeleteRoleWhenNotFoundShouldThrow() {
        when(roleRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(AppObjectNotFoundException.class, () -> roleService.deleteRole(1L));
    }

    @Test
    void testDeleteRoleWhenUsersAssignedShouldThrowConflict() {
        Role role = new Role(1L, "ADMIN", Set.of(new User()), Set.of());
        when(roleRepository.findById(1L)).thenReturn(Optional.of(role));

        assertThrows(AppObjectDeletionConflictException.class, () -> roleService.deleteRole(1L));
    }

    @Test
    void testDeleteRole_SuccessfullyDeletes() throws Exception {
        Role role = new Role(1L, "ADMIN", null, Set.of());
        when(roleRepository.findById(1L)).thenReturn(Optional.of(role));

        roleService.deleteRole(1L);

        verify(roleRepository).delete(role);
    }
}
