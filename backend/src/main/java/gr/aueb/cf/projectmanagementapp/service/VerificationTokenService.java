package gr.aueb.cf.projectmanagementapp.service;

import gr.aueb.cf.projectmanagementapp.core.exceptions.AppObjectNotFoundException;
import gr.aueb.cf.projectmanagementapp.model.User;
import gr.aueb.cf.projectmanagementapp.model.VerificationToken;
import gr.aueb.cf.projectmanagementapp.repository.VerificationTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class VerificationTokenService {
    private static final int TOKEN_EXPIRATION_HOURS = 24;

    private final VerificationTokenRepository verificationTokenRepository;

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
