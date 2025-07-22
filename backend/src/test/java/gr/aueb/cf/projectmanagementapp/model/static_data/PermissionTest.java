package gr.aueb.cf.projectmanagementapp.model.static_data;

import gr.aueb.cf.projectmanagementapp.core.enums.Action;
import gr.aueb.cf.projectmanagementapp.core.enums.Resource;
import org.junit.jupiter.api.Test;

import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;

class PermissionTest {

    @Test
    void testDefaultConstructorGettersAndSetters() {
        Permission permission = new Permission();
        assertNull(permission.getResource());
        assertNull(permission.getAction());
        assertNull(permission.getName());
        assertNull(permission.getId());
        permission.setId(1L);
        permission.setName("test");
        permission.setAction(Action.READ);
        permission.setResource(Resource.USER);
        permission.onCreate(); // Simulate JPA lifecycle
        assertEquals(Resource.USER, permission.getResource());
        assertEquals(Action.READ, permission.getAction());
        assertEquals(1L, permission.getId());
        assertEquals("READ_USER", permission.getName());
    }

    @Test
    void testOverloadConstructorGettersAndSetters() {
        Permission permission = new Permission(1L, null, Resource.USER, Action.READ, new HashSet<>());
        permission.onCreate(); // Simulate JPA lifecycle
        assertEquals(Resource.USER, permission.getResource());
        assertEquals(Action.READ, permission.getAction());
        assertEquals(1L, permission.getId());
        assertEquals("READ_USER", permission.getName());
        assertTrue(permission.getRoles().isEmpty());
    }
}