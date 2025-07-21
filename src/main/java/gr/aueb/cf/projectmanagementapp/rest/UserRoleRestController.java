package gr.aueb.cf.projectmanagementapp.rest;

import gr.aueb.cf.projectmanagementapp.core.exceptions.AppObjectNotFoundException;
import gr.aueb.cf.projectmanagementapp.dto.ApiErrorDTO;
import gr.aueb.cf.projectmanagementapp.dto.RoleReadOnlyDTO;
import gr.aueb.cf.projectmanagementapp.dto.UserRoleInsertDTO;
import gr.aueb.cf.projectmanagementapp.service.UserRoleService;
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
@RequestMapping("/api/v1/users/{uuid}/roles")
@Tag(name = "User Roles")
@SecurityRequirement(name = "bearerAuth")
@RequiredArgsConstructor
public class UserRoleRestController {
    private final UserRoleService userRoleService;
    private static final Logger LOGGER = LoggerFactory.getLogger(UserRoleRestController.class);

    @GetMapping
    @PreAuthorize("@authorizationService.hasAuthority(authentication.principal, 'READ_USER') || @authorizationService.hasOwnership(authentication.principal, #uuid)")
    @Operation(
            summary = "Get roles for a specific user",
            description = "Retrieves all roles assigned to the user identified by the given UUID.",
            parameters = {
                    @Parameter(
                            name = "uuid",
                            description = "The unique identifier of the user to retrieve all its roles",
                            required = true,
                            example = "baba3f82-7b0f-4440-9893-a2f76169802c",
                            in = ParameterIn.PATH,
                            schema = @Schema(type = "string", format = "uuid")
                    )
            },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "List of users roles returned successfully",
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
    public ResponseEntity<List<RoleReadOnlyDTO>> getUserRoles(
            @PathVariable String uuid
    ) throws AppObjectNotFoundException {
        try {
            List<RoleReadOnlyDTO> userRoles = userRoleService.findAllUserRoles(uuid);
            return new ResponseEntity<>(userRoles, HttpStatus.OK);
        } catch (AppObjectNotFoundException e) {
            LOGGER.warn("User not found trying fetched user roles: {}", uuid);
            throw e;
        }
    }

    @PatchMapping
    @PreAuthorize("@authorizationService.hasAuthority(authentication.principal, 'UPDATE_ROLE')")
    @Operation(
            summary = "Update roles of a specific user",
            description = "Update the roles assigned to the user identified by the given UUID.",
            parameters = {
                    @Parameter(
                            name = "uuid",
                            description = "The unique identifier of the user to update all its roles",
                            required = true,
                            example = "baba3f82-7b0f-4440-9893-a2f76169802c",
                            in = ParameterIn.PATH,
                            schema = @Schema(type = "string", format = "uuid")
                    )
            },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "List of updated users roles returned successfully",
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
                            description = "Forbidden access. Authenticated user has not permission to access the specific resource.",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ApiErrorDTO.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "User or Role not found.",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ApiErrorDTO.class))
                    ),
            }
    )
    public ResponseEntity<List<RoleReadOnlyDTO>> updateUserRoles(
            @PathVariable String uuid,
            @Valid @RequestBody UserRoleInsertDTO roleDTO,
            BindingResult bindingResult
    ) throws AppObjectNotFoundException {
        try {
            if (bindingResult.hasErrors()) {
                LOGGER.warn("Validation errors in updating user roles: {}", bindingResult.getAllErrors());
            }
            List<RoleReadOnlyDTO> userRoles = userRoleService.changeUserRoles(uuid, roleDTO);
            return new ResponseEntity<>(userRoles, HttpStatus.OK);
        } catch (AppObjectNotFoundException e) {
            LOGGER.warn("User not found trying update user roles: {}", uuid);
            throw e;
        }
    }
}
