package gr.aueb.cf.projectmanagementapp.service;

import gr.aueb.cf.projectmanagementapp.core.exceptions.AppObjectNotFoundException;
import gr.aueb.cf.projectmanagementapp.core.exceptions.AppServerException;
import gr.aueb.cf.projectmanagementapp.model.PasswordResetToken;
import gr.aueb.cf.projectmanagementapp.model.User;
import gr.aueb.cf.projectmanagementapp.repository.PasswordResetTokenRepository;
import gr.aueb.cf.projectmanagementapp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class PasswordResetTokenService {

    private static final int TOKEN_EXPIRATION_MINUTES = 30;

    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final UserRepository userRepository;


    @Transactional
    public PasswordResetToken generateTokenForUser(String username) throws AppServerException {
        try {
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new AppObjectNotFoundException("User", "User with username " + username + " not found"));

            // Check for existing valid token
            if (user.getPasswordResetToken() != null) {
                if (user.getPasswordResetToken().isTokenValid()) {
                    return user.getPasswordResetToken();
                } else {
                    // Delete if there is token but is expired
                    user.clearPasswordResetToken();
                }
            }
            // Create new token
            user.createPasswordResetToken(TOKEN_EXPIRATION_MINUTES);
            User savedUser = userRepository.save(user);
            return savedUser.getPasswordResetToken();
        } catch (Exception e) {
            throw new AppServerException("Failed to generate password reset token", e.getMessage());
        }
    }

    @Transactional
    public User getUserForValidToken(String token) throws AppObjectNotFoundException {
        PasswordResetToken resetToken = passwordResetTokenRepository.findByToken(token)
                .orElseThrow(() -> new AppObjectNotFoundException("Token", "Password reset token not found"));
        if (!resetToken.isTokenValid()) {
            throw new AppObjectNotFoundException("Token", "Password reset token has expired");
        }
        return resetToken.getUser();
    }
}
