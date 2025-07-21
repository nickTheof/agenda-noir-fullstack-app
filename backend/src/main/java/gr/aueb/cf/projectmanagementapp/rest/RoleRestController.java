package gr.aueb.cf.projectmanagementapp.rest;

import gr.aueb.cf.projectmanagementapp.core.exceptions.*;
import gr.aueb.cf.projectmanagementapp.dto.ApiErrorDTO;
import gr.aueb.cf.projectmanagementapp.dto.RoleCreateDTO;
import gr.aueb.cf.projectmanagementapp.dto.RoleReadOnlyDTO;
import gr.aueb.cf.projectmanagementapp.dto.RoleUpdateDTO;
import gr.aueb.cf.projectmanagementapp.service.IRoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
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
@RequestMapping("/api/v1/roles")
@Tag(name = "Roles")
@SecurityRequirement(name = "bearerAuth")
@RequiredArgsConstructor
public class RoleRestController {

    private final IRoleService roleService;
    private static final Logger LOGGER = LoggerFactory.getLogger(RoleRestController.class);

    @GetMapping
    @PreAuthorize("@authorizationService.hasAuthority(authentication.principal, 'READ_ROLE')")
    @Operation(
            summary = "Get all roles",
            description = "Retrieve a list of all roles. The list of permission objects are provided to the response object. Requires READ_ROLE authority.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "List of roles returned successfully",
                            content = @Content(array = @ArraySchema(schema = @Schema(implementation = RoleReadOnlyDTO.class)))
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
    public ResponseEntity<List<RoleReadOnlyDTO>> getAllRoles() {
        List<RoleReadOnlyDTO> roles = roleService.findAllRoles();
        return new ResponseEntity<>(roles, HttpStatus.OK);
    }

    @PostMapping
    @PreAuthorize("@authorizationService.hasAuthority(authentication.principal, 'CREATE_ROLE')")
    @Operation(
            summary = "Create a new role",
            description = """
        Creates a new role with specified permissions.
        Requires CREATE_ROLE authority.
        Validations:
        - Role name must be unique
        - At least one permission required
        - All permissions must exist in system""",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = RoleCreateDTO.class)
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Role created successfully",
                            content = @Content(
                                    schema = @Schema(implementation = RoleReadOnlyDTO.class)
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
                            description = "Forbidden access. Authenticated user has not permission to access the specific resources.",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ApiErrorDTO.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "409",
                            description = "Role name already exists or Invalid permissions provided",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ApiErrorDTO.class)
                            )
                    ),
            }
    )
    public ResponseEntity<RoleReadOnlyDTO> createRole(
            @Valid @RequestBody RoleCreateDTO createDTO,
            BindingResult bindingResult) throws ValidationException, AppObjectInvalidArgumentException, AppObjectAlreadyExistsException {
        if (bindingResult.hasErrors()) {
            LOGGER.warn("Validation error in RoleCreateDTO: {}", bindingResult.getAllErrors());
            throw new ValidationException(bindingResult);
        }
        try {
            RoleReadOnlyDTO createdRole = roleService.createRole(createDTO);
            LOGGER.info("Role created successfully: {}", createdRole);
            return new ResponseEntity<>(createdRole, HttpStatus.CREATED);
        } catch (AppObjectAlreadyExistsException | AppObjectInvalidArgumentException e) {
            LOGGER.warn("Create role procedure failed: {}", e.getMessage(), e);
            throw e;
        }
    }

    @GetMapping("/{id}")
    @PreAuthorize("@authorizationService.hasAuthority(authentication.principal, 'READ_ROLE')")
    @Operation(
            summary = "Get role by ID",
            description = "Retrieve a single role with full permission details. Requires READ_ROLE permission.",
            parameters = {
                    @Parameter(
                            name = "id",
                            in = ParameterIn.PATH,
                            required = true,
                            schema = @Schema(type = "integer", example = "1"),
                            description = "Role ID"
                    )
            },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Role retrieved successfully",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = RoleReadOnlyDTO.class)
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
                            description = "Forbidden access. Authenticated user has not permission to access the specific resources.",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ApiErrorDTO.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Role not found",
                            content = @Content(
                                    schema = @Schema(implementation = ApiErrorDTO.class)
                            )
                    )
            }
    )
    public ResponseEntity<RoleReadOnlyDTO> getRole(
            @PathVariable("id") Long id
    ) throws AppObjectNotFoundException {
        try {
            RoleReadOnlyDTO role = roleService.findRoleById(id);
            return new ResponseEntity<>(role, HttpStatus.OK);
        } catch (AppObjectNotFoundException e) {
            LOGGER.error("Role with id {} not found", id);
            throw e;
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("@authorizationService.hasAuthority(authentication.principal, 'UPDATE_ROLE')")
    @Operation(
            summary = "Update a role by ID",
            description = "Update a role retrieved by its unique id. Requires UPDATE_ROLE permission.",
            parameters = {
                    @Parameter(
                            name = "id",
                            in = ParameterIn.PATH,
                            required = true,
                            schema = @Schema(type = "integer", example = "1"),
                            description = "Role ID"
                    )
            },
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = RoleUpdateDTO.class)
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Role updated successfully",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = RoleReadOnlyDTO.class)
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
                            description = "Forbidden access. Authenticated user has not permission to access the specific resources.",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ApiErrorDTO.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Role not found",
                            content = @Content(
                                    schema = @Schema(implementation = ApiErrorDTO.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "409",
                            description = "Role name already exists or Invalid permissions provided",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ApiErrorDTO.class)
                            )
                    ),
            }
    )
    public ResponseEntity<RoleReadOnlyDTO> updateRole(
            @PathVariable("id") Long id,
            @Valid @RequestBody RoleUpdateDTO updateDTO,
            BindingResult bindingResult
            ) throws AppObjectNotFoundException, AppObjectAlreadyExistsException, AppObjectInvalidArgumentException, ValidationException {
        if (bindingResult.hasErrors()) {
            LOGGER.warn("Validation error in RoleUpdateDTO: {}", bindingResult.getAllErrors());
            throw new ValidationException(bindingResult);
        }
        try {
            RoleReadOnlyDTO updatedRole = roleService.updateRole(id, updateDTO);
            LOGGER.info("Update role successfully: {}", updatedRole);
            return new ResponseEntity<>(updatedRole, HttpStatus.OK);
        } catch (AppObjectAlreadyExistsException | AppObjectInvalidArgumentException | AppObjectNotFoundException e) {
            LOGGER.warn("AppObjectAlreadyExistsException: {}", e.getMessage(), e);
            throw e;
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("@authorizationService.hasAuthority(authentication.principal, 'DELETE_ROLE')")
    @Operation(
            summary = "Delete a role by ID",
            description = "Deletes a role if it exists and is not referenced by other entities. Requires a user with DELETE_ROLE permission.",
            parameters = {
                    @Parameter(
                            name = "id",
                            in = ParameterIn.PATH,
                            required = true,
                            schema = @Schema(type = "integer", example = "1"),
                            description = "Role ID"
                    )
            },
            responses = {
                    @ApiResponse(
                            description = "The role with the specified id deleted successfully.",
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
                            description = "Role not found.",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ApiErrorDTO.class))
                    ),
                    @ApiResponse(
                            responseCode = "409",
                            description = "Cannot delete role due to existing references (foreign key constraint)",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ApiErrorDTO.class)
                            )
                    ),
            }
    )
    public ResponseEntity<Void> deleteRole(@PathVariable("id") Long id) throws AppObjectNotFoundException, AppObjectDeletionConflictException {
        try {
            roleService.deleteRole(id);
            LOGGER.info("Role successfully deleted: {}", id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (AppObjectNotFoundException | AppObjectDeletionConflictException e) {
            LOGGER.warn("Delete role procedure failed: {}", e.getMessage(), e);
            throw e;
        }
    }
 }
