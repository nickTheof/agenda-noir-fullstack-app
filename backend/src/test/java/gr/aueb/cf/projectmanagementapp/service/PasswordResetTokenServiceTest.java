package gr.aueb.cf.projectmanagementapp.service;

import gr.aueb.cf.projectmanagementapp.core.exceptions.AppObjectNotAuthorizedException;
import gr.aueb.cf.projectmanagementapp.core.exceptions.AppObjectNotFoundException;
import gr.aueb.cf.projectmanagementapp.core.exceptions.AppServerException;
import gr.aueb.cf.projectmanagementapp.model.PasswordResetToken;
import gr.aueb.cf.projectmanagementapp.model.User;
import gr.aueb.cf.projectmanagementapp.repository.PasswordResetTokenRepository;
import gr.aueb.cf.projectmanagementapp.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PasswordResetTokenServiceTest {

    @Mock
    private PasswordResetTokenRepository passwordResetTokenRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private PasswordResetTokenService passwordResetTokenService;

    @Test
    void testGenerateTokenWhenUserNotFoundThrows() {
        when(userRepository.findByUsername("usertest")).thenReturn(Optional.empty());

        assertThrows(AppServerException.class, () -> {passwordResetTokenService.generateTokenForUser("usertest");});
    }

    @Test
    void testGenerateTokenForUserWhenValidExistingTokenShouldReturnSameToken() throws Exception {
        User user = mock(User.class);
        PasswordResetToken existingToken = mock(PasswordResetToken.class);

        when(userRepository.findByUsername("usertest")).thenReturn(Optional.of(user));
        when(user.getPasswordResetToken()).thenReturn(existingToken);
        when(existingToken.isTokenValid()).thenReturn(true);

        PasswordResetToken result = passwordResetTokenService.generateTokenForUser("usertest");

        assertEquals(existingToken, result);
        verify(userRepository, never()).save(any());
    }

    @Test
    void testGenerateTokenForUserWhenInValidExistingTokenShouldReturnNewToken() throws Exception {
        User user = new User();

        PasswordResetToken expiredToken = new PasswordResetToken(user, -10); // expired 10 mins ago
        user.setPasswordResetToken(expiredToken);

        when(userRepository.findByUsername("usertest")).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0)); // just return the saved user

        PasswordResetToken newToken = passwordResetTokenService.generateTokenForUser("usertest");

        assertNotNull(newToken);
        assertTrue(newToken.isTokenValid(), "Expected token to be valid");
        verify(userRepository).save(user);
    }

    @Test
    void testGenerateTokenForUserWhenNoExistingTokenShouldReturnNewToken() throws Exception {
        User user = new User();
        assertNull(user.getPasswordResetToken());

        when(userRepository.findByUsername("usertest")).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        PasswordResetToken newToken = passwordResetTokenService.generateTokenForUser("usertest");

        assertNotNull(newToken, "Token should not be null");
        assertTrue(newToken.isTokenValid(), "Token should be valid");
        assertEquals(user, newToken.getUser(), "Token's user should be the same");
        verify(userRepository).save(user);
    }

    @Test
    void testGetUserForNotFoundTokenShouldThrow() {
        String token = "invalidToken";
        when(passwordResetTokenRepository.findByToken(token)).thenReturn(Optional.empty());
        assertThrows(AppObjectNotFoundException.class, () -> {
            passwordResetTokenService.getUserForValidToken(token);
        });
    }

    @Test
    void testGetUserForInvalidTokenShouldThrow() {
        String token = "invalidToken";
        PasswordResetToken expiredToken = mock(PasswordResetToken.class);
        when(passwordResetTokenRepository.findByToken(token)).thenReturn(Optional.of(expiredToken));
        when(expiredToken.isTokenValid()).thenReturn(false);
        assertThrows(AppObjectNotFoundException.class, () -> {
            passwordResetTokenService.getUserForValidToken(token);
        });
    }

    @Test
    void testGetUserForValidTokenShouldReturnUser() throws Exception {
        User user = new User();
        PasswordResetToken token = new PasswordResetToken(user, 30);

        when(passwordResetTokenRepository.findByToken("abc123")).thenReturn(Optional.of(token));

        User result = passwordResetTokenService.getUserForValidToken("abc123");

        assertEquals(user, result);
    }
}