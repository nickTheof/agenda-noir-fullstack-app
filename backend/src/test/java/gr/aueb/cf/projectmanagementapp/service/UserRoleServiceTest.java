package gr.aueb.cf.projectmanagementapp.service;

import gr.aueb.cf.projectmanagementapp.core.exceptions.AppObjectNotFoundException;
import gr.aueb.cf.projectmanagementapp.dto.RoleReadOnlyDTO;
import gr.aueb.cf.projectmanagementapp.dto.UserRoleInsertDTO;
import gr.aueb.cf.projectmanagementapp.mapper.Mapper;
import gr.aueb.cf.projectmanagementapp.model.Role;
import gr.aueb.cf.projectmanagementapp.model.User;
import gr.aueb.cf.projectmanagementapp.repository.RoleRepository;
import gr.aueb.cf.projectmanagementapp.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserRoleServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private Mapper mapper;

    @InjectMocks
    private UserRoleService userRoleService;

    private User testUser;
    private Role testRole1, testRole2;
    private final String testUuid = "uuid";
    private final String testRoleName1 = "ADMIN";
    private final String testRoleName2 = "USER";

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setUuid(testUuid);

        testRole1 = new Role();
        testRole1.setName(testRoleName1);

        testRole2 = new Role();
        testRole2.setName(testRoleName2);
    }

    @Test
    void testFindAllUserRolesWhenUserExistsShouldReturnRoles() throws AppObjectNotFoundException {
        testUser.addRole(testRole1);
        testUser.addRole(testRole2);
        RoleReadOnlyDTO dto1 = new RoleReadOnlyDTO(1L, testRoleName1, null);
        RoleReadOnlyDTO dto2 = new RoleReadOnlyDTO(2L, testRoleName2, null);

        when(userRepository.findByUuid(testUuid)).thenReturn(Optional.of(testUser));
        when(mapper.mapToRoleReadOnlyDTO(testRole1)).thenReturn(dto1);
        when(mapper.mapToRoleReadOnlyDTO(testRole2)).thenReturn(dto2);

        List<RoleReadOnlyDTO> result = userRoleService.findAllUserRoles(testUuid);

        assertEquals(2, result.size());
        assertTrue(result.contains(dto1));
        assertTrue(result.contains(dto2));
    }

    @Test
    void testFindAllUserRolesWhenUserNotFoundShouldThrowException() {
        when(userRepository.findByUuid(testUuid)).thenReturn(Optional.empty());

        assertThrows(AppObjectNotFoundException.class, () ->
                userRoleService.findAllUserRoles(testUuid)
        );
    }

    @Test
    void testChangeUserRolesShouldUpdateRolesCorrectly() throws AppObjectNotFoundException {
        testUser.addRole(testRole1);
        UserRoleInsertDTO insertDTO = new UserRoleInsertDTO(List.of(testRoleName2));
        RoleReadOnlyDTO resultDto = new RoleReadOnlyDTO(2L, testRoleName2, null);

        when(userRepository.findByUuid(testUuid)).thenReturn(Optional.of(testUser));
        when(roleRepository.findByName(testRoleName2)).thenReturn(Optional.of(testRole2));
        when(userRepository.save(testUser)).thenReturn(testUser);
        when(mapper.mapToRoleReadOnlyDTO(testRole2)).thenReturn(resultDto);

        List<RoleReadOnlyDTO> result = userRoleService.changeUserRoles(testUuid, insertDTO);

        assertEquals(1, result.size());
        assertEquals(resultDto, result.getFirst());
        verify(userRepository).save(testUser);
        assertFalse(testUser.getAllRoles().contains(testRole1));
        assertTrue(testUser.getAllRoles().contains(testRole2));
    }

    @Test
    void testChangeUserRolesWhenUserNotFoundShouldThrowException() {
        UserRoleInsertDTO insertDTO = new UserRoleInsertDTO(List.of(testRoleName1));

        when(userRepository.findByUuid(testUuid)).thenReturn(Optional.empty());

        assertThrows(AppObjectNotFoundException.class, () ->
                userRoleService.changeUserRoles(testUuid, insertDTO)
        );
    }

    @Test
    void testChangeUserRolesWhenRoleNotFoundShouldThrowException() {
        UserRoleInsertDTO insertDTO = new UserRoleInsertDTO(List.of("NON_EXISTENT_ROLE"));

        when(userRepository.findByUuid(testUuid)).thenReturn(Optional.of(testUser));
        when(roleRepository.findByName("NON_EXISTENT_ROLE")).thenReturn(Optional.empty());

        assertThrows(AppObjectNotFoundException.class, () ->
                userRoleService.changeUserRoles(testUuid, insertDTO)
        );
    }

    @Test
    void testChangeUserRolesShouldRemoveAllExistingRoles() throws AppObjectNotFoundException {
        testUser.addRole(testRole1);
        testUser.addRole(testRole2);
        UserRoleInsertDTO insertDTO = new UserRoleInsertDTO(List.of());

        when(userRepository.findByUuid(testUuid)).thenReturn(Optional.of(testUser));
        when(userRepository.save(testUser)).thenReturn(testUser);

        List<RoleReadOnlyDTO> result = userRoleService.changeUserRoles(testUuid, insertDTO);

        assertTrue(result.isEmpty());
        assertTrue(testUser.getAllRoles().isEmpty());
    }
}