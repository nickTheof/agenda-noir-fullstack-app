package gr.aueb.cf.projectmanagementapp.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name="verification_tokens",
        indexes = {
                @Index(name = "idx_verification_token", columnList = "token"),
                @Index(name = "idx_verification_token_user", columnList = "user_id")
        })
public class VerificationToken extends AbstractToken{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(nullable = false, name = "user_id", unique = true)
    private User user;

    public VerificationToken(User user) {
        super();
        this.user = user;
    }

    public VerificationToken(User user, int customExpiryMinutes) {
        super(customExpiryMinutes);
        this.user = user;
    }

}
