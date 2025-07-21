package gr.aueb.cf.projectmanagementapp.service;

import gr.aueb.cf.projectmanagementapp.model.User;
import gr.aueb.cf.projectmanagementapp.model.VerificationToken;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Scheduled service for cleaning up expired user registrations that weren't activated.
 * Automatically removes user accounts and their associated verification tokens when:
 * <ul>
 *   <li>The account registration was never completed (email not verified)</li>
 *   <li>The verification token has expired (default: 24 hours after creation)</li>
 * </ul>
 * <p><b>Scheduling:</b> Runs hourly at the top of each hour (HH:00:00)</p>
 */
@Service
@RequiredArgsConstructor
public class UserActivationCleanUpService {

    private final IUserService userService;
    private final VerificationTokenService verificationTokenService;
    private static final Logger LOGGER = LoggerFactory.getLogger(UserActivationCleanUpService.class);

    /**
     * Executes the cleanup process for expired registrations.
     */
    @Scheduled(cron = "0 0 * * * *") // Runs every hour at minute 0
    @Transactional
    public void cleanupExpiredRegistrations() {
        LOGGER.info("Starting cleanup of expired user registrations...");

        List<VerificationToken> expiredTokens = verificationTokenService.findAllExpiredTokens();

        expiredTokens.forEach(token -> {
            try {
                User user = token.getUser();
                LOGGER.info("Deleting unactivated user {} with expired token", user.getUsername());

                // Delete in this order to maintain referential integrity
                verificationTokenService.deleteToken(token);
                userService.deleteUser(user.getUsername());

            } catch (Exception e) {
                LOGGER.error("Error cleaning up expired registration for token {}", token.getId(), e);
            }
        });

        LOGGER.info("Cleanup completed. Removed {} expired registrations", expiredTokens.size());
    }
}

