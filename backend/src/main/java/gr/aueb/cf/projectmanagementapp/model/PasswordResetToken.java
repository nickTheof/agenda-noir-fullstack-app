package gr.aueb.cf.projectmanagementapp.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name="password_reset_tokens",
        indexes = {
                @Index(name = "idx_password_reset_token", columnList = "token"),
                @Index(name = "idx_password_reset_user", columnList = "user_id")
        })
public class PasswordResetToken extends AbstractToken{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(nullable = false, name = "user_id", unique = true)
    private User user;

    public PasswordResetToken(User user, int customExpiryMinutes) {
        super(customExpiryMinutes);// Custom expiry
        this.user = user;
    }
}
