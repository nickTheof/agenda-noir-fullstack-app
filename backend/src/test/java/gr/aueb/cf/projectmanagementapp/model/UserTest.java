package gr.aueb.cf.projectmanagementapp.model;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.Instant;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class UserTest {
    private User user;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .username("testuser")
                .firstname("Test")
                .lastname("User")
                .password("secret")
                .passwordLastModified(Instant.now().minus(10, java.time.temporal.ChronoUnit.DAYS))
                .enabled(true)
                .verified(true)
                .accountNonLocked(true)
                .isDeleted(false)
                .loginConsecutiveFailAttempts(0)
                .build();
    }

    @Test
    void defaultConstructorGettersAndSetters() {
        long epochSeconds = 1721308800L;
        Instant passwordModified = Instant.ofEpochSecond(epochSeconds);

        User defaultUser = new User();
        assertNull(defaultUser.getId());
        assertNull(defaultUser.getUuid());
        assertNull(defaultUser.getUsername());
        assertNull(defaultUser.getFirstname());
        assertNull(defaultUser.getLastname());
        assertNull(defaultUser.getPassword());
        assertNull(defaultUser.getPasswordLastModified());
        assertNull(defaultUser.getEnabled());
        assertNull(defaultUser.getVerified());
        assertNull(defaultUser.getAccountNonLocked());
        assertNull(defaultUser.getIsDeleted());
        assertNull(defaultUser.getLoginConsecutiveFailAttempts());
        defaultUser.setId(1L);
        defaultUser.setUuid("uuid");
        defaultUser.setUsername("username");
        defaultUser.setFirstname("firstname");
        defaultUser.setLastname("lastname");
        defaultUser.setPassword("password");
        defaultUser.setEnabled(true);
        defaultUser.setVerified(true);
        defaultUser.setAccountNonLocked(true);
        defaultUser.setIsDeleted(false);
        defaultUser.setLoginConsecutiveFailAttempts(0);
        defaultUser.setPasswordLastModified(passwordModified);
        assertEquals(1L, defaultUser.getId());
        assertEquals("uuid", defaultUser.getUuid());
        assertEquals("username", defaultUser.getUsername());
        assertEquals("firstname", defaultUser.getFirstname());
        assertEquals("lastname", defaultUser.getLastname());
        assertEquals("password", defaultUser.getPassword());
        assertTrue(defaultUser.getEnabled());
        assertTrue(defaultUser.getVerified());
        assertTrue(defaultUser.getAccountNonLocked());
        assertFalse(defaultUser.getIsDeleted());
        assertEquals(0, defaultUser.getLoginConsecutiveFailAttempts());
        assertEquals(passwordModified, defaultUser.getPasswordLastModified());
    }

    @Test
    void allArgsConstructorShouldSetAllFields() {
        long epochSeconds = 1721308800L;
        Instant passwordModified = Instant.ofEpochSecond(epochSeconds);

        User user = new User(
                1L,
                "uuid",
                "username",
                "firstname",
                "lastname",
                "password",
                passwordModified,
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
        assertEquals(1L, user.getId());
        assertEquals("uuid", user.getUuid());
        assertEquals("username", user.getUsername());
        assertEquals("firstname", user.getFirstname());
        assertEquals("lastname", user.getLastname());
        assertEquals("password", user.getPassword());
        assertEquals(passwordModified, user.getPasswordLastModified());
        assertEquals(0, user.getLoginConsecutiveFailAttempts());
        assertTrue(user.getEnabled());
        assertTrue(user.getVerified());
        assertTrue(user.getAccountNonLocked());
        assertNull(user.getLockTime());
        assertNull(user.getDeletedAt());
        assertFalse(user.getIsDeleted());
        assertNull(user.getPasswordResetToken());
        assertNull(user.getVerificationToken());
    }

    @Test
    void builderShouldBuildUserCorrectly() {
        long epochSeconds = 1721308800L;
        Instant passwordModified = Instant.ofEpochSecond(epochSeconds);

        User user = User.builder()
                .id(1L)
                .uuid("uuid")
                .username("username")
                .firstname("firstname")
                .lastname("lastname")
                .password("password")
                .passwordLastModified(passwordModified)
                .loginConsecutiveFailAttempts(0)
                .enabled(true)
                .verified(true)
                .accountNonLocked(true)
                .isDeleted(false)
                .build();

        assertEquals(1L, user.getId());
        assertEquals("uuid", user.getUuid());
        assertEquals("username", user.getUsername());
        assertEquals("firstname", user.getFirstname());
        assertEquals("lastname", user.getLastname());
        assertEquals("password", user.getPassword());
        assertEquals(passwordModified, user.getPasswordLastModified());
        assertEquals(0, user.getLoginConsecutiveFailAttempts());
        assertTrue(user.getEnabled());
        assertTrue(user.getVerified());
        assertTrue(user.getAccountNonLocked());
        assertFalse(user.getIsDeleted());
    }

    @Test
    void testIsAccountNonExpired() {
        assertTrue(user.isAccountNonExpired());
    }

    @Test
    void testIsAccountNonLocked() {
        assertTrue(user.isAccountNonLocked());
        user.setAccountNonLocked(false);
        assertFalse(user.isAccountNonLocked());
    }

    @Test
    void testIsCredentialsNonExpired() {
        assertTrue(user.isCredentialsNonExpired());
        user.setPasswordLastModified(Instant.now().minus(91, java.time.temporal.ChronoUnit.DAYS));
        assertFalse(user.isCredentialsNonExpired());
    }

    @Test
    void testIsEnabled() {
        assertTrue(user.isEnabled());
        user.setEnabled(false);
        assertFalse(user.isEnabled());
        user.setEnabled(true);
        user.setVerified(false);
        assertFalse(user.isEnabled());
        user.setVerified(true);
        user.setIsDeleted(true);
        assertFalse(user.isEnabled());
    }

    @Test
    void testLockAccount() {
        assertTrue(user.getAccountNonLocked());
        assertNull(user.getLockTime());
        user.lockAccount();
        assertFalse(user.getAccountNonLocked());
        assertNotNull(user.getLockTime());
    }

    @Test
    void testUnlockAccountResetsFailedAttempts() {
        user.incrementFailedAttempts();
        user.incrementFailedAttempts();
        user.lockAccount();
        assertFalse(user.getAccountNonLocked());
        assertNotNull(user.getLockTime());
        user.unlockAccount();
        assertTrue(user.getAccountNonLocked());
        assertNull(user.getLockTime());
        assertEquals(0, user.getLoginConsecutiveFailAttempts());
    }

    @Test
    void testIncrementFailedAttemptsLocksAccount() {
        for (int i = 0; i < 5; i++) {
            user.incrementFailedAttempts();
        }
        assertEquals(5, user.getLoginConsecutiveFailAttempts());
        assertFalse(user.isAccountNonLocked());
        assertNotNull(user.getLockTime());
    }

    @Test
    void testResetFailedAttemptsAccount() {
        assertEquals(0, user.getLoginConsecutiveFailAttempts());
        user.incrementFailedAttempts();
        assertEquals(1, user.getLoginConsecutiveFailAttempts());
        user.resetFailedAttempts();
        assertEquals(0, user.getLoginConsecutiveFailAttempts());
    }

    @Test
    void testSoftDelete() {
        user.softDelete();

        assertTrue(user.getIsDeleted());
        assertFalse(user.getEnabled());
        assertFalse(user.getVerified());
        assertNotNull(user.getDeletedAt());
        assertNull(user.getPasswordResetToken());
        assertNull(user.getVerificationToken());
    }

    @Test
    void testVerifyAccountEnablesUser() {
        user.setVerified(false);
        user.setEnabled(false);

        user.verifyAccount();

        assertTrue(user.getVerified());
        assertTrue(user.getEnabled());
    }

    @Test
    void testUpdatePasswordChangesTimestamp() {
        Instant beforeUpdate = user.getPasswordLastModified();
        user.updatePassword("newSecret");
        assertEquals("newSecret", user.getPassword());
        assertTrue(user.getPasswordLastModified().isAfter(beforeUpdate));
    }

    @Test
    void testAddAndRemoveRole() {
        Role role = new Role();
        role.setName("admin");

        // Add role
        user.addRole(role);
        assertTrue(user.getAllRoles().contains(role));
        assertTrue(role.getUsers().contains(user));

        // Remove role
        user.removeRole(role);
        assertFalse(user.getAllRoles().contains(role));
        assertFalse(role.getUsers().contains(user));
    }

    @Test
    void testAddAndRemoveProject() {
        Project project = new Project();
        project.setName("test-project");

        // Add project
        user.addProject(project);
        assertTrue(user.getAllProjects().contains(project));
        assertEquals(project.getOwner(), user);

        // Remove project
        user.removeProject(project);
        assertFalse(user.getAllProjects().contains(project));
        assertNull(project.getOwner());
    }

    @Test
    void testCreateAndClearPasswordResetToken() {
        user.createPasswordResetToken(30);

        assertNotNull(user.getPasswordResetToken());
        assertEquals(user, user.getPasswordResetToken().getUser());

        user.clearPasswordResetToken();
        assertNull(user.getPasswordResetToken());
    }

    @Test
    void testHasValidPasswordResetToken() {
        PasswordResetToken mockToken = mock(PasswordResetToken.class);
        when(mockToken.isTokenValid()).thenReturn(true);

        user.setPasswordResetToken(mockToken);
        assertTrue(user.hasValidPasswordResetToken());

        when(mockToken.isTokenValid()).thenReturn(false);
        assertFalse(user.hasValidPasswordResetToken());
    }

    @Test
    void testCreateAndClearVerificationToken() {
        user.createVerificationToken(30);

        assertNotNull(user.getVerificationToken());
        assertEquals(user, user.getVerificationToken().getUser());

        user.clearVerificationToken();
        assertNull(user.getVerificationToken());
    }

    @Test
    void testHasValidVerificationToken() {
        VerificationToken mockToken = mock(VerificationToken.class);
        when(mockToken.isTokenValid()).thenReturn(true);

        user.setVerificationToken(mockToken);
        assertTrue(user.hasValidVerificationToken());

        when(mockToken.isTokenValid()).thenReturn(false);
        assertFalse(user.hasValidVerificationToken());
    }
}