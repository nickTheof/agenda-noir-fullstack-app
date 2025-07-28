package gr.aueb.cf.projectmanagementapp.service;

import gr.aueb.cf.projectmanagementapp.core.enums.ProjectStatus;
import gr.aueb.cf.projectmanagementapp.core.exceptions.AppObjectDeletionConflictException;
import gr.aueb.cf.projectmanagementapp.core.exceptions.AppObjectNotFoundException;
import gr.aueb.cf.projectmanagementapp.dto.ProjectCreateDTO;
import gr.aueb.cf.projectmanagementapp.dto.ProjectPatchDTO;
import gr.aueb.cf.projectmanagementapp.dto.ProjectReadOnlyDTO;
import gr.aueb.cf.projectmanagementapp.dto.ProjectUpdateDTO;
import gr.aueb.cf.projectmanagementapp.mapper.Mapper;
import gr.aueb.cf.projectmanagementapp.model.Project;
import gr.aueb.cf.projectmanagementapp.model.Ticket;
import gr.aueb.cf.projectmanagementapp.model.User;
import gr.aueb.cf.projectmanagementapp.repository.ProjectRepository;
import gr.aueb.cf.projectmanagementapp.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserProjectServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private Mapper mapper;

    @InjectMocks
    private UserProjectService userProjectService;

    private User testUser;
    private Project testProject1;
    private ProjectReadOnlyDTO testProjectReadOnlyDTO1;
    private Project testProject2;
    private ProjectReadOnlyDTO testProjectReadOnlyDTO2;
    private final String testUuid = "testUuid";

    @BeforeEach
    void setUp() {
        testUser = User.builder().
                uuid(testUuid).
                build();
        testProject1 = new Project(1L, "uuid1", "name1", "description1", null, false, ProjectStatus.OPEN, testUser, new HashSet<>());
        testProject2 = new Project(2L, "uuid2", "name2", "description2", null, false, ProjectStatus.OPEN, testUser, new HashSet<>());
        testUser.addProject(testProject1);
        testUser.addProject(testProject2);
        testProjectReadOnlyDTO1 = new ProjectReadOnlyDTO(testProject1.getId(), testProject1.getUuid(), testProject1.getName(), testProject1.getDescription(), testProject1.getOwner().getUuid(), testProject1.getStatus().name(), testProject1.getIsDeleted());
        testProjectReadOnlyDTO2 = new ProjectReadOnlyDTO(testProject2.getId(), testProject2.getUuid(), testProject2.getName(), testProject2.getDescription(), testProject2.getOwner().getUuid(), testProject2.getStatus().name(), testProject2.getIsDeleted());
    }

    @Test
    void testFindAllUserProjectsWhenUserNotFoundThrowsException() {
        when(userRepository.findByUuid(testUuid)).thenReturn(Optional.empty());

        assertThrows(AppObjectNotFoundException.class, () -> userProjectService.findAllUserProjects(testUuid));
    }

    @Test
    void testFindAllUserProjectsWhenUserExistsReturnsUserProjects() throws AppObjectNotFoundException {
        when(userRepository.findByUuid(testUuid)).thenReturn(Optional.of(testUser));
        when(mapper.mapToProjectReadOnlyDTO(testProject1)).thenReturn(testProjectReadOnlyDTO1);
        when(mapper.mapToProjectReadOnlyDTO(testProject2)).thenReturn(testProjectReadOnlyDTO2);

        List<ProjectReadOnlyDTO> result = userProjectService.findAllUserProjects(testUuid);


        assertEquals(2, result.size());
        assertTrue(result.contains(testProjectReadOnlyDTO1));
        assertTrue(result.contains(testProjectReadOnlyDTO2));

    }

    @Test
    void testFindUserProjectByUuidWhenUserNotFoundThrowsException() {
        when(userRepository.existsByUuid(testUuid)).thenReturn(false);
        assertThrows(AppObjectNotFoundException.class, () -> userProjectService.findUserProjectByUuid(testUuid, testProject1.getUuid()));
    }

    @Test
    void testFindUserProjectByUuidWhenUserExistsButProjectNotFoundThrowsException() {
        when(userRepository.existsByUuid(testUuid)).thenReturn(true);
        when(projectRepository.findByUuidAndOwnerUuid(testProject1.getUuid(), testUuid)).thenReturn(Optional.empty());
        assertThrows(AppObjectNotFoundException.class, () -> userProjectService.findUserProjectByUuid(testUuid, testProject1.getUuid()));
    }

    @Test
    void testFindUserProjectByUuidWhenUserExistsReturnsUserProject() throws AppObjectNotFoundException {
        when(userRepository.existsByUuid(testUuid)).thenReturn(true);
        when(projectRepository.findByUuidAndOwnerUuid(testProject1.getUuid(), testUuid)).thenReturn(Optional.of(testProject1));
        when(mapper.mapToProjectReadOnlyDTO(testProject1)).thenReturn(testProjectReadOnlyDTO1);

        ProjectReadOnlyDTO result = userProjectService.findUserProjectByUuid(testUuid, testProject1.getUuid());
        assertEquals(testProjectReadOnlyDTO1, result);
    }

    @Test
    void testCreateUserProjectWhenUserNotFoundThrowsException() {
        ProjectCreateDTO createDTO = new ProjectCreateDTO("New Project", "Description", "OPEN");

        when(userRepository.findByUuid(testUuid)).thenReturn(Optional.empty());

        assertThrows(AppObjectNotFoundException.class, () -> userProjectService.createUserProject(testUuid, createDTO));
    }


    @Test
    void testCreateUserProjectShouldCreateAndReturnProject() throws AppObjectNotFoundException {
        ProjectCreateDTO createDTO = new ProjectCreateDTO("New Project", "Description", "OPEN");
        Project newProject = new Project(3L, "uuid3", "name3", "description3", null, false, ProjectStatus.OPEN, testUser, new HashSet<>());
        ProjectReadOnlyDTO projectDto = new ProjectReadOnlyDTO(newProject.getId(), newProject.getUuid(), newProject.getName(), newProject.getDescription(), newProject.getOwner().getUuid(), newProject.getStatus().name(), newProject.getIsDeleted());

        when(userRepository.findByUuid(testUuid)).thenReturn(Optional.of(testUser));
        when(mapper.mapToProject(createDTO)).thenReturn(newProject);
        when(projectRepository.save(newProject)).thenReturn(newProject);
        when(mapper.mapToProjectReadOnlyDTO(newProject)).thenReturn(projectDto);

        ProjectReadOnlyDTO result = userProjectService.createUserProject(testUuid, createDTO);

        assertEquals(newProject.getUuid(), result.uuid());
        verify(projectRepository).save(newProject);
    }

    @Test
    void testUpdateUserProjectWithUpdateDTOWhenUserNotFoundShouldThrowException() {
        ProjectUpdateDTO updateDTO = new ProjectUpdateDTO("Updated Project", "New Desc", "ON_GOING", false);

        when(userRepository.existsByUuid(testUuid)).thenReturn(false);

        assertThrows(AppObjectNotFoundException.class, () -> userProjectService.updateUserProject(testUuid, testProject1.getUuid(), updateDTO));
    }

    @Test
    void testUpdateUserProjectWithUpdateDTOWhenUserExistsButProjectNotFoundShouldThrowException(){
        ProjectUpdateDTO updateDTO = new ProjectUpdateDTO("Updated Project", "New Desc", "ON_GOING", false);

        when(userRepository.existsByUuid(testUuid)).thenReturn(true);
        when(projectRepository.findByUuidAndOwnerUuid(testProject1.getUuid(), testUuid)).thenReturn(Optional.empty());

        assertThrows(AppObjectNotFoundException.class, () -> userProjectService.updateUserProject(testUuid, testProject1.getUuid(), updateDTO));
    }

    @Test
    void testUpdateUserProjectWithUpdateDTOShouldUpdateProject() throws AppObjectNotFoundException {
        ProjectUpdateDTO updateDTO = new ProjectUpdateDTO("Updated Project", "New Desc", "ON_GOING", false);
        Project updatedProject = new Project(1L, "uuid1", updateDTO.name(), updateDTO.description(), null, false, ProjectStatus.valueOf(updateDTO.status()), testUser, new HashSet<>());
        ProjectReadOnlyDTO projectDto = new ProjectReadOnlyDTO(testProject1.getId(), testProject1.getUuid(), updatedProject.getName(), updatedProject.getDescription(), testProject1.getOwner().getUuid(), updatedProject.getStatus().name(), testProject1.getIsDeleted());

        when(userRepository.existsByUuid(testUuid)).thenReturn(true);
        when(projectRepository.findByUuidAndOwnerUuid(testProject1.getUuid(), testUuid)).thenReturn(Optional.of(testProject1));
        when(mapper.mapToProject(updateDTO, testProject1)).thenReturn(updatedProject);
        when(projectRepository.save(updatedProject)).thenReturn(updatedProject);
        when(mapper.mapToProjectReadOnlyDTO(updatedProject)).thenReturn(projectDto);

        ProjectReadOnlyDTO result = userProjectService.updateUserProject(testUuid, testProject1.getUuid(), updateDTO);

        assertEquals(updateDTO.name(), result.name());
        assertEquals(updateDTO.description(), result.description());
        assertEquals(updateDTO.status(), result.status());
    }

    @Test
    void testUpdateUserProjectWithPatchDTOWhenUserNotFoundShouldThrowException() {
        ProjectPatchDTO patchDTO = new ProjectPatchDTO("Updated Project", "New Desc", "ON_GOING", false);

        when(userRepository.existsByUuid(testUuid)).thenReturn(false);

        assertThrows(AppObjectNotFoundException.class, () -> userProjectService.updateUserProject(testUuid, testProject1.getUuid(), patchDTO));
    }

    @Test
    void testUpdateUserProjectWithPatchDTOWhenUserExistsButProjectNotFoundShouldThrowException(){
        ProjectPatchDTO patchDTO = new ProjectPatchDTO("Updated Project", "New Desc", "ON_GOING", false);

        when(userRepository.existsByUuid(testUuid)).thenReturn(true);
        when(projectRepository.findByUuidAndOwnerUuid(testProject1.getUuid(), testUuid)).thenReturn(Optional.empty());

        assertThrows(AppObjectNotFoundException.class, () -> userProjectService.updateUserProject(testUuid, testProject1.getUuid(), patchDTO));
    }

    @Test
    void testUpdateUserProjectWithPatchDTOShouldUpdateProject() throws AppObjectNotFoundException {
        ProjectPatchDTO patchDTO = new ProjectPatchDTO("Updated Project", "New Desc", "ON_GOING", false);
        Project updatedProject = new Project(1L, "uuid1", patchDTO.name(), patchDTO.description(), null, false, ProjectStatus.valueOf(patchDTO.status()), testUser, new HashSet<>());
        ProjectReadOnlyDTO projectDto = new ProjectReadOnlyDTO(testProject1.getId(), testProject1.getUuid(), updatedProject.getName(), updatedProject.getDescription(), testProject1.getOwner().getUuid(), updatedProject.getStatus().name(), testProject1.getIsDeleted());

        when(userRepository.existsByUuid(testUuid)).thenReturn(true);
        when(projectRepository.findByUuidAndOwnerUuid(testProject1.getUuid(), testUuid)).thenReturn(Optional.of(testProject1));
        when(mapper.mapToProject(patchDTO, testProject1)).thenReturn(updatedProject);
        when(projectRepository.save(updatedProject)).thenReturn(updatedProject);
        when(mapper.mapToProjectReadOnlyDTO(updatedProject)).thenReturn(projectDto);

        ProjectReadOnlyDTO result = userProjectService.updateUserProject(testUuid, testProject1.getUuid(), patchDTO);

        assertEquals(patchDTO.name(), result.name());
        assertEquals(patchDTO.description(), result.description());
        assertEquals(patchDTO.status(), result.status());
    }

    @Test
    void testDeleteUserProjectWhenUserNotFoundShouldThrowException() {
        when(userRepository.findByUuid(testUuid)).thenReturn(Optional.empty());

        assertThrows(AppObjectNotFoundException.class, () ->
                userProjectService.deleteUserProject(testUuid, testProject1.getUuid())
        );
    }

    @Test
    void testDeleteUserProjectWhenUserExistsButProjectNotFoundShouldThrowException(){
        when(userRepository.findByUuid(testUuid)).thenReturn(Optional.of(testUser));
        when(projectRepository.findByUuidAndOwnerUuid(testProject1.getUuid(), testUuid)).thenReturn(Optional.empty());

        assertThrows(AppObjectNotFoundException.class, () ->
                userProjectService.deleteUserProject(testUuid, testProject1.getUuid())
        );
    }

    @Test
    void testDeleteUserProjectWhenNoTicketsShouldDelete() throws AppObjectNotFoundException, AppObjectDeletionConflictException {
        when(userRepository.findByUuid(testUuid)).thenReturn(Optional.of(testUser));
        when(projectRepository.findByUuidAndOwnerUuid(testProject1.getUuid(), testUuid)).thenReturn(Optional.of(testProject1));

        userProjectService.deleteUserProject(testUuid, testProject1.getUuid());

        verify(userRepository).save(testUser);
        assertFalse(testUser.getAllProjects().contains(testProject1));
    }

    @Test
    void testDeleteUserProjectWhenProjectHasTicketsShouldThrowException() {
        Ticket ticket = new Ticket();
        testProject1.addTicket(ticket);

        when(userRepository.findByUuid(testUuid)).thenReturn(Optional.of(testUser));
        when(projectRepository.findByUuidAndOwnerUuid(testProject1.getUuid(),testUuid)).thenReturn(Optional.of(testProject1));

        assertThrows(AppObjectDeletionConflictException.class, () ->
                userProjectService.deleteUserProject(testUuid, testProject1.getUuid())
        );
    }

}