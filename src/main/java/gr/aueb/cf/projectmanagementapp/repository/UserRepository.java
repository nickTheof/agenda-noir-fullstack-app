package gr.aueb.cf.projectmanagementapp.repository;

import gr.aueb.cf.projectmanagementapp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {
    Optional<User> findByUuid(String uuid);
    Boolean existsByUuid(String uuid);
    Optional<User> findByUsername(String username);
}
