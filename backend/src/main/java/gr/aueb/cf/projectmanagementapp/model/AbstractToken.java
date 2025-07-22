package gr.aueb.cf.projectmanagementapp.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@MappedSuperclass
@Getter
@Setter
@NoArgsConstructor
public abstract class AbstractToken extends AbstractEntity {
    private static final int DEFAULT_EXPIRATION_MINUTES = 60;

    @Column(nullable = false, unique = true)
    private String token;

    @Column(name = "expiry_date", nullable = false)
    private LocalDateTime expiryDate;

    protected AbstractToken(int expiryTimeInMinutes) {
        this.token = generateRandomToken();
        this.expiryDate = calculateExpiryDate(expiryTimeInMinutes);
    }

    @PrePersist
    protected void onCreate() {
        if (expiryDate == null) {
            expiryDate = calculateExpiryDate(DEFAULT_EXPIRATION_MINUTES);
        }
        if (token == null) {
            token = generateRandomToken();
        }
    }

    // Business logic methods
    public boolean isTokenValid() {
        return expiryDate != null && expiryDate.isAfter(LocalDateTime.now());
    }

    // Private helpers
    private String generateRandomToken() {
        return UUID.randomUUID().toString();
    }

    private LocalDateTime calculateExpiryDate(int expiryTimeInMinutes) {
        return LocalDateTime.now().plusMinutes(expiryTimeInMinutes);
    }
}
