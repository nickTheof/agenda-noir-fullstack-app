package gr.aueb.cf.projectmanagementapp.service;

import gr.aueb.cf.projectmanagementapp.core.exceptions.AppObjectAlreadyExistsException;
import gr.aueb.cf.projectmanagementapp.core.exceptions.AppObjectNotFoundException;
import gr.aueb.cf.projectmanagementapp.dto.UserReadOnlyDTO;
import gr.aueb.cf.projectmanagementapp.dto.UserRegisterDTO;
import gr.aueb.cf.projectmanagementapp.model.User;
import gr.aueb.cf.projectmanagementapp.model.VerificationToken;

public interface IUserService {
    UserReadOnlyDTO registerUser(UserRegisterDTO dto) throws AppObjectAlreadyExistsException;
    VerificationToken getVerificationToken(String token) throws AppObjectNotFoundException;
    void deleteUser(String username) throws AppObjectNotFoundException;
    void updateUserPasswordAfterSuccessfulRecovery(User user, String newPassword);
    void updateUserAfterSuccessfulVerification(User user);
}
