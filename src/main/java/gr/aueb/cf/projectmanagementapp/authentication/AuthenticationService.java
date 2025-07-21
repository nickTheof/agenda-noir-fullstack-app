package gr.aueb.cf.projectmanagementapp.authentication;

import gr.aueb.cf.projectmanagementapp.core.exceptions.AppObjectNotAuthorizedException;
import gr.aueb.cf.projectmanagementapp.dto.AuthenticationRequestDTO;
import gr.aueb.cf.projectmanagementapp.dto.AuthenticationResponseDTO;
import gr.aueb.cf.projectmanagementapp.model.User;
import gr.aueb.cf.projectmanagementapp.repository.UserRepository;
import gr.aueb.cf.projectmanagementapp.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private static final int MAX_FAILED_ATTEMPTS = 5;
    private static final long LOCK_TIME_DURATION = 10; // in minutes

    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;

    public AuthenticationResponseDTO authenticate(AuthenticationRequestDTO dto)
            throws AppObjectNotAuthorizedException {

        User user = userRepository.findByUsername(dto.username())
                .orElseThrow(() -> new AppObjectNotAuthorizedException("User", "Invalid credentials"));

        // Check if account is locked
        if (!user.isAccountNonLocked()) {
            if (isLockTimeExpired(user)) {
                unlockUser(user);
            } else {
                throw new AppObjectNotAuthorizedException("User", String.format("Account is locked. Try again after %s",
                        user.getLockTime()
                                .plusMinutes(LOCK_TIME_DURATION)
                                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))));
            }
        }

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(dto.username(), dto.password()));

            // Reset failed attempts on successful login
            resetFailedAttempts(user);

            String token = jwtService.generateToken(authentication.getName(), user.getUuid());
            return new AuthenticationResponseDTO(token);
        } catch (BadCredentialsException e) {
            // Increment failed attempts
            increaseFailedAttempts(user);
            if (user.getLoginConsecutiveFailAttempts() >= MAX_FAILED_ATTEMPTS) {
                lockUser(user);
            }
            throw new AppObjectNotAuthorizedException("User", "Invalid credentials. Remaining attempts: " + (MAX_FAILED_ATTEMPTS - user.getLoginConsecutiveFailAttempts()));
        }
    }

    public boolean isPasswordValid(String username, String password) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password));
            return authentication.isAuthenticated();
        } catch (Exception e) {
            return false;
        }
    }

    private boolean isLockTimeExpired(User user) {
        if (user.getLockTime() == null) return false;

        LocalDateTime lockTime = user.getLockTime();
        LocalDateTime now = LocalDateTime.now();
        long diffInMinutes = Duration.between(lockTime, now).toMinutes();

        return diffInMinutes >= LOCK_TIME_DURATION;
    }

    private void lockUser(User user) {
        user.lockAccount();
        userRepository.save(user);
    }

    private void unlockUser(User user) {
        user.unlockAccount();
        userRepository.save(user);
    }

    private void resetFailedAttempts(User user) {
        user.resetFailedAttempts();
        userRepository.save(user);
    }

    private void increaseFailedAttempts(User user) {
        user.setLoginConsecutiveFailAttempts(user.getLoginConsecutiveFailAttempts() + 1);
        userRepository.save(user);
    }
}
