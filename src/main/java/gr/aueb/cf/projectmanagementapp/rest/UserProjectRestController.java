package gr.aueb.cf.projectmanagementapp.rest;

import gr.aueb.cf.projectmanagementapp.core.exceptions.AppObjectDeletionConflictException;
import gr.aueb.cf.projectmanagementapp.core.exceptions.AppObjectNotFoundException;
import gr.aueb.cf.projectmanagementapp.core.exceptions.ValidationException;
import gr.aueb.cf.projectmanagementapp.dto.*;
import gr.aueb.cf.projectmanagementapp.service.UserProjectService;
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
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users/{userUuid}/projects")
@Tag(name = "User Projects")
@SecurityRequirement(name = "bearerAuth")
@RequiredArgsConstructor
public class UserProjectRestController {

    private final UserProjectService userProjectService;
    private static final Logger LOGGER = LoggerFactory.getLogger(UserProjectRestController.class);

    @GetMapping
    @PreAuthorize("@authorizationService.hasAuthority(authentication.principal, 'READ_PROJECT') || @ authorizationService.hasOwnership(authentication.principal, #userUuid)")
    @Operation(
            summary = "Get all projects for a user",
            description = "Retrieves all projects belonging to the specified user",
            parameters = {
                    @Parameter(
                            name = "userUuid",
                            description = "The unique identifier of the user to retrieve all its projects",
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
                            description = "List of users returned successfully",
                            content = @Content(array = @ArraySchema(schema = @Schema(implementation = ProjectReadOnlyDTO.class)))
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
    public ResponseEntity<List<ProjectReadOnlyDTO>> getUserProjects(
            @PathVariable("userUuid") String userUuid) throws AppObjectNotFoundException {
        try {
            List<ProjectReadOnlyDTO> projectReadOnlyDTOS = userProjectService.findAllUserProjects(userUuid);
            return new ResponseEntity<>(projectReadOnlyDTOS, HttpStatus.OK);
        } catch (AppObjectNotFoundException e) {
            LOGGER.error("Retrieving user projects of user with uuid={} failed", userUuid, e);
            throw e;
        }
    }

    @PostMapping
    @PreAuthorize("@authorizationService.hasAuthority(authentication.principal, 'CREATE_PROJECT') || @authorizationService.hasOwnership(authentication.principal, #userUuid)")
    @Operation(
            summary = "Create a new project for a user",
            description = "Creates a new project associated with the specified user",
            parameters = {
                    @Parameter(
                            name = "userUuid",
                            description = "The unique identifier of the user. This user is the owner of the project.",
                            required = true,
                            example = "baba3f82-7b0f-4440-9893-a2f76169802c",
                            in = ParameterIn.PATH,
                            schema = @Schema(type = "string", format = "uuid")
                    )
            },
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ProjectCreateDTO.class)
                    )
            )
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Project created successfully",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ProjectReadOnlyDTO.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Validation error",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ApiErrorDTO.class)
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
    public ResponseEntity<ProjectReadOnlyDTO> createUserProject(
            @PathVariable("userUuid") String userUuid,
            @Valid @RequestBody ProjectCreateDTO createDTO,
            BindingResult bindingResult
    ) throws ValidationException, AppObjectNotFoundException {
        if (bindingResult.hasErrors()) {
            LOGGER.warn("Validation error in ProjectCreateDTO: {}", bindingResult.getAllErrors());
            throw new ValidationException(bindingResult);
        }
        try {
            ProjectReadOnlyDTO readOnlyDTO = userProjectService.createUserProject(userUuid, createDTO);
            LOGGER.info("Created user project: {}", readOnlyDTO);
            return new ResponseEntity<>(readOnlyDTO, HttpStatus.CREATED);
        } catch (AppObjectNotFoundException e) {
            LOGGER.error("Creating user project failed", e);
            throw e;
        }
    }


    @GetMapping("/{projectUuid}")
    @PreAuthorize("@authorizationService.hasAuthority(authentication.principal, 'READ_PROJECT') || @authorizationService.hasOwnership(authentication.principal, #userUuid)")
    @Operation(
            summary = "Get a specific project by UUID",
            description = "Retrieves a specific project belonging to a user",
            parameters = {
                    @Parameter(
                            name = "userUuid",
                            description = "The unique identifier of the user. This user is the owner of the project.",
                            required = true,
                            example = "baba3f82-7b0f-4440-9893-a2f76169802c",
                            in = ParameterIn.PATH,
                            schema = @Schema(type = "string", format = "uuid")
                    ),
                    @Parameter(
                            name = "projectUuid",
                            description = "The unique identifier of the project. This projects belongs to the user with the specified userUuid.",
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
                            description = "Project retrieved successfully",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ProjectReadOnlyDTO.class)
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
                            description = "User or Project with the specified UUID not found.",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ApiErrorDTO.class))
                    ),
            }
    )
    public ResponseEntity<ProjectReadOnlyDTO> getUserProjectByUuid(
            @PathVariable("userUuid") String userUuid,
            @PathVariable("projectUuid") String projectUuid
    ) throws AppObjectNotFoundException {
        try {
            ProjectReadOnlyDTO projectReadOnlyDTO = userProjectService.findUserProjectByUuid(userUuid, projectUuid);
            return new ResponseEntity<>(projectReadOnlyDTO, HttpStatus.OK);
        } catch (AppObjectNotFoundException e) {
            LOGGER.error("Retrieving user project of user with uuid={} failed", userUuid, e);
            throw e;
        }
    }

    @PutMapping("/{projectUuid}")
    @PreAuthorize("@authorizationService.hasAuthority(authentication.principal, 'UPDATE_PROJECT') || @authorizationService.hasOwnership(authentication.principal, #userUuid)")
    @Operation(
            summary = "Update a project (full update)",
            description = "Performs a full update of the specified project",
            parameters = {
                    @Parameter(
                            name = "userUuid",
                            description = "The unique identifier of the user. This user is the owner of the project.",
                            required = true,
                            example = "baba3f82-7b0f-4440-9893-a2f76169802c",
                            in = ParameterIn.PATH,
                            schema = @Schema(type = "string", format = "uuid")
                    ),
                    @Parameter(
                            name = "projectUuid",
                            description = "The unique identifier of the project. This projects belongs to the user with the specified userUuid.",
                            required = true,
                            example = "baba3f82-7b0f-4440-9893-a2f76169802c",
                            in = ParameterIn.PATH,
                            schema = @Schema(type = "string", format = "uuid")
                    )
            },
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ProjectUpdateDTO.class)
                    )
            )
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Project updated successfully",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ProjectReadOnlyDTO.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Validation error",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ApiErrorDTO.class)
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
                            description = "User or Project with the specified UUID not found.",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ApiErrorDTO.class))
                    ),
            }
    )
    public ResponseEntity<ProjectReadOnlyDTO> updateUserProject(
            @PathVariable("userUuid") String userUuid,
            @PathVariable("projectUuid") String projectUuid,
            @Valid @RequestBody ProjectUpdateDTO updateDTO,
            BindingResult bindingResult
    ) throws ValidationException, AppObjectNotFoundException {
        if (bindingResult.hasErrors()) {
            LOGGER.warn("Validation error in ProjectUpdateDTO: {}", bindingResult.getAllErrors());
            throw new ValidationException(bindingResult);
        }
        try {
            ProjectReadOnlyDTO readOnlyDTO = userProjectService.updateUserProject(userUuid, projectUuid, updateDTO);
            LOGGER.info("Update user project: {}", readOnlyDTO);
            return new ResponseEntity<>(readOnlyDTO, HttpStatus.CREATED);
        } catch (AppObjectNotFoundException e) {
            LOGGER.error("Updating user project failed", e);
            throw e;
        }
    }

    @PatchMapping("/{projectUuid}")
    @PreAuthorize("@authorizationService.hasAuthority(authentication.principal, 'UPDATE_PROJECT') || @authorizationService.hasOwnership(authentication.principal, #userUuid)")
    @Operation(
            summary = "Update a project (partial update)",
            description = "Performs a partial update of the specified project",
            parameters = {
                    @Parameter(
                            name = "userUuid",
                            description = "The unique identifier of the user. This user is the owner of the project.",
                            required = true,
                            example = "baba3f82-7b0f-4440-9893-a2f76169802c",
                            in = ParameterIn.PATH,
                            schema = @Schema(type = "string", format = "uuid")
                    ),
                    @Parameter(
                            name = "projectUuid",
                            description = "The unique identifier of the project. This projects belongs to the user with the specified userUuid.",
                            required = true,
                            example = "baba3f82-7b0f-4440-9893-a2f76169802c",
                            in = ParameterIn.PATH,
                            schema = @Schema(type = "string", format = "uuid")
                    )
            },
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ProjectPatchDTO.class)
                    )
            )
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Project updated successfully",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ProjectReadOnlyDTO.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Validation error",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ApiErrorDTO.class)
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
                            description = "User or Project with the specified UUID not found.",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ApiErrorDTO.class))
                    ),
            }
    )
    public ResponseEntity<ProjectReadOnlyDTO> updateUserProject(
            @PathVariable("userUuid") String userUuid,
            @PathVariable("projectUuid") String projectUuid,
            @Valid @RequestBody ProjectPatchDTO patchDTO,
            BindingResult bindingResult
    ) throws ValidationException, AppObjectNotFoundException {
        if (bindingResult.hasErrors()) {
            LOGGER.warn("Validation error in ProjectPatchDTO: {}", bindingResult.getAllErrors());
            throw new ValidationException(bindingResult);
        }
        try {
            ProjectReadOnlyDTO readOnlyDTO = userProjectService.updateUserProject(userUuid, projectUuid, patchDTO);
            LOGGER.info("Partial update user project successfully: {}", readOnlyDTO);
            return new ResponseEntity<>(readOnlyDTO, HttpStatus.OK);
        } catch (AppObjectNotFoundException e) {
            LOGGER.error("Updating user project failed", e);
            throw e;
        }
    }

    @DeleteMapping("/{projectUuid}")
    @PreAuthorize("@authorizationService.hasAuthority(authentication.principal, 'DELETE_PROJECT') || @authorizationService.hasOwnership(authentication.principal, #userUuid)")
    @Operation(
            summary = "Delete a project",
            description = "Deletes the specified project if it contains no tickets",
            parameters = {
                    @Parameter(
                            name = "userUuid",
                            description = "The unique identifier of the user. This user is the owner of the project.",
                            required = true,
                            example = "baba3f82-7b0f-4440-9893-a2f76169802c",
                            in = ParameterIn.PATH,
                            schema = @Schema(type = "string", format = "uuid")
                    ),
                    @Parameter(
                            name = "projectUuid",
                            description = "The unique identifier of the project. This projects belongs to the user with the specified userUuid.",
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
                            description = "The project with the specified uuid deleted successfully.",
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
                            description = "User or Project not found.",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ApiErrorDTO.class))
                    ),
                    @ApiResponse(
                            responseCode = "409",
                            description = "Cannot delete project due to existing references (foreign key constraint)",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ApiErrorDTO.class)
                            )
                    ),
            }
    )
    public ResponseEntity<Void> deleteUserProjectByUuid(
            @PathVariable("userUuid") String userUuid,
            @PathVariable("projectUuid") String projectUuid
    ) throws AppObjectNotFoundException, AppObjectDeletionConflictException {
        try {
            userProjectService.deleteUserProject(userUuid, projectUuid);
            LOGGER.info("Deleted project with uuid: {} from user with uuid: {}", projectUuid, userUuid);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (AppObjectNotFoundException | AppObjectDeletionConflictException e) {
            LOGGER.error("Deleting user project of user with uuid={} and projectUuid={} failed", userUuid, projectUuid, e);
            throw e;
        }
    }

}
