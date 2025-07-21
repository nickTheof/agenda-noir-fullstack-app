package gr.aueb.cf.projectmanagementapp.repository;

import gr.aueb.cf.projectmanagementapp.model.PasswordResetToken;
import gr.aueb.cf.projectmanagementapp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
    Optional<PasswordResetToken> findByToken(String token);
}
