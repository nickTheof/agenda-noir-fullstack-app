package gr.aueb.cf.projectmanagementapp.model;

import gr.aueb.cf.projectmanagementapp.core.enums.TicketPriority;
import gr.aueb.cf.projectmanagementapp.core.enums.TicketStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "tickets",
        indexes = {
                @Index(name = "idx_ticket_uuid", columnList = "uuid"),
                @Index(name = "idx_ticket_project", columnList = "project_id"),
                @Index(name = "idx_ticket_status", columnList = "status"),
                @Index(name = "idx_ticket_priority", columnList = "priority")
        })
public class Ticket extends AbstractEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, updatable = false, unique = true)
    private String uuid;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String description;

    @Enumerated(EnumType.STRING)
    private TicketPriority priority;

    @Enumerated(EnumType.STRING)
    private TicketStatus status;

    @Column(name = "expiry_date", nullable = false)
    private LocalDate expiryDate;

    @ManyToOne
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @PrePersist
    protected void onCreate() {
        if (uuid == null) uuid = UUID.randomUUID().toString();
        if (priority == null) priority = TicketPriority.MEDIUM;
        if (status == null) status = TicketStatus.OPEN;
    }

}
