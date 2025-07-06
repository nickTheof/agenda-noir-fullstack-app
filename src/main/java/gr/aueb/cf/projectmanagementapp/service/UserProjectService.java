package gr.aueb.cf.projectmanagementapp.service;

import gr.aueb.cf.projectmanagementapp.core.exceptions.AppObjectDeletionConflictException;
import gr.aueb.cf.projectmanagementapp.core.exceptions.AppObjectNotFoundException;
import gr.aueb.cf.projectmanagementapp.core.filters.ProjectFilters;
import gr.aueb.cf.projectmanagementapp.core.specifications.ProjectSpecification;
import gr.aueb.cf.projectmanagementapp.dto.*;
import gr.aueb.cf.projectmanagementapp.mapper.Mapper;
import gr.aueb.cf.projectmanagementapp.model.Project;
import gr.aueb.cf.projectmanagementapp.model.User;
import gr.aueb.cf.projectmanagementapp.repository.ProjectRepository;
import gr.aueb.cf.projectmanagementapp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserProjectService implements IUserProjectService {
    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;
    private final Mapper mapper;


    @Transactional(readOnly = true)
    @Override
    public List<ProjectReadOnlyDTO> findAllUserProjects(String userUuid) throws AppObjectNotFoundException {
        User user = userRepository.findByUuid(userUuid).orElseThrow(() -> new AppObjectNotFoundException("User", "User with uuid " + userUuid + " not found"));
        return user.getAllProjects().stream().map(mapper::mapToProjectReadOnlyDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public Paginated<ProjectReadOnlyDTO> findUserProjectsFilteredPaginated(ProjectFiltersDTO filters, String userUuid) {
        ProjectFilters projectFilters = mapper.mapToProjectFilters(filters, userUuid);
        var filtered = projectRepository.findAll(getSpecsFromFilters(projectFilters), projectFilters.getPageable());
        return new Paginated<>(filtered.map(mapper::mapToProjectReadOnlyDTO));
    }

    @Transactional(readOnly = true)
    @Override
    public ProjectReadOnlyDTO findUserProjectByUuid(String userUuid, String projectUuid) throws AppObjectNotFoundException {
        return mapper.mapToProjectReadOnlyDTO(getValidProject(userUuid, projectUuid));
    }

    @Transactional
    @Override
    public ProjectReadOnlyDTO createUserProject(String userUuid, ProjectCreateDTO project) throws AppObjectNotFoundException {
        User user = userRepository.findByUuid(userUuid).orElseThrow(() -> new AppObjectNotFoundException("User", "User " + userUuid + " not found"));
        Project newProject = mapper.mapToProject(project);
        newProject.setOwner(user);
        return mapper.mapToProjectReadOnlyDTO(projectRepository.save(newProject));
    }

    @Transactional
    @Override
    public ProjectReadOnlyDTO updateUserProject(String userUuid, String projectUuid, ProjectUpdateDTO updateDTO) throws AppObjectNotFoundException {
        Project project = getValidProject(userUuid, projectUuid);
        Project toUpdate = mapper.mapToProject(updateDTO, project);
        return mapper.mapToProjectReadOnlyDTO(projectRepository.save(toUpdate));
    }

    @Transactional
    @Override
    public ProjectReadOnlyDTO updateUserProject(String userUuid, String projectUuid, ProjectPatchDTO patchDTO) throws AppObjectNotFoundException {
        Project project = getValidProject(userUuid, projectUuid);
        Project toUpdate = mapper.mapToProject(patchDTO, project);
        return mapper.mapToProjectReadOnlyDTO(projectRepository.save(toUpdate));
    }

    @Transactional
    @Override
    public void deleteUserProject(String userUuid, String projectUuid) throws AppObjectNotFoundException, AppObjectDeletionConflictException {
        User user = userRepository.findByUuid(userUuid).orElseThrow(() -> new AppObjectNotFoundException("User", "User " + userUuid + " not found"));
        Project project = projectRepository.findByUuidAndOwnerUuid(projectUuid, userUuid).orElseThrow(() -> new AppObjectNotFoundException("Project", "Project " + projectUuid + " not found"));
        if (!project.getAllTickets().isEmpty()) throw new AppObjectDeletionConflictException("Project", "Project cannot be deleted. There are tickets in this project.");
        user.removeProject(project);
        userRepository.save(user);
    }

    private Project getValidProject(String userUuid, String projectUuid) throws AppObjectNotFoundException {
        if (!userRepository.existsByUuid(userUuid)) throw new AppObjectNotFoundException("User", "User " + userUuid + " not found");
        return projectRepository.findByUuidAndOwnerUuid(projectUuid, userUuid).orElseThrow(() -> new AppObjectNotFoundException("Project", "Project " + projectUuid + " not found"));
    }

    private Specification<Project> getSpecsFromFilters(ProjectFilters filters) {
        Specification<Project> spec = (root, query, builder) -> null;
        if (filters.getUuid() != null) {
            spec = spec.and(ProjectSpecification.projectsFieldLike("uuid", filters.getUuid()));
        }
        if (filters.getName() != null) {
            spec = spec.and(ProjectSpecification.projectsFieldLike("name", filters.getName()));
        }
        if (filters.getIsDeleted() != null) {
            spec = spec.and(ProjectSpecification.projectsBooleanFieldIs("isDeleted", filters.getIsDeleted()));
        }
        if (filters.getStatus() != null) {
            spec = spec.and(ProjectSpecification.projectStatusIn(filters.getStatus()));
        }
        if (filters.getOwnerUuid() != null) {
            spec = spec.and(ProjectSpecification.projectsOwnerIs(filters.getOwnerUuid()));
        }
        return spec;
    }
}
