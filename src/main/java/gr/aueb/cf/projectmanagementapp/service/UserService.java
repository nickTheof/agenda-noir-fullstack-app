package gr.aueb.cf.projectmanagementapp.service;

import gr.aueb.cf.projectmanagementapp.core.exceptions.AppObjectAlreadyExistsException;
import gr.aueb.cf.projectmanagementapp.core.exceptions.AppObjectNotFoundException;
import gr.aueb.cf.projectmanagementapp.dto.UserReadOnlyDTO;
import gr.aueb.cf.projectmanagementapp.dto.UserRegisterDTO;
import gr.aueb.cf.projectmanagementapp.dto.UserUpdateDTO;
import gr.aueb.cf.projectmanagementapp.mapper.Mapper;
import gr.aueb.cf.projectmanagementapp.model.User;
import gr.aueb.cf.projectmanagementapp.model.VerificationToken;
import gr.aueb.cf.projectmanagementapp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService implements IUserService {
    private final UserRepository userRepository;
    private final Mapper mapper;

    @Transactional
    @Override
    public UserReadOnlyDTO registerUser(UserRegisterDTO dto) throws AppObjectAlreadyExistsException {
        if (userRepository.findByUsername(dto.username()).isPresent()){
            throw new AppObjectAlreadyExistsException("User", "User with username " + dto.username() + " already exists");
        }
        User user = mapper.mapToUser(dto);
        user.createVerificationToken();
        User savedUser = userRepository.save(user);
        return mapper.mapToUserReadOnlyDTO(savedUser);
    }

    @Transactional
    @Override
    public VerificationToken getVerificationToken(String username) throws AppObjectNotFoundException {
        Optional<User> user = userRepository.findByUsername(username);
        if (user.isEmpty()) {
            throw new AppObjectNotFoundException("User", "User with username " + username + " not found");
        }
        if (user.get().getVerificationToken() == null) {
            throw new AppObjectNotFoundException("Token", "Verification token not found");
        }
        return user.get().getVerificationToken();
    }

    @Transactional
    @Override
    public void deleteUser(String username) throws AppObjectNotFoundException {
        Optional<User> user = userRepository.findByUsername(username);
        if (user.isEmpty()) {
            throw new AppObjectNotFoundException("User", "User with username " + username + " not found");
        }
        userRepository.delete(user.get());
    }

    @Transactional
    @Override
    public void updateUserPasswordAfterSuccessfulRecovery(User user, String newPassword) {
        UserUpdateDTO updateDTO = new UserUpdateDTO(newPassword);
        User updatedUser = mapper.mapToUser(updateDTO, user);
        updatedUser.clearPasswordResetToken();
        userRepository.save(updatedUser);
    }

    @Transactional
    @Override
    public void updateUserAfterSuccessfulVerification(User user) {
        user.verifyAccount();
        user.clearVerificationToken();
        userRepository.save(user);
    }
}
