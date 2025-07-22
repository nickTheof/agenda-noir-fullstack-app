package gr.aueb.cf.projectmanagementapp.model;

import gr.aueb.cf.projectmanagementapp.core.enums.ProjectStatus;
import org.junit.jupiter.api.Test;

import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;

class ProjectTest {

    @Test
    void testDefaultConstructorGettersAndSetters() {
        Project project = new Project();
        User user = new User();
        project.setName("test");
        project.setDescription("test");
        project.setOwner(user);
        project.setIsDeleted(false);
        assertEquals("test", project.getName());
        assertEquals("test", project.getDescription());
        assertEquals(user, project.getOwner());
        assertFalse(project.getIsDeleted());
    }

    @Test
    void testOverloadConstructorGettersAndSetters() {
        User user = new User();
        Project project = new Project(1L, "uuid", "name", "description", null, false, ProjectStatus.OPEN, user, new HashSet<>());
        assertEquals(1L, project.getId());
        assertEquals("uuid", project.getUuid());
        assertEquals("name", project.getName());
        assertEquals("description", project.getDescription());
        assertEquals(user, project.getOwner());
        assertNull(project.getDeletedAt());
        assertFalse(project.getIsDeleted());
        assertEquals(ProjectStatus.OPEN, project.getStatus());
        assertTrue(project.getAllTickets().isEmpty());
    }

    @Test
    void testAddAndRemoveTicket() {
        Project project = new Project();
        Ticket ticket = new Ticket();
        // Add Ticket
        project.addTicket(ticket);
        assertTrue(project.getAllTickets().contains(ticket));
        assertEquals(ticket.getProject(), project);
        // Remove Ticket
        project.removeTicket(ticket);
        assertFalse(project.getAllTickets().contains(ticket));
        assertNull(ticket.getProject());
    }
}