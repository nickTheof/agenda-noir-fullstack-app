package gr.aueb.cf.projectmanagementapp.service;

import gr.aueb.cf.projectmanagementapp.authentication.AuthenticationService;
import gr.aueb.cf.projectmanagementapp.core.exceptions.AppObjectAlreadyExistsException;
import gr.aueb.cf.projectmanagementapp.core.exceptions.AppObjectNotAuthorizedException;
import gr.aueb.cf.projectmanagementapp.core.exceptions.AppObjectNotFoundException;
import gr.aueb.cf.projectmanagementapp.core.filters.UserFilters;
import gr.aueb.cf.projectmanagementapp.dto.*;
import gr.aueb.cf.projectmanagementapp.mapper.Mapper;
import gr.aueb.cf.projectmanagementapp.model.User;
import gr.aueb.cf.projectmanagementapp.model.VerificationToken;
import gr.aueb.cf.projectmanagementapp.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private AuthenticationService authenticationService;

    @Mock
    private Mapper mapper;

    @InjectMocks
    private UserService userService;

    private User testUser;
    private UserReadOnlyDTO testUserReadOnlyDTO;
    private final Long testId = 1L;
    private final String testUuid = UUID.randomUUID().toString();
    private final String testUsername = "testuser";
    private final String testFirstName = "testfirstname";
    private final String testLastName = "testlastname";
    private final String testPassword = "testpassword";

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(testId)
                .uuid(testUuid)
                .username(testUsername)
                .firstname(testFirstName)
                .lastname(testLastName)
                .password(testPassword)
                .enabled(true)
                .verified(true)
                .isDeleted(false)
                .build();

        testUserReadOnlyDTO = new UserReadOnlyDTO(
                testId,
                testUuid,
                testUsername,
                testFirstName,
                testLastName,
                true,
                true,
                false,
                0
        );
    }

    @Test
    void testFindAllUsersShouldReturnListOfUsersDTO() {
        when(userRepository.findAll()).thenReturn(List.of(testUser));
        when(mapper.mapToUserReadOnlyDTO(testUser)).thenReturn(testUserReadOnlyDTO);

        List<UserReadOnlyDTO> result = userService.findAllUsers();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testUuid, result.getFirst().uuid());
    }

    @Test
    void testFindUsersFilteredPaginatedShouldReturnPaginatedResults() {
        int page = 0;
        int size = 15;
        String sortBy = "id";
        String direction = "ASC";

        UserFiltersDTO filtersDTO = new UserFiltersDTO(
                page, size, sortBy, direction,
                testUuid, testUsername, testLastName, true, true, false, new ArrayList<>()
        );

        UserFilters filters = UserFilters.builder()
                .uuid(filtersDTO.uuid())
                .username(filtersDTO.username())
                .lastname(filtersDTO.lastname())
                .enabled(filtersDTO.enabled())
                .verified(filtersDTO.verified())
                .isDeleted(filtersDTO.isDeleted())
                .permissions(filtersDTO.permissions())
                .build();
        filters.setPage(filtersDTO.page());
        filters.setSize(filtersDTO.size());
        filters.setSortBy(filtersDTO.sortBy());
        filters.setOrderBy(Sort.Direction.valueOf(filtersDTO.orderBy()));
        Pageable pageable = filters.getPageable();
        Page<User> userPage = new PageImpl<>(List.of(testUser), pageable, 1);

        when(mapper.mapToUserFilters(filtersDTO)).thenReturn(filters);
        when(userRepository.findAll(ArgumentMatchers.<Specification<User>>any(), eq(filters.getPageable()))).thenReturn(userPage);
        when(mapper.mapToUserReadOnlyDTO(testUser)).thenReturn(testUserReadOnlyDTO);

        Paginated<UserReadOnlyDTO> result = userService.findUsersFilteredPaginated(filtersDTO);

        assertNotNull(result);
        assertEquals(1, result.data().size());
        assertEquals(testUuid, result.data().getFirst().uuid());
    }

    @Test
    void testFindUserByUuidWhenUserNotExistsShouldThrowException() {
        when(userRepository.findByUuid(testUuid)).thenReturn(Optional.empty());

        assertThrows(AppObjectNotFoundException.class, () -> userService.findUserByUuid(testUuid));
    }

    @Test
    void findUserByUuidWhenUserExistsShouldReturnUser() throws AppObjectNotFoundException {
        when(userRepository.findByUuid(testUuid)).thenReturn(Optional.of(testUser));
        when(mapper.mapToUserReadOnlyDTO(testUser)).thenReturn(testUserReadOnlyDTO);

        UserReadOnlyDTO result = userService.findUserByUuid(testUuid);

        assertNotNull(result);
        assertEquals(testUuid, result.uuid());
        verify(userRepository, times(1)).findByUuid(testUuid);
    }

    @Test
    void testRegisterUserWhenUsernameExistsShouldThrowException() {
        UserRegisterDTO registerDTO = new UserRegisterDTO(
                testUsername, "password", "Test", "password"
        );

        when(userRepository.findByUsername(testUsername)).thenReturn(Optional.of(testUser));

        assertThrows(AppObjectAlreadyExistsException.class, () -> userService.registerUser(registerDTO));
        verify(userRepository, never()).save(any());
    }

    @Test
    void testRegisterUserWhenUsernameNotExistsShouldRegisterUser() throws AppObjectAlreadyExistsException {
        UserRegisterDTO registerDTO = new UserRegisterDTO(
                testUsername, testFirstName, testLastName, testPassword
        );
        testUser.setVerificationToken(new VerificationToken());

        when(userRepository.findByUsername(testUsername)).thenReturn(Optional.empty());
        when(mapper.mapToUser(registerDTO)).thenReturn(testUser);
        when(userRepository.save(testUser)).thenReturn(testUser);
        when(mapper.mapToUserReadOnlyDTO(testUser)).thenReturn(testUserReadOnlyDTO);

        UserReadOnlyDTO result = userService.registerUser(registerDTO);

        assertNotNull(result);
        assertEquals(testUuid, result.uuid());
        verify(userRepository, times(1)).findByUsername(testUsername);
        verify(userRepository, times(1)).save(testUser);
        assertNotNull(testUser.getVerificationToken());
    }

    @Test
    void testInsertVerifiedUserWhenUsernameExistsShouldThrowException() {
        UserRegisterDTO registerDTO = new UserRegisterDTO(
                testUsername, "password", "Test", "password"
        );

        when(userRepository.findByUsername(testUsername)).thenReturn(Optional.of(testUser));

        assertThrows(AppObjectAlreadyExistsException.class, () -> userService.insertVerifiedUser(registerDTO));
        verify(userRepository, never()).save(any());
    }

    @Test
    void testInsertVerifiedUserWhenUsernameNotExistsShouldRegisterUser() throws AppObjectAlreadyExistsException {
        UserRegisterDTO registerDTO = new UserRegisterDTO(
                testUsername, testFirstName, testLastName, testPassword
        );

        when(userRepository.findByUsername(testUsername)).thenReturn(Optional.empty());
        when(mapper.mapToUser(registerDTO)).thenReturn(testUser);
        when(userRepository.save(testUser)).thenReturn(testUser);
        when(mapper.mapToUserReadOnlyDTO(testUser)).thenReturn(testUserReadOnlyDTO);

        UserReadOnlyDTO result = userService.insertVerifiedUser(registerDTO);

        assertNotNull(result);
        assertEquals(testUuid, result.uuid());
        assertTrue(testUser.getVerified());
        assertTrue(testUser.getEnabled());
        verify(userRepository, times(1)).findByUsername(testUsername);
        verify(userRepository, times(1)).save(testUser);
    }

    @Test
    void testGetVerificationTokenWhenUserNotExistsShouldThrowException() {
        when(userRepository.findByUsername(testUsername)).thenReturn(Optional.empty());

        assertThrows(AppObjectNotFoundException.class, () -> userService.getVerificationToken(testUsername));
    }

    @Test
    void testGetVerificationTokenWhenTokenNotExistsShouldThrowException() {
        testUser.setVerificationToken(null);
        when(userRepository.findByUsername(testUsername)).thenReturn(Optional.of(testUser));

        assertThrows(AppObjectNotFoundException.class, () -> userService.getVerificationToken(testUsername));
    }

    @Test
    void testGetVerificationTokenWhenUserAndTokenExistShouldReturnToken() throws AppObjectNotFoundException {
        VerificationToken token = new VerificationToken(testUser);
        testUser.setVerificationToken(token);

        when(userRepository.findByUsername(testUsername)).thenReturn(Optional.of(testUser));

        VerificationToken result = userService.getVerificationToken(testUsername);

        assertNotNull(result);
        assertEquals(token, result);
    }

    @Test
    void testDeleteUserWhenUserNotFoundShouldThrowException() {
        when(userRepository.findByUsername(testUsername)).thenReturn(Optional.empty());

        assertThrows(AppObjectNotFoundException.class, () -> userService.deleteUser(testUsername));
    }

    @Test
    void testDeleteUserWhenUserExistsShouldDeleteUser() throws AppObjectNotFoundException {
        when(userRepository.findByUsername(testUsername)).thenReturn(Optional.of(testUser));
        userService.deleteUser(testUsername);

        verify(userRepository, times(1)).delete(testUser);
    }

    @Test
    void testUpdateUserPasswordAfterSuccessfulRecoveryShouldUpdatePasswordAndClearToken() {
        String newPassword = "newPassword";
        UserUpdateDTO updateDTO = new UserUpdateDTO(newPassword);
        User updatedUser = User.builder().password(newPassword).build();

        when(mapper.mapToUser(updateDTO, testUser)).thenReturn(updatedUser);
        when(userRepository.save(updatedUser)).thenReturn(updatedUser);

        userService.updateUserPasswordAfterSuccessfulRecovery(testUser, newPassword);

        verify(userRepository, times(1)).save(updatedUser);
        assertNull(updatedUser.getPasswordResetToken());
        assertEquals(newPassword, updatedUser.getPassword());
    }

    @Test
    void testUpdateUserAfterSuccessfulVerificationShouldVerifyAndClearToken() {
        VerificationToken token = new VerificationToken(testUser);
        testUser.setEnabled(false);
        testUser.setVerified(false);
        testUser.setVerificationToken(token);

        when(userRepository.save(testUser)).thenReturn(testUser);

        userService.updateUserAfterSuccessfulVerification(testUser);

        assertTrue(testUser.getVerified());
        assertTrue(testUser.getEnabled());
        assertNull(testUser.getVerificationToken());
        verify(userRepository, times(1)).save(testUser);
    }


    @Test
    void testUpdateUserByUUIDWithUpdateDTOWhenUserExistsShouldUpdateUser() throws AppObjectNotFoundException, AppObjectAlreadyExistsException {
        String newUsername = "newusername";
        UserUpdateDTO updateDTO = new UserUpdateDTO(newUsername, "firstname", "lastname", "password", true, true, false);
        User updatedUser = User.builder()
                .id(testId)
                .uuid(testUuid)
                .username(newUsername)
                .firstname("firstname")
                .lastname("lastname")
                .password("password")
                .enabled(true)
                .verified(true)
                .isDeleted(false)
                .build();
        UserReadOnlyDTO updatedDTO = new UserReadOnlyDTO(
                testId, testUuid, newUsername,"firstname", "lastname", true, true, false, 0);

        when(userRepository.findByUuid(testUuid)).thenReturn(Optional.of(testUser));
        when(userRepository.findByUsername(newUsername)).thenReturn(Optional.empty());
        when(mapper.mapToUser(updateDTO, testUser)).thenReturn(updatedUser);
        when(userRepository.save(updatedUser)).thenReturn(updatedUser);
        when(mapper.mapToUserReadOnlyDTO(updatedUser)).thenReturn(updatedDTO);

        UserReadOnlyDTO result = userService.updateUserByUUID(testUuid, updateDTO);

        assertNotNull(result);
        assertEquals(newUsername, result.username());
        assertEquals(testId, result.id());
        assertEquals(testUuid, result.uuid());
        assertEquals("firstname", result.firstname());
        assertEquals("lastname", result.lastname());
        verify(userRepository, times(1)).save(updatedUser);
    }

    @Test
    void testUpdateUserByUuidWithUpdateDTOWhenUserNotExistsShouldThrowException() {
        when(userRepository.findByUuid(testUuid)).thenReturn(Optional.empty());

        assertThrows(AppObjectNotFoundException.class, () -> userService.updateUserByUUID(testUuid, new UserUpdateDTO("username", "firstname", "lastname", "password", true, true, false)));
    }

    @Test
    void testUpdateUserByUuidWithUpdateDTOWhenUserExistsAndNewUsernameExistsShouldThrowException() {
        String newUsername = "existinguser";
        UserUpdateDTO updateDTO = new UserUpdateDTO(newUsername, "firstname", "lastname", "password", true, true, false);
        User existingUser = User.builder().uuid(UUID.randomUUID().toString()).build();

        when(userRepository.findByUuid(testUuid)).thenReturn(Optional.of(testUser));
        when(userRepository.findByUsername(newUsername)).thenReturn(Optional.of(existingUser));

        assertThrows(AppObjectAlreadyExistsException.class,
                () -> userService.updateUserByUUID(testUuid, updateDTO));
    }


    @Test
    void testUpdateUserByUuidWithPatchDTOWhenUserNotFoundShouldThrowException() {
        when(userRepository.findByUuid(testUuid)).thenReturn(Optional.empty());
        assertThrows(AppObjectNotFoundException.class, () -> userService.updateUserByUUID(testUuid, new UserPatchDTO(true, true, true)));
    }

    @Test
    void testUpdateUserByUuidWithPatchDTOWhenUserExistsShouldUpdateUser() throws AppObjectNotFoundException {
        UserPatchDTO patchDTO = new UserPatchDTO(true, true, true);
        User updatedUser = User.builder()
                .id(testId)
                .uuid(testUuid)
                .username(testUsername)
                .firstname(testFirstName)
                .lastname(testLastName)
                .password(testPassword)
                .enabled(patchDTO.enabled())
                .verified(patchDTO.verified())
                .isDeleted(patchDTO.deleted())
                .build();
        UserReadOnlyDTO updatedDTO = new UserReadOnlyDTO(testId, testUuid, testUsername, testFirstName, testLastName, patchDTO.enabled(), patchDTO.verified(), patchDTO.deleted(), 0);

        when(userRepository.findByUuid(testUuid)).thenReturn(Optional.of(testUser));
        when(mapper.mapToUser(patchDTO, testUser)).thenReturn(updatedUser);
        when(userRepository.save(updatedUser)).thenReturn(updatedUser);
        when(mapper.mapToUserReadOnlyDTO(updatedUser)).thenReturn(updatedDTO);

        UserReadOnlyDTO result = userService.updateUserByUUID(testUuid, patchDTO);

        assertNotNull(result);
        assertTrue(result.enabled());
        assertTrue(result.verified());
        assertTrue(result.isDeleted());
        verify(userRepository, times(1)).findByUuid(testUuid);
        verify(userRepository, times(1)).save(updatedUser);
    }

    @Test
    void testChangeUserPasswordWhenUserNotFoundShouldThrowException() {
        when(userRepository.findByUuid(testUuid)).thenReturn(Optional.empty());

        assertThrows(AppObjectNotFoundException.class, () -> userService.changeUserPassword(testUuid, new ChangePasswordDTO("old", "new")));
    }

    @Test
    void testChangeUserPasswordWhenUserExistsAndInvalidOldPasswordShouldThrowException() {
        String oldPassword = "oldPassword";
        String newPassword = "newPassword";
        ChangePasswordDTO changeDTO = new ChangePasswordDTO(oldPassword, newPassword);

        when(userRepository.findByUuid(testUuid)).thenReturn(Optional.of(testUser));
        when(authenticationService.isPasswordValid(testUsername, oldPassword)).thenReturn(false);

        assertThrows(AppObjectNotAuthorizedException.class,
                () -> userService.changeUserPassword(testUuid, changeDTO));
        verify(userRepository, times(1)).findByUuid(testUuid);
        verify(authenticationService, times(1)).isPasswordValid(testUsername, oldPassword);
        verify(userRepository, never()).save(any());
    }

    @Test
    void testChangeUserPasswordWhenValidShouldUpdatePassword() throws AppObjectNotFoundException, AppObjectNotAuthorizedException {
        String oldPassword = "oldPassword";
        String newPassword = "newPassword";
        ChangePasswordDTO changeDTO = new ChangePasswordDTO(oldPassword, newPassword);
        UserUpdateDTO updateDTO = new UserUpdateDTO(newPassword);
        User updatedUser = User.builder().password(newPassword).build();

        when(userRepository.findByUuid(testUuid)).thenReturn(Optional.of(testUser));
        when(authenticationService.isPasswordValid(testUsername, oldPassword)).thenReturn(true);
        when(mapper.mapToUser(updateDTO, testUser)).thenReturn(updatedUser);
        when(userRepository.save(updatedUser)).thenReturn(updatedUser);

        userService.changeUserPassword(testUuid, changeDTO);

        verify(userRepository, times(1)).findByUuid(testUuid);
        verify(authenticationService, times(1)).isPasswordValid(testUsername, oldPassword);
        verify(userRepository, times(1)).save(updatedUser);
    }

    @Test
    void testDeleteUserByUuidWhenUserExistsShouldDeleteUser() throws AppObjectNotFoundException {
        when(userRepository.findByUuid(testUuid)).thenReturn(Optional.of(testUser));

        userService.deleteUserByUuid(testUuid);

        verify(userRepository, times(1)).findByUuid(testUuid);
        verify(userRepository, times(1)).delete(testUser);
    }

    @Test
    void testDeleteUserByUuidWhenUserNotExistsShouldThrowException() {
        when(userRepository.findByUuid(testUuid)).thenReturn(Optional.empty());

        assertThrows(AppObjectNotFoundException.class, () -> userService.deleteUserByUuid(testUuid));
        verify(userRepository, times(1)).findByUuid(testUuid);
    }
}