package gr.aueb.cf.projectmanagementapp.service;

import gr.aueb.cf.projectmanagementapp.core.exceptions.AppObjectNotFoundException;
import gr.aueb.cf.projectmanagementapp.model.User;
import gr.aueb.cf.projectmanagementapp.model.VerificationToken;
import gr.aueb.cf.projectmanagementapp.repository.VerificationTokenRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VerificationTokenServiceTest {
    @Mock
    private VerificationTokenRepository verificationTokenRepository;

    @InjectMocks
    private VerificationTokenService verificationTokenService;

    @Test
    void testGetUserForNotFoundTokenShouldThrow() {
        String token = "invalidToken";
        when(verificationTokenRepository.findByToken(token)).thenReturn(Optional.empty());

        assertThrows(AppObjectNotFoundException.class, () -> {
            verificationTokenService.getUserForValidToken(token);
        });
    }

    @Test
    void testGetUserForInvalidTokenShouldThrow() {
        String token = "invalidToken";
        VerificationToken expiredToken = mock(VerificationToken.class);
        when(verificationTokenRepository.findByToken(token)).thenReturn(Optional.of(expiredToken));
        when(expiredToken.isTokenValid()).thenReturn(false);

        assertThrows(AppObjectNotFoundException.class, () -> {
            verificationTokenService.getUserForValidToken(token);
        });
    }

    @Test
    void testGetUserForValidTokenShouldReturnUser() throws Exception {
        User user = new User();
        VerificationToken token = new VerificationToken(user, 24*60);

        when(verificationTokenRepository.findByToken("abc123")).thenReturn(Optional.of(token));

        User result = verificationTokenService.getUserForValidToken("abc123");

        assertEquals(user, result);
    }

    @Test
    void testFindAllExpiredTokens() {
        List<VerificationToken> expiredTokens = List.of(new VerificationToken(), new VerificationToken());

        when(verificationTokenRepository.findByCreatedAtBefore(any()))
                .thenReturn(expiredTokens);

        List<VerificationToken> result = verificationTokenService.findAllExpiredTokens();

        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    void testDeleteToken() {
        VerificationToken token = new VerificationToken();

        verificationTokenService.deleteToken(token);

        verify(verificationTokenRepository).delete(token);
    }


}