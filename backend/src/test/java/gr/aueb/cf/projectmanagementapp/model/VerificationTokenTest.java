package gr.aueb.cf.projectmanagementapp.model;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

class VerificationTokenTest {

    @Test
    void testDefaultConstructorAndSetters() {
        VerificationToken token = new VerificationToken();
        User user = new User();
        token.setUser(user);
        token.setToken("token");
        LocalDateTime futureDate = LocalDateTime.now().plusMinutes(30);
        token.setExpiryDate(futureDate);

        assertEquals(user, token.getUser());
        assertEquals("token", token.getToken());
        assertEquals(futureDate, token.getExpiryDate());
    }

    @Test
    void testConstructorWithDefaultExpiry() {
        User user = new User();
        VerificationToken token = new VerificationToken(user);
        token.onCreate(); // Simulate JPA lifecycle
        assertNotNull(token.getToken());
        assertNotNull(token.getExpiryDate());
        assertTrue(token.getExpiryDate().isAfter(LocalDateTime.now()));
        assertEquals(user, token.getUser());
    }

    @Test
    void testConstructorWithCustomExpiry() {
        User user = new User();
        int customExpiry = 120;
        VerificationToken token = new VerificationToken(user, customExpiry);

        assertNotNull(token.getToken());
        assertNotNull(token.getExpiryDate());
        assertTrue(token.getExpiryDate().isAfter(LocalDateTime.now().plusMinutes(100)));
        assertEquals(user, token.getUser());
    }

    @Test
    void testPrePersistGeneratesTokenAndExpiry() {
        VerificationToken token = new VerificationToken();
        token.onCreate(); // Simulate JPA lifecycle

        assertNotNull(token.getToken());
        assertNotNull(token.getExpiryDate());
        assertTrue(token.getExpiryDate().isAfter(LocalDateTime.now()));
    }

    @Test
    void testTokenValidity() {
        VerificationToken token = new VerificationToken();
        token.setExpiryDate(LocalDateTime.now().plusMinutes(5));
        assertTrue(token.isTokenValid());

        token.setExpiryDate(LocalDateTime.now().minusMinutes(5));
        assertFalse(token.isTokenValid());
    }
}