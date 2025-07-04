package gr.aueb.cf.projectmanagementapp.repository;

import gr.aueb.cf.projectmanagementapp.model.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface ProjectRepository extends JpaRepository<Project, Long>, JpaSpecificationExecutor<Project> {
    Optional<Project> findByUuidAndOwnerUuid(String projectUuid, String ownerUuid);
}
