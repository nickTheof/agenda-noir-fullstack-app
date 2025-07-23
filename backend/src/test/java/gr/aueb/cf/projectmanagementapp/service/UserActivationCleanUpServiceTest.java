package gr.aueb.cf.projectmanagementapp.service;

import gr.aueb.cf.projectmanagementapp.core.exceptions.AppObjectNotFoundException;
import gr.aueb.cf.projectmanagementapp.model.User;
import gr.aueb.cf.projectmanagementapp.model.VerificationToken;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserActivationCleanUpServiceTest {

    @Mock
    private IUserService userService;

    @Mock
    private VerificationTokenService verificationTokenService;

    @InjectMocks
    private UserActivationCleanUpService cleanUpService;

    @Test
    void testCleanupExpiredRegistrationsDeletesUsersAndTokens() throws AppObjectNotFoundException {
        User user1 = new User();
        user1.setUsername("expiredUser");
        VerificationToken token1 = mock(VerificationToken.class);
        when(token1.getUser()).thenReturn(user1);
        when(verificationTokenService.findAllExpiredTokens())
                .thenReturn(List.of(token1));

        cleanUpService.cleanupExpiredRegistrations();

        verify(verificationTokenService).deleteToken(token1);
        verify(userService).deleteUser("expiredUser");
    }

    @Test
    void testCleanupContinuesOnException() throws AppObjectNotFoundException {
        User user1 = User.builder().username("failUser").build();
        User user2 = User.builder().username("successUser").build();

        VerificationToken token1 = mock(VerificationToken.class);
        VerificationToken token2 = mock(VerificationToken.class);
        when(token1.getUser()).thenReturn(user1);
        when(token2.getUser()).thenReturn(user2);

        when(verificationTokenService.findAllExpiredTokens())
                .thenReturn(List.of(token1, token2));

        // Fail first
        doThrow(new RuntimeException("Something went wrong"))
                .when(userService).deleteUser("failUser");

        cleanUpService.cleanupExpiredRegistrations();

        verify(userService).deleteUser("successUser");
    }
}
