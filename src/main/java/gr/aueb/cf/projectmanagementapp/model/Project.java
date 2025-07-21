package gr.aueb.cf.projectmanagementapp.model;

import gr.aueb.cf.projectmanagementapp.core.enums.ProjectStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "projects",
        indexes = {
                @Index(name = "idx_project_uuid", columnList = "uuid"),
                @Index(name = "idx_project_owner", columnList = "user_id"),
                @Index(name = "idx_project_status", columnList = "status")
        })
public class Project extends AbstractEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, updatable = false, unique = true)
    private String uuid;

    @Column(nullable = false)
    private String name;

    private String description;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @ColumnDefault("false")
    @Column(nullable = false, name = "is_deleted")
    private Boolean isDeleted;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProjectStatus status;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User owner;

    @Getter(AccessLevel.PRIVATE)
    @OneToMany(mappedBy = "project", orphanRemoval = true)
    private Set<Ticket> tickets = new HashSet<>();

    @PrePersist
    protected void onCreate() {
        if (uuid == null) uuid = UUID.randomUUID().toString();
        if (isDeleted == null) isDeleted = false;
        if (status == null) status = ProjectStatus.OPEN;
    }

    // Manage project tickets helper methods
    public Set<Ticket> getAllTickets(){
        return Collections.unmodifiableSet(tickets);
    }

    public void addTicket(Ticket ticket) {
        if (ticket == null) return;
        if (tickets == null) tickets = new HashSet<>();
        tickets.add(ticket);
        ticket.setProject(this);
    }

    public void removeTicket(Ticket ticket) {
        if (ticket == null) return;
        if (tickets == null) return;
        tickets.remove(ticket);
        ticket.setProject(null);
    }
}
