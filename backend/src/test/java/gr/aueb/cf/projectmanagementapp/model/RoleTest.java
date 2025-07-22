package gr.aueb.cf.projectmanagementapp.model;

import gr.aueb.cf.projectmanagementapp.model.static_data.Permission;
import org.junit.jupiter.api.Test;

import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;

class RoleTest {

    @Test
    void testDefaultConstructorGettersAndSetters() {
        Role role = new Role();
        assertNull(role.getName());
        assertNull(role.getId());
        assertTrue(role.getUsers().isEmpty());
        assertTrue(role.getPermissions().isEmpty());
        role.setId(1L);
        role.setName("test");
        assertEquals("test", role.getName());
        assertEquals(1L, role.getId());
    }

    @Test
    void testOverloadConstructorGettersAndSetters() {
        Role role = new Role(1L, "roleTest", new HashSet<>(), new HashSet<>());
        assertEquals("roleTest", role.getName());
        assertEquals(1L, role.getId());
        assertTrue(role.getUsers().isEmpty());
        assertTrue(role.getPermissions().isEmpty());
    }

    @Test
    void testAddAndRemovePermission() {
        Role role = new Role();

        //Add Permission
        Permission permission = new Permission();
        role.addPermission(permission);
        assertTrue(role.getPermissions().contains(permission));
        assertTrue(permission.getRoles().contains(role));

        //Remove Permission
        role.removePermission(permission);
        assertTrue(role.getPermissions().isEmpty());
        assertTrue(permission.getRoles().isEmpty());
    }

}