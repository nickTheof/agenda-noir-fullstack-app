package gr.aueb.cf.projectmanagementapp.model;

import gr.aueb.cf.projectmanagementapp.core.enums.TicketPriority;
import gr.aueb.cf.projectmanagementapp.core.enums.TicketStatus;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class TicketTest {

    @Test
    void testDefaultConstructorGettersAndSetters() {
        Ticket ticket = new Ticket();
        Project project = new Project();
        LocalDate expiryDate = LocalDate.now().plusDays(10);

        ticket.setId(1L);
        ticket.setUuid("uuid");
        ticket.setTitle("Fix Bug");
        ticket.setDescription("Fix a critical backend bug.");
        ticket.setPriority(TicketPriority.HIGH);
        ticket.setStatus(TicketStatus.ON_GOING);
        ticket.setExpiryDate(expiryDate);
        ticket.setProject(project);

        assertEquals(1L, ticket.getId());
        assertEquals("uuid", ticket.getUuid());
        assertEquals("Fix Bug", ticket.getTitle());
        assertEquals("Fix a critical backend bug.", ticket.getDescription());
        assertEquals(TicketPriority.HIGH, ticket.getPriority());
        assertEquals(TicketStatus.ON_GOING, ticket.getStatus());
        assertEquals(expiryDate, ticket.getExpiryDate());
        assertEquals(project, ticket.getProject());
    }

    @Test
    void testOverloadConstructorGettersAndSetters() {
        Project project = new Project();
        LocalDate expiry = LocalDate.now().plusWeeks(1);

        Ticket ticket = new Ticket(
                1L,
                "uuid",
                "Fix Bug",
                "Fix a critical backend bug.",
                TicketPriority.HIGH,
                TicketStatus.ON_GOING,
                expiry,
                project
        );

        assertEquals(1L, ticket.getId());
        assertEquals("uuid", ticket.getUuid());
        assertEquals("Fix Bug", ticket.getTitle());
        assertEquals("Fix a critical backend bug.", ticket.getDescription());
        assertEquals(TicketPriority.HIGH, ticket.getPriority());
        assertEquals(TicketStatus.ON_GOING, ticket.getStatus());
        assertEquals(expiry, ticket.getExpiryDate());
        assertEquals(project, ticket.getProject());
    }

    @Test
    void testBuilderPattern() {
        Project project = new Project();
        LocalDate expiry = LocalDate.now().plusDays(5);

        Ticket ticket = Ticket.builder()
                .id(1L)
                .uuid("uuid")
                .title("Fix Bug")
                .description("Fix a critical backend bug.")
                .priority(TicketPriority.MEDIUM)
                .status(TicketStatus.OPEN)
                .expiryDate(expiry)
                .project(project)
                .build();

        assertEquals(1L, ticket.getId());
        assertEquals("uuid", ticket.getUuid());
        assertEquals("Fix Bug", ticket.getTitle());
        assertEquals("Fix a critical backend bug.", ticket.getDescription());
        assertEquals(TicketPriority.MEDIUM, ticket.getPriority());
        assertEquals(TicketStatus.OPEN, ticket.getStatus());
        assertEquals(expiry, ticket.getExpiryDate());
        assertEquals(project, ticket.getProject());
    }

    @Test
    void testPrePersistDefaults() {
        // Ticket missing UUID, priority, and status
        Ticket ticket = new Ticket();
        ticket.setTitle("test");
        ticket.setDescription("Test");
        ticket.setExpiryDate(LocalDate.now().plusDays(3));
        ticket.setProject(new Project());

        // Simulate @PrePersist lifecycle
        ticket.onCreate();

        assertNotNull(ticket.getUuid());
        assertEquals(TicketPriority.MEDIUM, ticket.getPriority());
        assertEquals(TicketStatus.OPEN, ticket.getStatus());
    }
}
