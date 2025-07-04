package gr.aueb.cf.projectmanagementapp.service;

import gr.aueb.cf.projectmanagementapp.core.exceptions.AppObjectNotFoundException;
import gr.aueb.cf.projectmanagementapp.dto.RoleReadOnlyDTO;
import gr.aueb.cf.projectmanagementapp.dto.UserRoleInsertDTO;

import java.util.List;

public interface IUserRoleService {
    List<RoleReadOnlyDTO> findAllUserRoles(String uuid) throws AppObjectNotFoundException;
    List<RoleReadOnlyDTO> changeUserRoles(String uuid, UserRoleInsertDTO dto) throws AppObjectNotFoundException;
}
