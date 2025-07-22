package gr.aueb.cf.projectmanagementapp.mapper;

import gr.aueb.cf.projectmanagementapp.core.enums.*;
import gr.aueb.cf.projectmanagementapp.core.filters.ProjectFilters;
import gr.aueb.cf.projectmanagementapp.core.filters.TicketFilters;
import gr.aueb.cf.projectmanagementapp.core.filters.UserFilters;
import gr.aueb.cf.projectmanagementapp.dto.*;
import gr.aueb.cf.projectmanagementapp.model.Project;
import gr.aueb.cf.projectmanagementapp.model.Role;
import gr.aueb.cf.projectmanagementapp.model.Ticket;
import gr.aueb.cf.projectmanagementapp.model.User;
import gr.aueb.cf.projectmanagementapp.model.static_data.Permission;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class MapperTest {
    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private Mapper mapper;

    @Test
    void mapToUserShouldMapCorrectlyAndEncodePassword() {
        UserRegisterDTO dto = new UserRegisterDTO("username", "firstname", "lastname", "password");
        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");
        User user = mapper.mapToUser(dto);
        assertEquals("username", user.getUsername());
        assertEquals("firstname", user.getFirstname());
        assertEquals("lastname", user.getLastname());
        assertEquals("encodedPassword", user.getPassword());
    }

    @Test
    void mapToUserReadOnlyShouldMapCorrectly() {
        User user = new User(
                1L,
                "uuid",
                "username",
                "firstname",
                "lastname",
                "password",
                null,
                0,
                true,
                true,
                true,
                null,
                null,
                false,
                null,
                null,
                null,
                null
        );
        UserReadOnlyDTO dto = mapper.mapToUserReadOnlyDTO(user);
        assertEquals(1L, dto.id());
        assertEquals("uuid", dto.uuid());
        assertEquals("username", dto.username());
        assertEquals("firstname", dto.firstname());
        assertEquals("lastname", dto.lastname());
        assertTrue(dto.enabled());
        assertTrue(dto.verified());
        assertFalse(dto.isDeleted());
        assertEquals(0, dto.loginConsecutiveFailAttempts());
    }

    @Test
    void mapToUserShouldUpdateOnlyNonNullFields() {
        User user = new User();
        user.setUsername("old");
        user.setFirstname("old");
        user.setLastname("old");
        user.setEnabled(false);
        user.setVerified(false);
        user.setIsDeleted(false);

        UserUpdateDTO dto = new UserUpdateDTO("new", "Nik", null, null, true, true, true);
        User updated = mapper.mapToUser(dto, user);

        assertEquals("new", updated.getUsername());
        assertEquals("Nik", updated.getFirstname());
        assertEquals("old", updated.getLastname()); // not overwritten
        assertTrue(updated.getEnabled());
        assertTrue(updated.getVerified());
        assertTrue(updated.getIsDeleted());
        assertNotNull(updated.getDeletedAt());
    }

    @Test
    void mapToUserUpdateDeletedShouldChangeDeleted() {
        User user = new User();
        user.setIsDeleted(false);
        UserUpdateDTO dto = new UserUpdateDTO(null, null, null, null, null, null, true);
        User updated = mapper.mapToUser(dto, user);
        assertTrue(updated.getIsDeleted());
        assertNotNull(updated.getDeletedAt());
    }

    @Test
    void mapToUserUpdateDeletedShouldNotChangeDeletedIfAlreadyDeleted() {
        LocalDateTime deletedAt = LocalDateTime.of(2020, 1, 1, 0, 0);
        User user = new User();
        user.setIsDeleted(true);
        user.setDeletedAt(deletedAt);
        UserUpdateDTO dto = new UserUpdateDTO(null, null, null, null, null, null, true);
        User updated = mapper.mapToUser(dto, user);
        assertTrue(updated.getIsDeleted());
        assertEquals(deletedAt, updated.getDeletedAt());
    }

    @Test
    void mapToUserUpdateDeletedShouldRestoreIfPreviouslyDeleted() {
        LocalDateTime deletedAt = LocalDateTime.of(2022, 5, 10, 10, 0);
        User user = new User();
        user.setIsDeleted(true);
        user.setDeletedAt(deletedAt);

        UserUpdateDTO dto = new UserUpdateDTO(null, null, null, null, null, null, false);
        User updated = mapper.mapToUser(dto, user);

        assertFalse(updated.getIsDeleted());
        assertNull(updated.getDeletedAt());
    }

    @Test
    void mapToUserShouldPatchEnabledAndVerifiedFields() {
        UserPatchDTO dto = new UserPatchDTO(true, false, true);
        User user = new User();
        user.setVerified(false);
        user.setIsDeleted(false);
        user.setEnabled(false);

        mapper.mapToUser(dto, user);

        assertFalse(user.getVerified());
        assertTrue(user.getEnabled());
        assertTrue(user.getIsDeleted());
    }

    @Test
    void mapToUserPatchShouldSoftDeleteUser() {
        UserPatchDTO dto = new UserPatchDTO(null, null, true);
        User user = new User();
        user.setIsDeleted(false);

        mapper.mapToUser(dto, user);

        assertTrue(user.getIsDeleted());
        assertNotNull(user.getDeletedAt());
    }

    @Test
    void mapToUserPatchShouldRestoreUser() {
        UserPatchDTO dto = new UserPatchDTO(null, null, false);
        User user = new User();
        user.setIsDeleted(true);
        user.setDeletedAt(LocalDateTime.now());

        mapper.mapToUser(dto, user);

        assertFalse(user.getIsDeleted());
        assertNull(user.getDeletedAt());
    }

    @Test
    void mapToUserPatchShouldIgnoreNullFields() {
        UserPatchDTO dto = new UserPatchDTO(null, null, null);
        User user = new User();
        user.setVerified(false);
        user.setIsDeleted(false);
        user.setEnabled(true);

        mapper.mapToUser(dto, user);

        // No changes expected
        assertFalse(user.getVerified());
        assertFalse(user.getIsDeleted());
        assertTrue(user.getEnabled());
    }

    @Test
    void mapToUserFiltersShouldMapFieldsAndInheritPaginationBehavior() {
        // Given
        UserFiltersDTO dto = new UserFiltersDTO(
                2,
                10,
                "username",
                "DESC",
                "uuid",
                "username",
                "lastname",
                true,
                false,
                true,
                List.of("READ_USER", "DELETE_USER")
        );

        UserFilters filters = mapper.mapToUserFilters(dto);

        assertEquals(2, filters.getPage());
        assertEquals(10, filters.getSize());
        assertEquals("username", filters.getSortBy());
        assertEquals(Sort.Direction.DESC, filters.getOrderBy());
        assertEquals("username", filters.getUsername());
        assertEquals("uuid", filters.getUuid());
        assertEquals("lastname", filters.getLastname());
        assertEquals(Boolean.TRUE, filters.getEnabled());
        assertEquals(Boolean.TRUE, filters.getIsDeleted());
        assertEquals(Boolean.FALSE, filters.getVerified());
        assertEquals(List.of("READ_USER", "DELETE_USER"), filters.getPermissions());

        assertEquals(10, filters.getPageSize());
        assertEquals(Sort.by(Sort.Direction.DESC, "username"), filters.getSort());
        Pageable pageable = filters.getPageable();
        assertEquals(2, pageable.getPageNumber());
        assertEquals(10, pageable.getPageSize());
        assertEquals(Sort.by(Sort.Direction.DESC, "username"), pageable.getSort());
    }

    @Test
    void mapToUserFiltersShouldUseDefaultPaginationWhenValuesMissing() {
        // Given: DTO with nulls
        UserFiltersDTO dto = new UserFiltersDTO(null, null, null, null, null, null, null, null, null, null, null);

        // When
        UserFilters filters = mapper.mapToUserFilters(dto);

        // Then: test default values
        assertEquals(0, filters.getPage());
        assertEquals(15, filters.getPageSize());
        assertEquals(Sort.by(Sort.Direction.ASC, "id"), filters.getSort());

        Pageable pageable = filters.getPageable();
        assertEquals(0, pageable.getPageNumber());
        assertEquals(15, pageable.getPageSize());
        assertEquals(Sort.by("id"), pageable.getSort());
    }


    @Test
    void mapPermissionReadOnlyShouldMapCorrectly() {
        Permission permission = new Permission(1L, "READ_USER", Resource.USER, Action.READ, new HashSet<>());
        PermissionReadOnlyDTO dto = mapper.mapToPermissionReadOnlyDTO(permission);
        assertEquals(1L, dto.id());
        assertEquals(Resource.USER.name(), dto.resource());
        assertEquals(Action.READ.name(), dto.action());
        assertEquals("READ_USER", dto.name());
    }

    @Test
    void mapToRoleReadOnlyShouldMapCorrectly() {
        Permission permission1 = new Permission(1L, "READ_USER", Resource.USER, Action.READ, new HashSet<>());
        Permission permission2 = new Permission(2L, "CREATE_USER", Resource.USER, Action.CREATE, new HashSet<>());

        Role role = new Role();
        role.setId(1L);
        role.setName("ADMIN");
        role.addPermission(permission1);
        role.addPermission(permission2);

        RoleReadOnlyDTO dto = mapper.mapToRoleReadOnlyDTO(role);

        assertEquals(role.getId(), dto.id());
        assertEquals(role.getName(), dto.name());
        assertEquals(2, dto.permissions().size());

        Set<String> permissionNames = dto.permissions().stream()
                .map(PermissionReadOnlyDTO::name)
                .collect(Collectors.toSet());

        assertTrue(permissionNames.contains("READ_USER"));
        assertTrue(permissionNames.contains("CREATE_USER"));
    }

    @Test
    void mapToProjectReadOnlyShouldMapCorrectly() {
        User user = new User();
        user.setUuid("uuid");
        Project project =  new Project(1L, "uuid", "name", "description", null, false, ProjectStatus.OPEN, user, new HashSet<>());

        ProjectReadOnlyDTO dto = mapper.mapToProjectReadOnlyDTO(project);

        assertEquals(project.getId(), dto.id());
        assertEquals(project.getName(), dto.name());
        assertEquals(project.getDescription(), dto.description());
        assertEquals(project.getStatus().name(), dto.status());
        assertFalse(project.getIsDeleted());
        assertEquals(project.getOwner().getUuid(), dto.ownerUuid());
    }


    @Test
    void mapToProjectShouldMapCorrectlyFromCreateDTO() {
        ProjectCreateDTO dto = new ProjectCreateDTO("Project Name", "Description", ProjectStatus.OPEN.name());

        Project project = mapper.mapToProject(dto);

        assertEquals("Project Name", project.getName());
        assertEquals("Description", project.getDescription());
        assertEquals(ProjectStatus.OPEN, project.getStatus());
    }

    @Test
    void mapToProjectShouldPatchNameAndDeleteProperly() {
        Project project = new Project();
        project.setName("Old Name");
        project.setIsDeleted(false);

        ProjectPatchDTO dto = new ProjectPatchDTO("New Name", null, null, true);

        mapper.mapToProject(dto, project);

        assertEquals("New Name", project.getName());
        assertTrue(project.getIsDeleted());
        assertNotNull(project.getDeletedAt());
    }


    @Test
    void softDeleteLogicShouldToggleDeleteFlags() {
        Project project = new Project();
        project.setIsDeleted(false);

        ProjectPatchDTO deleteDto = new ProjectPatchDTO(null, null, null, true);
        mapper.mapToProject(deleteDto, project);
        assertTrue(project.getIsDeleted());
        assertNotNull(project.getDeletedAt());

        ProjectPatchDTO restoreDto = new ProjectPatchDTO(null, null, null, false);
        mapper.mapToProject(restoreDto, project);
        assertFalse(project.getIsDeleted());
        assertNull(project.getDeletedAt());
    }

    @Test
    void mapToProjectFiltersShouldMapFieldsAndInheritPaginationBehavior() {
        // Given
        ProjectFiltersDTO dto = new ProjectFiltersDTO(
                2,
                10,
                "uuid",
                "DESC",
                "uuid",
                "name",
                true,
                List.of("OPEN", "ON_GOING")
        );

        ProjectFilters filters = mapper.mapToProjectFilters(dto, "aaaa1234");

        assertEquals(2, filters.getPage());
        assertEquals(10, filters.getSize());
        assertEquals("uuid", filters.getSortBy());
        assertEquals(Sort.Direction.DESC, filters.getOrderBy());
        assertEquals("uuid", filters.getUuid());
        assertEquals("name", filters.getName());
        assertEquals(Boolean.TRUE, filters.getIsDeleted());
        assertEquals("aaaa1234", filters.getOwnerUuid());
        assertEquals(List.of("OPEN", "ON_GOING"), filters.getStatus());

        assertEquals(10, filters.getPageSize());
        assertEquals(Sort.by(Sort.Direction.DESC, "uuid"), filters.getSort());
        Pageable pageable = filters.getPageable();
        assertEquals(2, pageable.getPageNumber());
        assertEquals(10, pageable.getPageSize());
        assertEquals(Sort.by(Sort.Direction.DESC, "uuid"), pageable.getSort());
    }

    @Test
    void mapToProjectFiltersShouldUseDefaultPaginationWhenValuesMissing() {
        ProjectFiltersDTO dto = new ProjectFiltersDTO(null, null, null, null, null, null, null, null);

        ProjectFilters filters = mapper.mapToProjectFilters(dto, "aaaa1234");

        assertEquals(0, filters.getPage());
        assertEquals(15, filters.getPageSize());
        assertEquals(Sort.by(Sort.Direction.ASC, "id"), filters.getSort());
        assertEquals("aaaa1234", filters.getOwnerUuid());
        Pageable pageable = filters.getPageable();
        assertEquals(0, pageable.getPageNumber());
        assertEquals(15, pageable.getPageSize());
        assertEquals(Sort.by("id"), pageable.getSort());
    }

    @Test
    void mapToTicketReadOnlyShouldMapCorrectly() {
        LocalDate expiry = LocalDate.now().plusWeeks(1);

        Ticket ticket = new Ticket(
                1L,
                "uuid",
                "Fix Bug",
                "Fix a critical backend bug.",
                TicketPriority.HIGH,
                TicketStatus.ON_GOING,
                expiry,
                null
        );
        TicketReadOnlyDTO dto = mapper.mapToTicketReadOnlyDTO(ticket);
        assertEquals(ticket.getId(), dto.id());
        assertEquals(ticket.getUuid(), dto.uuid());
        assertEquals(ticket.getTitle(), dto.title());
        assertEquals(ticket.getDescription(), dto.description());
        assertEquals(ticket.getPriority().name(), dto.priority());
        assertEquals(ticket.getStatus().name(), dto.status());
        assertEquals(ticket.getExpiryDate(), dto.expiryDate());
    }

    @Test
    void mapToTicketShouldMapCorrectlyFromCreateDTO() {
        LocalDate expiry = LocalDate.now().plusWeeks(1);
        TicketCreateDTO dto = new TicketCreateDTO("title", "description", TicketPriority.LOW.name(), TicketStatus.OPEN.name(), expiry);
        Ticket ticket = mapper.mapToTicket(dto);
        assertNull(ticket.getId());
        assertNull(ticket.getUuid());
        assertNull(ticket.getProject());
        assertEquals("title", ticket.getTitle());
        assertEquals("description", ticket.getDescription());
        assertEquals(TicketPriority.LOW, ticket.getPriority());
        assertEquals(TicketStatus.OPEN, ticket.getStatus());
        assertEquals(expiry, ticket.getExpiryDate());
    }

    @Test
    void mapToTicketShouldUpdateProperly() {
        LocalDate expiry = LocalDate.now().plusWeeks(1);
        Ticket ticket = new Ticket();
        TicketUpdateDTO dto = new TicketUpdateDTO("title", "description", TicketPriority.LOW.name(), TicketStatus.OPEN.name(), expiry);
        Ticket updated = mapper.mapToTicket(dto, ticket);
        assertEquals(dto.title(), updated.getTitle());
        assertEquals(dto.description(), updated.getDescription());
        assertEquals(TicketPriority.valueOf(dto.priority()), updated.getPriority());
        assertEquals(TicketStatus.valueOf(dto.status()), updated.getStatus());
        assertEquals(expiry, updated.getExpiryDate());
    }

    @Test
    void mapToTicketShouldPatchProperly() {
        LocalDate oldExpiry = LocalDate.now().plusWeeks(1);
        LocalDate newExpiry = LocalDate.now().plusWeeks(1);
        Ticket ticket = new Ticket();
        ticket.setTitle("old title");
        ticket.setDescription("old description");
        ticket.setPriority(TicketPriority.HIGH);
        ticket.setStatus(TicketStatus.ON_GOING);
        ticket.setExpiryDate(oldExpiry);
        TicketPatchDTO dto = new TicketPatchDTO("title", "description", TicketPriority.LOW.name(), TicketStatus.OPEN.name(), newExpiry);
        Ticket updated = mapper.mapToTicket(dto, ticket);
        assertEquals(dto.title(), updated.getTitle());
        assertEquals(dto.description(), updated.getDescription());
        assertEquals(TicketPriority.valueOf(dto.priority()), updated.getPriority());
        assertEquals(TicketStatus.valueOf(dto.status()), updated.getStatus());
        assertEquals(newExpiry, updated.getExpiryDate());
    }

    @Test
    void mapToTicketFiltersShouldMapFieldsAndInheritPaginationBehavior() {
        TicketFiltersDTO dto = new TicketFiltersDTO(
                1, 20, "priority", "DESC",
                "uuid-123", "Fix login bug", List.of("OPEN"), List.of("CRITICAL"),
                LocalDate.of(2025, 1, 1)
        );
        String ownerUuid = "owner-uuid";
        String projectUuid = "project-uuid";

        // Act
        TicketFilters filters = mapper.mapToTicketFilters(dto, ownerUuid, projectUuid);

        // Assert
        assertEquals(1, filters.getPage());
        assertEquals(20, filters.getPageSize());
        assertEquals("priority", filters.getSortField());
        assertEquals(Sort.Direction.DESC, filters.getSortDirection());
        assertEquals("uuid-123", filters.getUuid());
        assertEquals("Fix login bug", filters.getTitle());
        assertEquals(List.of("OPEN"), filters.getStatus());
        assertEquals( List.of("CRITICAL"), filters.getPriority());
        assertEquals(LocalDate.of(2025, 1, 1), filters.getExpiryDate());
        assertEquals(ownerUuid, filters.getOwnerUuid());
        assertEquals(projectUuid, filters.getProjectUuid());
    }

    @Test
    void mapToTicketFiltersShouldUseDefaultPaginationWhenValuesMissing() {
        // Arrange
        TicketFiltersDTO dto = new TicketFiltersDTO(null, null, null, null, null, null, null, null, null);
        String ownerUuid = "owner-uuid";
        String projectUuid = "project-uuid";

        // Act
        TicketFilters filters = mapper.mapToTicketFilters(dto, ownerUuid, projectUuid);

        // Assert: Defaults and nulls
        assertEquals(0, filters.getPage());
        assertEquals(15, filters.getPageSize());
        assertEquals("id", filters.getSortField());
        assertEquals(Sort.Direction.ASC, filters.getSortDirection());
        assertNull(filters.getUuid());
        assertNull(filters.getTitle());
        assertNull(filters.getStatus());
        assertNull(filters.getPriority());
        assertNull(filters.getExpiryDate());
        assertEquals(ownerUuid, filters.getOwnerUuid());
        assertEquals(projectUuid, filters.getProjectUuid());
    }

}