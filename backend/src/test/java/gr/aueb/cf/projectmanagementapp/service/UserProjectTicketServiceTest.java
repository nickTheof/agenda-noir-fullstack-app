package gr.aueb.cf.projectmanagementapp.service;

import gr.aueb.cf.projectmanagementapp.core.enums.ProjectStatus;
import gr.aueb.cf.projectmanagementapp.core.enums.TicketPriority;
import gr.aueb.cf.projectmanagementapp.core.enums.TicketStatus;
import gr.aueb.cf.projectmanagementapp.core.exceptions.AppObjectNotFoundException;
import gr.aueb.cf.projectmanagementapp.dto.TicketCreateDTO;
import gr.aueb.cf.projectmanagementapp.dto.TicketPatchDTO;
import gr.aueb.cf.projectmanagementapp.dto.TicketReadOnlyDTO;
import gr.aueb.cf.projectmanagementapp.dto.TicketUpdateDTO;
import gr.aueb.cf.projectmanagementapp.mapper.Mapper;
import gr.aueb.cf.projectmanagementapp.model.Project;
import gr.aueb.cf.projectmanagementapp.model.Ticket;
import gr.aueb.cf.projectmanagementapp.model.User;
import gr.aueb.cf.projectmanagementapp.repository.ProjectRepository;
import gr.aueb.cf.projectmanagementapp.repository.TicketRepository;
import gr.aueb.cf.projectmanagementapp.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserProjectTicketServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private TicketRepository ticketRepository;

    @Mock
    private Mapper mapper;

    @InjectMocks
    private UserProjectTicketService userProjectTicketService;

    private Project testProject;
    private Ticket ticket1;
    private TicketReadOnlyDTO ticketDTO1;
    private Ticket ticket2;
    private TicketReadOnlyDTO ticketDTO2;
    private final String testUserUuid = "testUserUuid";
    private final String testProjectUuid = "testProjectUuid";

    @BeforeEach
    void setUp() {
        User testUser = User.builder().
                uuid(testUserUuid).
                build();
        testProject = new Project(1L, testProjectUuid, "name1", "description1", null, false, ProjectStatus.OPEN, testUser, new HashSet<>());
        ticket1 = Ticket.builder()
                .id(100L)
                .uuid("ticket1")
                .title("title1")
                .description("test1")
                .priority(TicketPriority.LOW)
                .status(TicketStatus.OPEN)
                .expiryDate(LocalDate.now().plusDays(1))
                .build();
        ticket2 = Ticket.builder()
                .id(200L)
                .uuid("ticket2")
                .title("title2")
                .description("test2")
                .priority(TicketPriority.LOW)
                .status(TicketStatus.OPEN)
                .expiryDate(LocalDate.now().plusDays(1))
                .build();
        testUser.addProject(testProject);
        testProject.addTicket(ticket1);
        testProject.addTicket(ticket2);
        ticketDTO1 = new TicketReadOnlyDTO(ticket1.getId(), ticket1.getUuid(), ticket1.getTitle(), ticket1.getDescription(), ticket1.getPriority().name(), ticket1.getStatus().name(), ticket1.getExpiryDate());
        ticketDTO2 = new TicketReadOnlyDTO(ticket2.getId(), ticket2.getUuid(), ticket2.getTitle(), ticket2.getDescription(), ticket2.getPriority().name(), ticket2.getStatus().name(), ticket2.getExpiryDate());
    }

    @Test
    void testGetProjectTicketsWhenUserNotExistsShouldThrowException() {
        when(userRepository.existsByUuid(testUserUuid)).thenReturn(false);

        assertThrows(AppObjectNotFoundException.class, () -> userProjectTicketService.getProjectTickets(testUserUuid, testProjectUuid));
    }

    @Test
    void testGetProjectTicketsWhenUserExistsButProjectNotExistsShouldThrowException() {
        when(userRepository.existsByUuid(testUserUuid)).thenReturn(true);
        when(projectRepository.existsByUuidAndOwnerUuid(testProjectUuid, testUserUuid)).thenReturn(false);

        assertThrows(AppObjectNotFoundException.class, () -> userProjectTicketService.getProjectTickets(testUserUuid, testProjectUuid));
    }

    @Test
    void testGetProjectTicketsWhenUserAndProjectExistShouldReturnProjectTickets() throws AppObjectNotFoundException {

        when(userRepository.existsByUuid(testUserUuid)).thenReturn(true);
        when(projectRepository.existsByUuidAndOwnerUuid(testProjectUuid, testUserUuid)).thenReturn(true);
        when(ticketRepository.findByProjectUuid(testProjectUuid)).thenReturn(List.of(ticket1, ticket2));
        when(mapper.mapToTicketReadOnlyDTO(ticket1)).thenReturn(ticketDTO1);
        when(mapper.mapToTicketReadOnlyDTO(ticket2)).thenReturn(ticketDTO2);

        List<TicketReadOnlyDTO> result = userProjectTicketService.getProjectTickets(testUserUuid, testProjectUuid);

        assertEquals(2, result.size());
        assertTrue(result.contains(ticketDTO1));
        assertTrue(result.contains(ticketDTO2));
    }

    @Test
    void testGetProjectTicketByUuidWhenUserNotExistsShouldThrowException() {
        when(userRepository.existsByUuid(testUserUuid)).thenReturn(false);

        assertThrows(AppObjectNotFoundException.class, () -> userProjectTicketService.getProjectTicketByUuid(testUserUuid, testProjectUuid, ticket1.getUuid()));
    }

    @Test
    void testGetProjectTicketByUuidWhenUserExistsButProjectNotExistsShouldThrowException() {
        when(userRepository.existsByUuid(testUserUuid)).thenReturn(true);
        when(projectRepository.existsByUuidAndOwnerUuid(testProjectUuid, testUserUuid)).thenReturn(false);

        assertThrows(AppObjectNotFoundException.class, () -> userProjectTicketService.getProjectTicketByUuid(testUserUuid, testProjectUuid, ticket1.getUuid()));
    }

    @Test
    void testGetProjectTicketByUuidWhenUserAndProjectExistButTicketNotExistsShouldThrowException() {
        when(userRepository.existsByUuid(testUserUuid)).thenReturn(true);
        when(projectRepository.existsByUuidAndOwnerUuid(testProjectUuid, testUserUuid)).thenReturn(true);
        when(ticketRepository.findByUuidAndProjectUuid(ticket1.getUuid(), testProjectUuid)).thenReturn(Optional.empty());

        assertThrows(AppObjectNotFoundException.class, () -> userProjectTicketService.getProjectTicketByUuid(testUserUuid, testProjectUuid, ticket1.getUuid()));
    }

    @Test
    void testGetProjectTicketByUuidWhenUserAndProjectAndTicketExistShouldReturnProjectTicket() throws AppObjectNotFoundException {
        when(userRepository.existsByUuid(testUserUuid)).thenReturn(true);
        when(projectRepository.existsByUuidAndOwnerUuid(testProjectUuid, testUserUuid)).thenReturn(true);
        when(ticketRepository.findByUuidAndProjectUuid(ticket1.getUuid(), testProjectUuid)).thenReturn(Optional.of(ticket1));
        when(mapper.mapToTicketReadOnlyDTO(ticket1)).thenReturn(ticketDTO1);

        TicketReadOnlyDTO result = userProjectTicketService.getProjectTicketByUuid(testUserUuid, testProjectUuid, ticket1.getUuid());
        assertEquals(ticketDTO1, result);
    }

    @Test
    void testCreateProjectTicketWhenUserNotExistsShouldThrowException() {
        TicketCreateDTO createDTO = new TicketCreateDTO("test-title", "test-desc", TicketPriority.LOW.name(), TicketStatus.OPEN.name(), LocalDate.now().plusDays(1));
        when(userRepository.existsByUuid(testUserUuid)).thenReturn(false);

        assertThrows(AppObjectNotFoundException.class, () -> userProjectTicketService.createProjectTicket(testUserUuid, testProjectUuid, createDTO));
    }

    @Test
    void testCreateProjectTicketWhenUserExistsButProjectNotFoundShouldThrowException() {
        TicketCreateDTO createDTO = new TicketCreateDTO("test-title", "test-desc", TicketPriority.LOW.name(), TicketStatus.OPEN.name(), LocalDate.now().plusDays(1));
        when(userRepository.existsByUuid(testUserUuid)).thenReturn(true);
        when(projectRepository.findByUuidAndOwnerUuid(testProjectUuid, testUserUuid)).thenReturn(Optional.empty());

        assertThrows(AppObjectNotFoundException.class, () -> userProjectTicketService.createProjectTicket(testUserUuid, testProjectUuid, createDTO));
    }

    @Test
    void testCreateProjectTicketWhenUserAndProjectExistShouldReturnTicket() throws AppObjectNotFoundException {
        TicketCreateDTO createDTO = new TicketCreateDTO("test-title", "test-desc", TicketPriority.LOW.name(), TicketStatus.OPEN.name(), LocalDate.now().plusDays(1));
        Ticket newTicket = Ticket.builder()
                .title(createDTO.title())
                .description(createDTO.description())
                .priority(TicketPriority.valueOf(createDTO.priority()))
                .status(TicketStatus.valueOf(createDTO.status()))
                .expiryDate(createDTO.expiryDate())
                .build();
        Ticket createdTicket = Ticket.builder()
                .id(1000L)
                .uuid("uuid-random-100")
                .title(createDTO.title())
                .description(createDTO.description())
                .priority(TicketPriority.valueOf(createDTO.priority()))
                .status(TicketStatus.valueOf(createDTO.status()))
                .expiryDate(createDTO.expiryDate())
                .project(testProject)
                .build();
        TicketReadOnlyDTO readOnlyDTO = new TicketReadOnlyDTO(createdTicket.getId(), createdTicket.getUuid(), createdTicket.getTitle(), createdTicket.getDescription(), createdTicket.getPriority().name(), createdTicket.getStatus().name(), createdTicket.getExpiryDate());
        when(userRepository.existsByUuid(testUserUuid)).thenReturn(true);
        when(projectRepository.findByUuidAndOwnerUuid(testProjectUuid, testUserUuid)).thenReturn(Optional.of(testProject));
        when(mapper.mapToTicket(createDTO)).thenReturn(newTicket);
        when(ticketRepository.save(newTicket)).thenReturn(createdTicket);
        when(mapper.mapToTicketReadOnlyDTO(createdTicket)).thenReturn(readOnlyDTO);

        TicketReadOnlyDTO result = userProjectTicketService.createProjectTicket(testUserUuid, testProjectUuid, createDTO);
        assertEquals(readOnlyDTO, result);
        verify(ticketRepository).save(newTicket);
    }

    @Test
    void testUpdateProjectTicketWithTicketUpdateDTOWhenUserNotExistsShouldThrowException() {
        TicketUpdateDTO updateDTO = new TicketUpdateDTO("updated-title", "updated-desc", TicketPriority.LOW.name(), TicketStatus.OPEN.name(), LocalDate.now().plusDays(1));

        when(userRepository.existsByUuid(testUserUuid)).thenReturn(false);

        assertThrows(AppObjectNotFoundException.class, () -> userProjectTicketService.updateProjectTicket(testUserUuid, testProjectUuid, ticket1.getUuid(), updateDTO));
    }

    @Test
    void testUpdateProjectTicketWithTicketUpdateDTOWhenUserExistsButProjectNotFoundShouldThrowException() {
        TicketUpdateDTO updateDTO = new TicketUpdateDTO("updated-title", "updated-desc", TicketPriority.LOW.name(), TicketStatus.OPEN.name(), LocalDate.now().plusDays(1));

        when(userRepository.existsByUuid(testUserUuid)).thenReturn(true);
        when(projectRepository.existsByUuidAndOwnerUuid(testProjectUuid, testUserUuid)).thenReturn(false);

        assertThrows(AppObjectNotFoundException.class, () -> userProjectTicketService.updateProjectTicket(testUserUuid, testProjectUuid, ticket1.getUuid(), updateDTO));
    }

    @Test
    void testUpdateProjectTicketWithTicketUpdateDTOWhenUserAndProjectExistButTicketNotFoundShouldThrowException() {
        TicketUpdateDTO updateDTO = new TicketUpdateDTO("updated-title", "updated-desc", TicketPriority.LOW.name(), TicketStatus.OPEN.name(), LocalDate.now().plusDays(1));

        when(userRepository.existsByUuid(testUserUuid)).thenReturn(true);
        when(projectRepository.existsByUuidAndOwnerUuid(testProjectUuid, testUserUuid)).thenReturn(true);
        when(ticketRepository.findByUuidAndProjectUuid(ticket1.getUuid(), testProjectUuid)).thenReturn(Optional.empty());

        assertThrows(AppObjectNotFoundException.class, () -> userProjectTicketService.updateProjectTicket(testUserUuid, testProjectUuid, ticket1.getUuid(), updateDTO));
    }

    @Test
    void testUpdateProjectTicketWithUpdateDTOShouldUpdateTicker() throws AppObjectNotFoundException {
        TicketUpdateDTO updateDTO = new TicketUpdateDTO("updated-title", "updated-desc", TicketPriority.LOW.name(), TicketStatus.OPEN.name(), LocalDate.now().plusDays(1));
        Ticket updatedTicket = Ticket.builder()
                .id(ticket1.getId())
                .uuid(ticket1.getUuid())
                .project(ticket1.getProject())
                .title(updateDTO.title())
                .description(updateDTO.description())
                .priority(TicketPriority.valueOf(updateDTO.priority()))
                .status(TicketStatus.valueOf(updateDTO.status()))
                .expiryDate(updateDTO.expiryDate())
                .build();
        TicketReadOnlyDTO readOnlyDTO = ticketDTO1 = new TicketReadOnlyDTO(updatedTicket.getId(), updatedTicket.getUuid(), updatedTicket.getTitle(), updatedTicket.getDescription(), updatedTicket.getPriority().name(), updatedTicket.getStatus().name(), updatedTicket.getExpiryDate());

        when(userRepository.existsByUuid(testUserUuid)).thenReturn(true);
        when(projectRepository.existsByUuidAndOwnerUuid(testProjectUuid, testUserUuid)).thenReturn(true);
        when(ticketRepository.findByUuidAndProjectUuid(ticket1.getUuid(), testProjectUuid)).thenReturn(Optional.of(ticket1));
        when(mapper.mapToTicket(updateDTO, ticket1)).thenReturn(updatedTicket);
        when(ticketRepository.save(updatedTicket)).thenReturn(updatedTicket);
        when(mapper.mapToTicketReadOnlyDTO(updatedTicket)).thenReturn(readOnlyDTO);

        TicketReadOnlyDTO result = userProjectTicketService.updateProjectTicket(testUserUuid, testProjectUuid, ticket1.getUuid(), updateDTO);
        assertEquals(readOnlyDTO, result);
        assertEquals(readOnlyDTO.uuid(), ticket1.getUuid());
    }

    @Test
    void testUpdateProjectTicketWithTicketPatchDTOWhenUserNotExistsShouldThrowException() {
        TicketPatchDTO patchDTO = new TicketPatchDTO("updated-title", "updated-desc", TicketPriority.LOW.name(), TicketStatus.OPEN.name(), LocalDate.now().plusDays(1));

        when(userRepository.existsByUuid(testUserUuid)).thenReturn(false);

        assertThrows(AppObjectNotFoundException.class, () -> userProjectTicketService.updateProjectTicket(testUserUuid, testProjectUuid, ticket1.getUuid(), patchDTO));
    }

    @Test
    void testUpdateProjectTicketWithTicketPatchDTOWhenUserExistsButProjectNotFoundShouldThrowException() {
        TicketPatchDTO patchDTO = new TicketPatchDTO("updated-title", "updated-desc", TicketPriority.LOW.name(), TicketStatus.OPEN.name(), LocalDate.now().plusDays(1));

        when(userRepository.existsByUuid(testUserUuid)).thenReturn(true);
        when(projectRepository.existsByUuidAndOwnerUuid(testProjectUuid, testUserUuid)).thenReturn(false);

        assertThrows(AppObjectNotFoundException.class, () -> userProjectTicketService.updateProjectTicket(testUserUuid, testProjectUuid, ticket1.getUuid(), patchDTO));
    }

    @Test
    void testUpdateProjectTicketWithTicketPatchDTOWhenUserAndProjectExistButTicketNotFoundShouldThrowException() {
        TicketPatchDTO patchDTO = new TicketPatchDTO("updated-title", "updated-desc", TicketPriority.LOW.name(), TicketStatus.OPEN.name(), LocalDate.now().plusDays(1));

        when(userRepository.existsByUuid(testUserUuid)).thenReturn(true);
        when(projectRepository.existsByUuidAndOwnerUuid(testProjectUuid, testUserUuid)).thenReturn(true);
        when(ticketRepository.findByUuidAndProjectUuid(ticket1.getUuid(), testProjectUuid)).thenReturn(Optional.empty());

        assertThrows(AppObjectNotFoundException.class, () -> userProjectTicketService.updateProjectTicket(testUserUuid, testProjectUuid, ticket1.getUuid(), patchDTO));
    }

    @Test
    void testUpdateProjectWithPatchDTOTicketShouldUpdateTicker() throws AppObjectNotFoundException {
        TicketPatchDTO patchDTO = new TicketPatchDTO("updated-title", "updated-desc", TicketPriority.LOW.name(), TicketStatus.OPEN.name(), LocalDate.now().plusDays(1));
        Ticket updatedTicket = Ticket.builder()
                .id(ticket1.getId())
                .uuid(ticket1.getUuid())
                .project(ticket1.getProject())
                .title(patchDTO.title())
                .description(patchDTO.description())
                .priority(TicketPriority.valueOf(patchDTO.priority()))
                .status(TicketStatus.valueOf(patchDTO.status()))
                .expiryDate(patchDTO.expiryDate())
                .build();
        TicketReadOnlyDTO readOnlyDTO = ticketDTO1 = new TicketReadOnlyDTO(updatedTicket.getId(), updatedTicket.getUuid(), updatedTicket.getTitle(), updatedTicket.getDescription(), updatedTicket.getPriority().name(), updatedTicket.getStatus().name(), updatedTicket.getExpiryDate());

        when(userRepository.existsByUuid(testUserUuid)).thenReturn(true);
        when(projectRepository.existsByUuidAndOwnerUuid(testProjectUuid, testUserUuid)).thenReturn(true);
        when(ticketRepository.findByUuidAndProjectUuid(ticket1.getUuid(), testProjectUuid)).thenReturn(Optional.of(ticket1));
        when(mapper.mapToTicket(patchDTO, ticket1)).thenReturn(updatedTicket);
        when(ticketRepository.save(updatedTicket)).thenReturn(updatedTicket);
        when(mapper.mapToTicketReadOnlyDTO(updatedTicket)).thenReturn(readOnlyDTO);

        TicketReadOnlyDTO result = userProjectTicketService.updateProjectTicket(testUserUuid, testProjectUuid, ticket1.getUuid(), patchDTO);
        assertEquals(readOnlyDTO, result);
        assertEquals(readOnlyDTO.uuid(), ticket1.getUuid());
    }


    @Test
    void testDeleteProjectTicketWhenUserNotExistsShouldThrowException() {
        when(userRepository.existsByUuid(testUserUuid)).thenReturn(false);

        assertThrows(AppObjectNotFoundException.class, () -> userProjectTicketService.deleteProjectTicket(testUserUuid, testProjectUuid, ticket1.getUuid()));
    }

    @Test
    void testDeleteProjectTicketWhenUserExistsButProjectNotFoundShouldThrowException() {
        when(userRepository.existsByUuid(testUserUuid)).thenReturn(true);
        when(projectRepository.existsByUuidAndOwnerUuid(testProjectUuid, testUserUuid)).thenReturn(false);

        assertThrows(AppObjectNotFoundException.class, () -> userProjectTicketService.deleteProjectTicket(testUserUuid, testProjectUuid, ticket1.getUuid()));
    }

    @Test
    void testDeleteProjectTicketWhenUserAndProjectExistButTicketNotFoundShouldThrowException() {
        when(userRepository.existsByUuid(testUserUuid)).thenReturn(true);
        when(projectRepository.existsByUuidAndOwnerUuid(testProjectUuid, testUserUuid)).thenReturn(true);
        when(ticketRepository.findByUuidAndProjectUuid(ticket1.getUuid(), testProjectUuid)).thenReturn(Optional.empty());

        assertThrows(AppObjectNotFoundException.class, () -> userProjectTicketService.deleteProjectTicket(testUserUuid, testProjectUuid, ticket1.getUuid()));
    }

    @Test
    void testDeleteProjectTicketShouldDeleteTicket() throws AppObjectNotFoundException {
        when(userRepository.existsByUuid(testUserUuid)).thenReturn(true);
        when(projectRepository.existsByUuidAndOwnerUuid(testProjectUuid, testUserUuid)).thenReturn(true);
        when(ticketRepository.findByUuidAndProjectUuid(ticket1.getUuid(), testProjectUuid)).thenReturn(Optional.of(ticket1));

        userProjectTicketService.deleteProjectTicket(testUserUuid, testProjectUuid, ticket1.getUuid());

        verify(ticketRepository).delete(ticket1);
    }




}