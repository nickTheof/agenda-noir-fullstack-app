package gr.aueb.cf.projectmanagementapp.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "users",
        indexes = {
                @Index(name = "idx_user_username", columnList = "username"),
                @Index(name = "idx_user_uuid", columnList = "uuid"),
        })
public class User extends AbstractEntity implements UserDetails {

    private static final int MAX_FAILED_ATTEMPTS = 5;
    private static final int MAX_PASSWORD_VALIDITY_DAYS = 90;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, updatable = false)
    private String uuid;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String firstname;

    @Column(nullable = false)
    private String lastname;

    @Column(nullable = false)
    private String password;

    @Column(name = "password_last_modified", nullable = false)
    private Instant passwordLastModified;

    @ColumnDefault("0")
    @Column(name = "login_consecutive_fail_attempts")
    private Integer loginConsecutiveFailAttempts;


    @ColumnDefault("false")
    @Column(nullable = false)
    private Boolean enabled;

    @ColumnDefault("false")
    @Column(nullable = false)
    private Boolean verified;


    @ColumnDefault("true")
    @Column(name="account_non_locked", nullable = false)
    private Boolean accountNonLocked;

    @Column(name = "lock_time")
    private LocalDateTime lockTime;

    @Column(name="deleted_at")
    private LocalDateTime deletedAt;

    @ColumnDefault("false")
    @Column(name="is_deleted")
    private Boolean isDeleted;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private PasswordResetToken passwordResetToken;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private VerificationToken verificationToken;

    @Getter(AccessLevel.PRIVATE)
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "users_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"),
            indexes = @Index(name = "idx_user_role", columnList = "user_id,role_id")
    )
    private Set<Role> roles = new HashSet<>();

    @Getter(AccessLevel.PRIVATE)
    @OneToMany(mappedBy = "owner")
    private Set<Project> projects = new HashSet<>();

    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.roles.stream()
                .flatMap(role -> role.getAllPermissions().stream())
                .map(permission -> new SimpleGrantedAuthority(permission.getName()))
                .collect(Collectors.toSet());
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return accountNonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return passwordLastModified.isAfter(Instant.now().minus(90, ChronoUnit.DAYS));
    }

    @Override
    public boolean isEnabled() {
        return enabled && verified && !isDeleted;
    }

    @PrePersist
    protected void onCreate() {
        if (uuid == null) uuid = UUID.randomUUID().toString();
        if (passwordLastModified == null) passwordLastModified = Instant.now();
        if (loginConsecutiveFailAttempts == null) loginConsecutiveFailAttempts = 0;
        if (enabled == null) enabled = false;
        if (verified == null) verified = false;
        if (accountNonLocked == null) accountNonLocked = true;
        if (isDeleted == null) isDeleted = false;
    }

    // Helper methods for business logic
    public void lockAccount() {
        this.setAccountNonLocked(false);
        this.setLockTime(LocalDateTime.now());
    }

    public void unlockAccount() {
        this.setAccountNonLocked(true);
        this.setLockTime(null);
        this.setLoginConsecutiveFailAttempts(0);
    }

    public void verifyAccount() {
        this.verified = true;
        if (!this.enabled) {
            this.enabled = true;
        }
    }

    public void updatePassword(String newPassword) {
        this.password = newPassword;
        this.passwordLastModified = Instant.now();
    }

    // Password reset methods
    public void createPasswordResetToken(Integer expiryTime) {
        this.passwordResetToken = new PasswordResetToken(this, expiryTime);
    }

    public void clearPasswordResetToken() {
        if (this.passwordResetToken != null) {
            this.passwordResetToken.setUser(null);
            this.passwordResetToken = null;
        }
    }

    public boolean hasValidPasswordResetToken() {
        return this.passwordResetToken != null && this.passwordResetToken.isTokenValid();
    }

    //Verification methods
    public void createVerificationToken() {
        this.verificationToken = new VerificationToken(this);
    }

    public void createVerificationToken(Integer expiryTime) {
        this.verificationToken = new VerificationToken(this, expiryTime);
    }

    public void clearVerificationToken() {
        if (this.verificationToken != null) {
            this.verificationToken.setUser(null);
            this.verificationToken = null;
        }
    }

    public boolean hasValidVerificationToken() {
        return this.verificationToken != null && this.verificationToken.isTokenValid();
    }

    // Enhanced Security Methods
    public void incrementFailedAttempts() {
        if (loginConsecutiveFailAttempts == null) {
            loginConsecutiveFailAttempts = 0;
        }
        loginConsecutiveFailAttempts++;
        if (loginConsecutiveFailAttempts >= MAX_FAILED_ATTEMPTS) {
            lockAccount();
        }
    }

    public void resetFailedAttempts() {
        this.loginConsecutiveFailAttempts = 0;
    }

    public Set<Role> getAllRoles() {
        return Collections.unmodifiableSet(roles);
    }

    public void addRole(Role role) {
        if (role == null) return;
        if (roles == null) roles = new HashSet<>();
        this.roles.add(role);
        role.getUsers().add(this);
    }

    public void removeRole(Role role) {
        if (role == null) return;
        if (roles == null) return;
        this.roles.remove(role);
        role.getUsers().remove(this);
    }

    // Soft delete user
    public void softDelete(){
        this.deletedAt = LocalDateTime.now();
        this.verified = false;
        this.enabled = false;
        this.isDeleted = true;
        this.clearPasswordResetToken();
        this.clearVerificationToken();
    }


    // Project Management Helper methods
    public Set<Project> getAllProjects() {
        return Collections.unmodifiableSet(projects);
    }

    public void addProject(Project project) {
        if (project == null) return;
        if (projects == null) projects = new HashSet<>();
        this.projects.add(project);
        project.setOwner(this);
    }

    public void removeProject(Project project) {
        if (project == null) return;
        if (projects == null) return;
        this.projects.remove(project);
        project.setOwner(null);
    }
}
