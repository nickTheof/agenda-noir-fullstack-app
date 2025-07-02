package gr.aueb.cf.projectmanagementapp.rest;

import gr.aueb.cf.projectmanagementapp.core.exceptions.*;
import gr.aueb.cf.projectmanagementapp.dto.*;
import gr.aueb.cf.projectmanagementapp.model.User;
import gr.aueb.cf.projectmanagementapp.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Tag(name = "Users")
@SecurityRequirement(name = "bearerAuth")
public class UserRestController {
    private final UserService userService;
    private static final Logger LOGGER = LoggerFactory.getLogger(UserRestController.class);

    @GetMapping
    @PreAuthorize("@authorizationService.hasAuthority(authentication.principal, 'READ_USER')")
    @Operation(
            summary = "Get all users",
            description = "Returns a list of all users in the system. Requires READ_USER authority."
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "List of users returned successfully",
                            content = @Content(array = @ArraySchema(schema = @Schema(implementation = UserReadOnlyDTO.class)))
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Token not found or expired. Authentication failed.",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ApiErrorDTO.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Forbidden access. Authenticated user has not permission to access the specific resources.",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ApiErrorDTO.class)
                            )
                    ),
            }
    )
    public ResponseEntity<List<UserReadOnlyDTO>> findAllUsers() {
        List<UserReadOnlyDTO> users = userService.findAllUsers();
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    // TODO POST /users/filtered -> retrieve all users filtered and paginated . Accessible by users with READ_USER permission

    @PostMapping
    @PreAuthorize("@authorizationService.hasAuthority(authentication.principal, 'CREATE_USER')")
    @Operation(
            summary = "Register a new user with enabled and verified status.",
            description = "Creates a new user. It can be accessed only from users with permission to perform CREATE action in User Resource. User is not forced to verify his own account",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    content = @Content(
                            schema = @Schema(
                                    implementation = UserRegisterDTO.class
                            )
                    )
            )
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "User created",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = UserReadOnlyDTO.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Validation errors for UserRegisterDTO.",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ApiErrorDTO.class))
                    ),
                    @ApiResponse(
                            responseCode = "409",
                            description = "User registration failed. User with the given username already exists",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ApiErrorDTO.class))
                    ),
            }
    )
    public ResponseEntity<UserReadOnlyDTO> insertUser(
            @Valid @RequestBody UserRegisterDTO dto,
            BindingResult bindingResult
    ) throws ValidationException, AppObjectAlreadyExistsException, AppServerException, AppObjectNotFoundException {
        if (bindingResult.hasErrors()) {
            LOGGER.warn("Validation errors for UserRegisterDTO: {}", bindingResult.getAllErrors());
            throw new ValidationException(bindingResult);
        }
        try {
            //Register the new user
            UserReadOnlyDTO user = userService.insertVerifiedUser(dto);
            LOGGER.info("User inserted successfully: {}", user);
            // Send the successful response
            return new ResponseEntity<>(user, HttpStatus.CREATED);
        } catch (AppObjectAlreadyExistsException e) {
            // User registration failed due to business logic constraints (e.g. user with username already exists)
            LOGGER.error("User registration failed. {}", e.getMessage(), e);
            throw e;
        }
    }


    @GetMapping("/me")
    @Operation(
            summary = "Get current authenticated user",
            description = "Retrieve information of the current authenticated user"
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successful retrieve information of the current authenticated user",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = UserReadOnlyDTO.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Token not found or expired. Authentication failed.",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ApiErrorDTO.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "User not found.",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ApiErrorDTO.class))
                    ),
            }
    )
    public ResponseEntity<UserReadOnlyDTO> getCurrentUser(
            @AuthenticationPrincipal User user
            ) throws AppObjectNotFoundException {
        try {
            UserReadOnlyDTO dto = userService.findUserByUuid(user.getUuid());
            return new ResponseEntity<>(dto, HttpStatus.OK);
        } catch (AppObjectNotFoundException e) {
            LOGGER.error("Getting user failed. {}", e.getMessage(), e);
            throw e;
        }
    }

    @PutMapping("/me")
    @Operation(
            summary = "Update current authenticated user",
            description = "Update information (such as firstname, lastname, etc.) of the current authenticated user.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = UserUpdateDTO.class)
                    )
            )
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "User updated successfully",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = UserReadOnlyDTO.class))
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Validation errors for UserUpdateDTO",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ApiErrorDTO.class))
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Token not found or expired. Authentication failed.",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ApiErrorDTO.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "User not found to update.",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ApiErrorDTO.class))
                    ),
                    @ApiResponse(
                            responseCode = "409",
                            description = "The updated username corresponds to an already registered user.",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ApiErrorDTO.class))
                    )
            }
    )
    public ResponseEntity<UserReadOnlyDTO> updateCurrentUser(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody UserUpdateDTO dto,
            BindingResult bindingResult
            ) throws ValidationException, AppObjectNotFoundException, AppObjectAlreadyExistsException {
        if (bindingResult.hasErrors()) {
            LOGGER.warn("Validation errors for UserUpdateDTO: {}", bindingResult.getAllErrors());
            throw new ValidationException(bindingResult);
        }
        try {
            UserReadOnlyDTO updatedUser = userService.updateUserByUUID(user.getUuid(), dto);
            LOGGER.info("Updated user successfully: {}", updatedUser);
            return new ResponseEntity<>(updatedUser, HttpStatus.OK);
        } catch (AppObjectNotFoundException | AppObjectAlreadyExistsException e) {
            LOGGER.error("Updating user failed. {}", e.getMessage(), e);
            throw e;
        }
    }

    @PatchMapping("me/change-password")
    @Operation(
            summary = "Change password of current authenticated user",
            description = "An authenticated user changes his password by inserting old and new credentials",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ChangePasswordDTO.class)
                    )
            ),
            responses = {
                    @ApiResponse(
                            description = "The password changed successfully.",
                            responseCode = "204"
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Token not found or expired. Authentication failed.",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ApiErrorDTO.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "User not found.",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ApiErrorDTO.class))
                    ),

            }
    )
    public ResponseEntity<Void> changePassword(
            @Valid @RequestBody ChangePasswordDTO dto,
            BindingResult bindingResult,
            @AuthenticationPrincipal User user
    ) throws ValidationException, AppObjectNotFoundException, AppObjectNotAuthorizedException {
        if (bindingResult.hasErrors()) {
            throw new ValidationException(bindingResult);
        }
        try {
            userService.changeUserPassword(user.getUuid(), dto);
            return ResponseEntity.noContent().build();
        } catch (AppObjectNotFoundException | AppObjectNotAuthorizedException e) {
            LOGGER.error("Changing password failed. {}", e.getMessage(), e);
            throw e;
        }
    }

    @GetMapping("/{uuid}")
    @PreAuthorize("@authorizationService.hasOwnership(authentication.principal, #uuid) || @authorizationService.hasAuthority(authentication.principal, 'READ_USER')")
    @Operation(
            summary = "Retrieve user details by UUID",
            description = "Returns the user details if the authenticated user has permission to access the resource. "
                    + "Users can access their own data or need READ_USER permission to access others' data.",
            parameters = {
                    @Parameter(
                            name = "uuid",
                            description = "The unique identifier of the user to retrieve",
                            required = true,
                            example = "baba3f82-7b0f-4440-9893-a2f76169802c",
                            in = ParameterIn.PATH,
                            schema = @Schema(type = "string", format = "uuid")
                    )
            }
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successful retrieve information of the user with the provided uuid",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = UserReadOnlyDTO.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Token not found or expired. Authentication failed.",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ApiErrorDTO.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Forbidden access. Authenticated user has not permission to access the specific resource.",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ApiErrorDTO.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "User not found.",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ApiErrorDTO.class))
                    ),
            }
    )
    public ResponseEntity<UserReadOnlyDTO> getUserByUuid(
            @PathVariable("uuid") String uuid
    ) throws AppObjectNotFoundException {
        try {
            UserReadOnlyDTO dto = userService.findUserByUuid(uuid);
            return new ResponseEntity<>(dto, HttpStatus.OK);
        } catch (Exception e) {
            LOGGER.error("Getting user failed. {}", e.getMessage(), e);
            throw e;
        }
    }

    @PutMapping("/{uuid}")
    @PreAuthorize("@authorizationService.hasAuthority(authentication.principal, 'UPDATE_USER')")
    @Operation(
            summary = "Update a user with specific uuid.",
            description = "Updates a user find by its unique UUID.  That endpoint can be accessed only from users with permission to perform UPDATE action in the User Resource",
            parameters = {
                    @Parameter(
                            name = "uuid",
                            description = "The unique identifier of the user to update",
                            required = true,
                            example = "baba3f82-7b0f-4440-9893-a2f76169802c",
                            in = ParameterIn.PATH,
                            schema = @Schema(type = "string", format = "uuid")
                    )
            },
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = UserUpdateDTO.class)
                    )
            )
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successful update of the user with the provided uuid",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = UserReadOnlyDTO.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Validation errors for UserUpdateDTO",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ApiErrorDTO.class))
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Token not found or expired. Authentication failed.",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ApiErrorDTO.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Forbidden access. Authenticated user has not permission to update the specific resource.",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ApiErrorDTO.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "User not found.",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ApiErrorDTO.class))
                    ),
            }
    )
    public ResponseEntity<UserReadOnlyDTO> updateUserByUuid(
            @PathVariable("uuid") String uuid,
            @Valid @RequestBody UserUpdateDTO updateDTO,
            BindingResult bindingResult
    ) throws AppObjectNotFoundException, ValidationException, AppObjectAlreadyExistsException {
        if (bindingResult.hasErrors()) {
            LOGGER.warn("Validation errors for UserUpdateDTO in user with uuid {}: {}", uuid, bindingResult.getAllErrors());
            throw new ValidationException(bindingResult);
        }
        try {
            UserReadOnlyDTO readOnlyDTO = userService.updateUserByUUID(uuid, updateDTO);
            LOGGER.info("Updated user with uuid {} successfully: {}", uuid, readOnlyDTO);
            return new ResponseEntity<>(readOnlyDTO, HttpStatus.OK);
        } catch (Exception e) {
            LOGGER.error("Updating user failed. {}", e.getMessage(), e);
            throw e;
        }
    }


    @PatchMapping("/{uuid}")
    @PreAuthorize("@authorizationService.hasAuthority(authentication.principal, 'UPDATE_USER')")
    @Operation(
            summary = "Partially update user fields by UUID",
            description = "Updates one or more user flags such as enabled, verified, or deleted. Requires UPDATE_USER authority.",
            parameters = {
                    @Parameter(
                            name = "uuid",
                            description = "The unique identifier of the user to partially update",
                            required = true,
                            example = "baba3f82-7b0f-4440-9893-a2f76169802c",
                            in = ParameterIn.PATH,
                            schema = @Schema(type = "string", format = "uuid")
                    )
            },
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = UserPatchDTO.class)
                    )
            )
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successful partial update of the user with the provided uuid",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = UserReadOnlyDTO.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Validation errors for UserPatchDTO",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ApiErrorDTO.class))
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Token not found or expired. Authentication failed.",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ApiErrorDTO.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Forbidden access. Authenticated user has not permission to update the specific resource.",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ApiErrorDTO.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "User not found.",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ApiErrorDTO.class))
                    ),
            }
    )
    public ResponseEntity<UserReadOnlyDTO> partialUpdateUserByUuid(
            @PathVariable("uuid") String uuid,
            @Valid @RequestBody UserPatchDTO patchDTO,
            BindingResult bindingResult
    ) throws AppObjectNotFoundException, ValidationException {
        if (bindingResult.hasErrors()) {
            LOGGER.warn("Validation errors for UserPatchDTO : {}", bindingResult.getAllErrors());
            throw new ValidationException(bindingResult);
        }
        try {
            UserReadOnlyDTO readOnlyDTO = userService.updateUserByUUID(uuid, patchDTO);
            LOGGER.info("Partially updated user with uuid {} successfully: {}", uuid, readOnlyDTO);
            return new ResponseEntity<>(readOnlyDTO, HttpStatus.OK);
        } catch (Exception e) {
            LOGGER.error("Updating user failed. {}", e.getMessage(), e);
            throw e;
        }
    }

    @DeleteMapping("/{uuid}")
    @PreAuthorize("@authorizationService.hasAuthority(authentication.principal, 'DELETE_USER')")
    @Operation(
            summary = "Delete a user with specific uuid.",
            description = "Deletes a user by its unique UUID. That's an endpoint for a hard delete. It can be accessed only from users with permission to perform DELETE action in User Resource",
            parameters = {
                    @Parameter(
                            name = "uuid",
                            description = "The unique identifier of the user to delete",
                            required = true,
                            example = "baba3f82-7b0f-4440-9893-a2f76169802c",
                            in = ParameterIn.PATH,
                            schema = @Schema(type = "string", format = "uuid")
                    )
            }
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            description = "The user deleted successfully.",
                            responseCode = "204"
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Token not found or expired. Authentication failed.",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ApiErrorDTO.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Forbidden access. Authenticated user has not permission to access the specific resource.",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ApiErrorDTO.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "User not found.",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ApiErrorDTO.class))
                    ),
            }
    )
    public ResponseEntity<Void> deleteUserByUuid(
            @PathVariable("uuid") String uuid
    ) throws AppObjectNotFoundException {
        try {
            userService.deleteUserByUuid(uuid);
            LOGGER.info("Deleted user successfully {}", uuid);
            return ResponseEntity.noContent().build();
        }  catch (AppObjectNotFoundException e) {
        LOGGER.warn("User not found: {}", uuid);
        throw e;
         }
    }
}
