package gr.aueb.cf.projectmanagementapp.service;

import gr.aueb.cf.projectmanagementapp.core.exceptions.AppObjectAlreadyExistsException;
import gr.aueb.cf.projectmanagementapp.core.exceptions.AppObjectDeletionConflictException;
import gr.aueb.cf.projectmanagementapp.core.exceptions.AppObjectInvalidArgumentException;
import gr.aueb.cf.projectmanagementapp.core.exceptions.AppObjectNotFoundException;
import gr.aueb.cf.projectmanagementapp.dto.RoleCreateDTO;
import gr.aueb.cf.projectmanagementapp.dto.RoleReadOnlyDTO;
import gr.aueb.cf.projectmanagementapp.dto.RoleUpdateDTO;

import java.util.List;

public interface IRoleService {
    List<RoleReadOnlyDTO> findAllRoles();
    RoleReadOnlyDTO findRoleById(Long id) throws AppObjectNotFoundException;
    RoleReadOnlyDTO createRole(RoleCreateDTO dto) throws AppObjectAlreadyExistsException, AppObjectInvalidArgumentException;
    RoleReadOnlyDTO updateRole(Long id, RoleUpdateDTO dto) throws AppObjectAlreadyExistsException, AppObjectInvalidArgumentException, AppObjectNotFoundException;
    void deleteRole(Long id) throws AppObjectNotFoundException, AppObjectDeletionConflictException;
}