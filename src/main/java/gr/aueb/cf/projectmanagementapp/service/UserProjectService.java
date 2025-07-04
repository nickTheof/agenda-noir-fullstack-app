package gr.aueb.cf.projectmanagementapp.service;

import gr.aueb.cf.projectmanagementapp.core.exceptions.AppObjectDeletionConflictException;
import gr.aueb.cf.projectmanagementapp.core.exceptions.AppObjectNotFoundException;
import gr.aueb.cf.projectmanagementapp.core.exceptions.ValidationException;
import gr.aueb.cf.projectmanagementapp.dto.ProjectCreateDTO;
import gr.aueb.cf.projectmanagementapp.dto.ProjectPatchDTO;
import gr.aueb.cf.projectmanagementapp.dto.ProjectReadOnlyDTO;
import gr.aueb.cf.projectmanagementapp.dto.ProjectUpdateDTO;
import gr.aueb.cf.projectmanagementapp.mapper.Mapper;
import gr.aueb.cf.projectmanagementapp.model.Project;
import gr.aueb.cf.projectmanagementapp.model.User;
import gr.aueb.cf.projectmanagementapp.repository.ProjectRepository;
import gr.aueb.cf.projectmanagementapp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
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


    @Transactional
    @Override
    public List<ProjectReadOnlyDTO> findAllUserProjects(String userUuid) throws AppObjectNotFoundException {
        User user = userRepository.findByUuid(userUuid).orElseThrow(() -> new AppObjectNotFoundException("User", "User with uuid " + userUuid + " not found"));
        return user.getAllProjects().stream().map(mapper::mapToProjectReadOnlyDTO).collect(Collectors.toList());
    }

    @Transactional
    @Override
    public ProjectReadOnlyDTO findUserProjectByUuid(String userUuid, String projectUuid) throws AppObjectNotFoundException {
        if (userRepository.findByUuid(userUuid).isEmpty()) throw new AppObjectNotFoundException("User", "User " + userUuid + " not found");
        Project project = projectRepository.findByUuidAndOwnerUuid(projectUuid, userUuid).orElseThrow(() -> new AppObjectNotFoundException("Project", "Project with uuid " + projectUuid + " not found"));
        return mapper.mapToProjectReadOnlyDTO(project);
    }

    @Transactional
    @Override
    public ProjectReadOnlyDTO createUserProject(String userUuid, ProjectCreateDTO project) throws AppObjectNotFoundException {
        User user = userRepository.findByUuid(userUuid).orElseThrow(() -> new AppObjectNotFoundException("User", "User " + userUuid + " not found"));
        Project newProject = mapper.mapToProject(project);
        newProject.setOwner(user);
        Project savedProject = projectRepository.save(newProject);
        return mapper.mapToProjectReadOnlyDTO(savedProject);
    }

    @Transactional
    @Override
    public ProjectReadOnlyDTO updateUserProject(String userUuid, String projectUuid, ProjectUpdateDTO updateDTO) throws AppObjectNotFoundException {
        if (userRepository.findByUuid(userUuid).isEmpty()) throw new AppObjectNotFoundException("User", "User " + userUuid + " not found");
        Project project = projectRepository.findByUuidAndOwnerUuid(projectUuid, userUuid).orElseThrow(() -> new AppObjectNotFoundException("Project", "Project with uuid " + projectUuid + " not found"));
        Project toUpdate = mapper.mapToProject(updateDTO, project);
        Project updatedProject = projectRepository.save(toUpdate);
        return mapper.mapToProjectReadOnlyDTO(updatedProject);
    }

    @Transactional
    @Override
    public ProjectReadOnlyDTO updateUserProject(String userUuid, String projectUuid, ProjectPatchDTO patchDTO) throws AppObjectNotFoundException {
        if (userRepository.findByUuid(userUuid).isEmpty()) throw new AppObjectNotFoundException("User", "User with uuid " + userUuid + " not found");
        Project project = projectRepository.findByUuidAndOwnerUuid(projectUuid, userUuid).orElseThrow(() -> new AppObjectNotFoundException("Project", "Project with uuid " + projectUuid + " not found"));
        Project toUpdate = mapper.mapToProject(patchDTO, project);
        Project updatedProject = projectRepository.save(toUpdate);
        return mapper.mapToProjectReadOnlyDTO(updatedProject);
    }

    @Transactional
    @Override
    public void deleteUserProject(String userUuid, String projectUuid) throws AppObjectNotFoundException, AppObjectDeletionConflictException {
        User user = userRepository.findByUuid(userUuid).orElseThrow(() -> new AppObjectNotFoundException("User", "User " + userUuid + " not found"));
        Project project = projectRepository.findByUuidAndOwnerUuid(projectUuid, userUuid).orElseThrow(() -> new AppObjectNotFoundException("Project", "Project with uuid " + projectUuid + " not found"));
        if (!project.getAllTickets().isEmpty()) throw new AppObjectDeletionConflictException("Project", "Project cannot be deleted. There are tickets in this project.");
        user.removeProject(project);
        userRepository.save(user);
    }
}
