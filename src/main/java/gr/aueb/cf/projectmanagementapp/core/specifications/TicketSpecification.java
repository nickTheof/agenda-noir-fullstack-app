package gr.aueb.cf.projectmanagementapp.core.specifications;

import gr.aueb.cf.projectmanagementapp.model.Project;
import gr.aueb.cf.projectmanagementapp.model.Ticket;
import gr.aueb.cf.projectmanagementapp.model.User;
import jakarta.persistence.criteria.Join;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

public class TicketSpecification {
    private static final Set<String> allowedLikeFields = Set.of("uuid", "title");

    private TicketSpecification() {}

    public static Specification<Ticket> ticketsFieldLike(String field, String value) {
        return ((root, query, builder) -> {
            if (field == null || field.isBlank() || !allowedLikeFields.contains(field)) return builder.conjunction();
            if (value == null || value.isBlank()) return builder.conjunction();
            return builder.like(builder.upper(root.get(field)), value.toUpperCase() + "%");
        });
    }

    public static Specification<Ticket> ticketStatusIn(List<String> statusList) {
        return (root, query, builder) -> {
            if (statusList == null || statusList.isEmpty()) return builder.conjunction();
            return root.get("status").in(statusList);
        };
    }
    public static Specification<Ticket> ticketPriorityIn(List<String> priorityList) {
        return (root, query, builder) -> {
            if (priorityList == null || priorityList.isEmpty()) return builder.conjunction();
            return root.get("priority").in(priorityList);
        };
    }

    public static Specification<Ticket> ticketProjectIs(String projectUuid) {
        return (root, query, builder) -> {
            if (projectUuid == null || projectUuid.isBlank()) return builder.conjunction();
            Join<Ticket, Project> projectJoin = root.join("project");
            return builder.equal(projectJoin.get("uuid"), projectUuid);
        };
    }

    public static Specification<Ticket> ticketProjectOwnerIs(String ownerUuid) {
        return (root, query, builder) -> {
            if (ownerUuid == null || ownerUuid.isBlank()) return builder.conjunction();
            Join<Ticket, Project> projectJoin = root.join("project");
            Join<Project, User> userJoin = projectJoin.join("owner");
            return builder.equal(userJoin.get("uuid"), ownerUuid);
        };
    }

    public static Specification<Ticket> ticketExpiresBefore(LocalDate date) {
        return (root, query, builder) -> {
            if (date == null) return builder.conjunction();
            return builder.lessThanOrEqualTo(root.get("expiryDate"), date);
        };
    }
}
