package gr.aueb.cf.projectmanagementapp.service;

import gr.aueb.cf.projectmanagementapp.authentication.AuthenticationService;
import gr.aueb.cf.projectmanagementapp.core.exceptions.AppObjectAlreadyExistsException;
import gr.aueb.cf.projectmanagementapp.core.exceptions.AppObjectNotAuthorizedException;
import gr.aueb.cf.projectmanagementapp.core.exceptions.AppObjectNotFoundException;
import gr.aueb.cf.projectmanagementapp.dto.*;
import gr.aueb.cf.projectmanagementapp.mapper.Mapper;
import gr.aueb.cf.projectmanagementapp.model.User;
import gr.aueb.cf.projectmanagementapp.model.VerificationToken;
import gr.aueb.cf.projectmanagementapp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService implements IUserService {
    private final UserRepository userRepository;
    private final AuthenticationService authenticationService;
    private final Mapper mapper;


    @Override
    public List<UserReadOnlyDTO> findAllUsers() {
        return userRepository.findAll().stream().map(mapper::mapToUserReadOnlyDTO).collect(Collectors.toList());
    }

    @Transactional
    @Override
    public UserReadOnlyDTO findUserByUuid(String uuid) throws AppObjectNotFoundException {
        User user = userRepository.findByUuid(uuid).orElseThrow(() -> new AppObjectNotFoundException("User", "User with uuid " + uuid + " not found"));
        return mapper.mapToUserReadOnlyDTO(user);
    }

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
    public UserReadOnlyDTO insertVerifiedUser(UserRegisterDTO dto) throws AppObjectAlreadyExistsException {
        if (userRepository.findByUsername(dto.username()).isPresent()){
            throw new AppObjectAlreadyExistsException("User", "User with username " + dto.username() + " already exists");
        }
        User user = mapper.mapToUser(dto);
        user.setVerified(true);
        user.setEnabled(true);
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

    @Transactional(rollbackFor = {AppObjectAlreadyExistsException.class, AppObjectNotFoundException.class})
    @Override
    public UserReadOnlyDTO updateUserByUUID(String uuid, UserUpdateDTO dto) throws AppObjectNotFoundException, AppObjectAlreadyExistsException {
        User fetchedUser = userRepository.findByUuid(uuid).orElseThrow(() -> new AppObjectNotFoundException("User", "User with username " + uuid + " not found"));
        Optional<User> optionalUser = userRepository.findByUsername(dto.username());
        if (optionalUser.isPresent() && !optionalUser.get().getUuid().equals(fetchedUser.getUuid())) {
            throw new AppObjectAlreadyExistsException("User", "User with username " + dto.username() + " already exists");
        }
        User toUpdate = mapper.mapToUser(dto, fetchedUser);
        User updatedUser = userRepository.save(toUpdate);
        return mapper.mapToUserReadOnlyDTO(updatedUser);
    }

    @Transactional(rollbackFor = {AppObjectNotFoundException.class})
    @Override
    public UserReadOnlyDTO updateUserByUUID(String uuid, UserPatchDTO dto) throws AppObjectNotFoundException {
        User fetchedUser = userRepository.findByUuid(uuid).orElseThrow(() -> new AppObjectNotFoundException("User", "User with username " + uuid + " not found"));
        User toUpdate = mapper.mapToUser(dto, fetchedUser);
        User updatedUser = userRepository.save(toUpdate);
        return mapper.mapToUserReadOnlyDTO(updatedUser);
    }

    @Transactional(rollbackFor = {AppObjectNotFoundException.class, AppObjectNotAuthorizedException.class})
    @Override
    public void changeUserPassword(String uuid, ChangePasswordDTO dto) throws AppObjectNotFoundException, AppObjectNotAuthorizedException {
        User user = userRepository.findByUuid(uuid).orElseThrow(() -> new AppObjectNotFoundException("User", "User with uuid " + uuid + " not found"));
        boolean isOldPasswordValid = authenticationService.isPasswordValid(user.getUsername(), dto.oldPassword());
        if (!isOldPasswordValid) {
            throw new AppObjectNotAuthorizedException("User", "User with username: " + user.getUsername() + " not authorized");
        }
        UserUpdateDTO updateDTO = new UserUpdateDTO(dto.newPassword());
        User updatedUser = mapper.mapToUser(updateDTO, user);
        userRepository.save(updatedUser);
    }

    //TODO! If a user has projects open? The Projects - Tickets are deleted by cascade ???
    @Transactional
    @Override
    public void deleteUserByUuid(String uuid) throws AppObjectNotFoundException {
        Optional<User> user = userRepository.findByUuid(uuid);
        if (user.isEmpty()) {
            throw new AppObjectNotFoundException("User", "User with uuid " + uuid + " not found");
        }
        userRepository.delete(user.get());
    }
}
