package gr.aueb.cf.projectmanagementapp.service;

import gr.aueb.cf.projectmanagementapp.core.exceptions.AppObjectAlreadyExistsException;
import gr.aueb.cf.projectmanagementapp.core.exceptions.AppObjectNotAuthorizedException;
import gr.aueb.cf.projectmanagementapp.core.exceptions.AppObjectNotFoundException;
import gr.aueb.cf.projectmanagementapp.dto.*;
import gr.aueb.cf.projectmanagementapp.model.User;
import gr.aueb.cf.projectmanagementapp.model.VerificationToken;

import java.util.List;

public interface IUserService {
    List<UserReadOnlyDTO> findAllUsers();
    Paginated<UserReadOnlyDTO> findUsersFilteredPaginated(UserFiltersDTO filters);
    UserReadOnlyDTO findUserByUuid(String uuid) throws AppObjectNotFoundException;
    UserReadOnlyDTO registerUser(UserRegisterDTO dto) throws AppObjectAlreadyExistsException;
    UserReadOnlyDTO insertVerifiedUser(UserRegisterDTO dto) throws AppObjectAlreadyExistsException;
    VerificationToken getVerificationToken(String token) throws AppObjectNotFoundException;
    void deleteUser(String username) throws AppObjectNotFoundException;
    void updateUserPasswordAfterSuccessfulRecovery(User user, String newPassword);
    void updateUserAfterSuccessfulVerification(User user);
    UserReadOnlyDTO updateUserByUUID(String uuid, UserUpdateDTO dto) throws AppObjectNotFoundException, AppObjectAlreadyExistsException;
    UserReadOnlyDTO updateUserByUUID(String uuid, UserPatchDTO dto) throws AppObjectNotFoundException;
    void changeUserPassword(String uuid, ChangePasswordDTO passwordDTO) throws AppObjectNotFoundException, AppObjectNotAuthorizedException;
    void deleteUserByUuid(String uuid) throws AppObjectNotFoundException;
    List<RoleReadOnlyDTO> findAllUserRoles(String uuid) throws AppObjectNotFoundException;
    List<RoleReadOnlyDTO> changeUserRoles(String uuid, UserRoleInsertDTO dto) throws AppObjectNotFoundException;
}
