package gr.aueb.cf.projectmanagementapp.service;

import gr.aueb.cf.projectmanagementapp.core.exceptions.AppObjectNotFoundException;
import gr.aueb.cf.projectmanagementapp.core.exceptions.AppServerException;
import gr.aueb.cf.projectmanagementapp.model.User;
import gr.aueb.cf.projectmanagementapp.model.VerificationToken;
import gr.aueb.cf.projectmanagementapp.repository.UserRepository;
import gr.aueb.cf.projectmanagementapp.repository.VerificationTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class VerificationTokenService {
    private static final int TOKEN_EXPIRATION_HOURS = 24;
    private static final int TOKEN_EXPIRATION_MINUTES = TOKEN_EXPIRATION_HOURS * 60;

    private final VerificationTokenRepository verificationTokenRepository;
    private final UserRepository userRepository;

    @Transactional
    public VerificationToken generateTokenForUser(String username) throws AppServerException {
        try {
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new AppObjectNotFoundException("User", "User with username " + username + " not found"));

            // Check for existing valid token
            if (user.getVerificationToken() != null) {
                if (user.getVerificationToken().isTokenValid()) {
                    return user.getVerificationToken();
                } else {
                    // Delete if there is token but is expired
                    user.clearVerificationToken();
                }
            }
            // Create new token
            user.createVerificationToken(TOKEN_EXPIRATION_MINUTES);
            User savedUser = userRepository.save(user);
            return savedUser.getVerificationToken();
        } catch (Exception e) {
            throw new AppServerException("Failed to generate verification token", e.getMessage());
        }
    }


    @Transactional
    public User getUserForValidToken(String token) throws AppObjectNotFoundException {
        VerificationToken verificationToken = verificationTokenRepository.findByToken(token)
                .orElseThrow(() -> new AppObjectNotFoundException("Token", "Password reset token not found"));
        if (!verificationToken.isTokenValid()) {
            throw new AppObjectNotFoundException("Token", "Password reset token has expired");
        }
        return verificationToken.getUser();
    }

    @Transactional
    public List<VerificationToken> findAllExpiredTokens() {
        return verificationTokenRepository.findByCreatedAtBefore(LocalDateTime.now().minusHours(TOKEN_EXPIRATION_HOURS));
    }

    @Transactional
    public void deleteToken(VerificationToken token) {
        verificationTokenRepository.delete(token);
    }
}
