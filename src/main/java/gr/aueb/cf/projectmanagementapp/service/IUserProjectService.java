package gr.aueb.cf.projectmanagementapp.service;

import gr.aueb.cf.projectmanagementapp.core.exceptions.AppObjectDeletionConflictException;
import gr.aueb.cf.projectmanagementapp.core.exceptions.AppObjectNotFoundException;
import gr.aueb.cf.projectmanagementapp.dto.*;

import java.util.List;

public interface IUserProjectService {
    List<ProjectReadOnlyDTO> findAllUserProjects(String userUuid) throws AppObjectNotFoundException;
    Paginated<ProjectReadOnlyDTO> findUserProjectsFilteredPaginated(ProjectFiltersDTO filters, String userUuid);
    ProjectReadOnlyDTO findUserProjectByUuid(String userUuid, String projectUuid) throws AppObjectNotFoundException;
    ProjectReadOnlyDTO createUserProject(String userUuid, ProjectCreateDTO project) throws AppObjectNotFoundException;
    ProjectReadOnlyDTO updateUserProject(String userUuid, String projectUuid, ProjectUpdateDTO project) throws AppObjectNotFoundException;
    ProjectReadOnlyDTO updateUserProject(String userUuid, String projectUuid, ProjectPatchDTO project) throws AppObjectNotFoundException;
    void deleteUserProject(String userUuid, String projectUuid) throws AppObjectNotFoundException, AppObjectDeletionConflictException;
}
