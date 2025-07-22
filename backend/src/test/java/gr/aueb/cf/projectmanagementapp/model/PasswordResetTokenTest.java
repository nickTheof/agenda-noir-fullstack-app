package gr.aueb.cf.projectmanagementapp.model;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

class PasswordResetTokenTest {

    @Test
    void testDefaultConstructorAndSetters() {
        PasswordResetToken token = new PasswordResetToken();
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
    void testConstructorWithCustomExpiry() {
        User user = new User();
        int customExpiry = 120;
        PasswordResetToken token = new PasswordResetToken(user, customExpiry);

        assertNotNull(token.getToken());
        assertNotNull(token.getExpiryDate());
        assertTrue(token.getExpiryDate().isAfter(LocalDateTime.now().plusMinutes(100)));
        assertEquals(user, token.getUser());
    }

    @Test
    void testPrePersistGeneratesTokenAndExpiry() {
        PasswordResetToken token = new PasswordResetToken();
        token.onCreate(); // Simulate JPA lifecycle

        assertNotNull(token.getToken());
        assertNotNull(token.getExpiryDate());
        assertTrue(token.getExpiryDate().isAfter(LocalDateTime.now()));
    }

    @Test
    void testTokenValidity() {
        PasswordResetToken token = new PasswordResetToken();
        token.setExpiryDate(LocalDateTime.now().plusMinutes(5));
        assertTrue(token.isTokenValid());

        token.setExpiryDate(LocalDateTime.now().minusMinutes(5));
        assertFalse(token.isTokenValid());
    }
}
