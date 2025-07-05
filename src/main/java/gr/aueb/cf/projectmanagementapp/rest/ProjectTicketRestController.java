package gr.aueb.cf.projectmanagementapp.rest;

import gr.aueb.cf.projectmanagementapp.core.exceptions.AppObjectNotFoundException;
import gr.aueb.cf.projectmanagementapp.core.exceptions.ValidationException;
import gr.aueb.cf.projectmanagementapp.dto.*;
import gr.aueb.cf.projectmanagementapp.service.UserProjectTicketService;
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
@RequestMapping("/api/v1/users/{userUuid}/projects/{projectUuid}/tickets")
@RequiredArgsConstructor
@Tag(name =  "User Project Tickets")
@SecurityRequirement(name = "bearerAuth")
public class ProjectTicketRestController {
    private final UserProjectTicketService userProjectTicketService;
    private static final Logger LOGGER = LoggerFactory.getLogger(ProjectTicketRestController.class);


    @GetMapping
    @PreAuthorize("@authorizationService.hasOwnership(authentication.principal, #userUuid) || @authorizationService.hasAuthority(authentication.principal, 'READ_TICKET')")
    @Operation(
            summary = "Get all tickets for a specified project of a user",
            description = "Retrieves all tickets belonging to the specified project of the user",
            parameters = {
                    @Parameter(
                            name = "userUuid",
                            description = "The unique identifier of the user to retrieve all the tickets of the specified project",
                            required = true,
                            example = "baba3f82-7b0f-4440-9893-a2f76169802c",
                            in = ParameterIn.PATH,
                            schema = @Schema(type = "string", format = "uuid")
                    ),
                    @Parameter(
                            name = "projectUuid",
                            description = "The unique identifier of the project to retrieve all its tickets",
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
                            description = "List of tickets returned successfully",
                            content = @Content(array = @ArraySchema(schema = @Schema(implementation = TicketReadOnlyDTO.class)))
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
            }
    )
    public ResponseEntity<List<TicketReadOnlyDTO>> getUserProjectTickets(
            @PathVariable("userUuid") String userUuid,
            @PathVariable("projectUuid") String projectUuid) throws AppObjectNotFoundException {
        try {
            List<TicketReadOnlyDTO> tickets = userProjectTicketService.getProjectTickets(userUuid, projectUuid);
            return new ResponseEntity<>(tickets, HttpStatus.OK);
        } catch (AppObjectNotFoundException e) {
            LOGGER.error("Error getting project tickets", e);
            throw e;
        }
    }

    @PostMapping
    @PreAuthorize("@authorizationService.hasOwnership(authentication.principal, #userUuid) || @authorizationService.hasAuthority(authentication.principal, 'CREATE_TICKET')")
    @Operation(
            summary = "Create a new ticket for a user for a specified project",
            description = "Creates a new ticket associated with the specified project of a specified user",
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
                            description = "The unique identifier of the project to insert a new ticket",
                            required = true,
                            example = "baba3f82-7b0f-4440-9893-a2f76169802c",
                            in = ParameterIn.PATH,
                            schema = @Schema(type = "string", format = "uuid")
                    )
            },
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = TicketCreateDTO.class)
                    )
            )
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Ticket created successfully",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = TicketReadOnlyDTO.class)
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
                            description = "User or Project not found.",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ApiErrorDTO.class))
                    ),
            }
    )
    public ResponseEntity<TicketReadOnlyDTO> createUserProjectTicket(
            @PathVariable("userUuid") String userUuid,
            @PathVariable("projectUuid") String projectUuid,
            @Valid @RequestBody TicketCreateDTO ticketCreateDTO,
            BindingResult bindingResult
    ) throws ValidationException, AppObjectNotFoundException {
        if (bindingResult.hasErrors()) {
            LOGGER.warn("Validation errors in creating ticket: {}", bindingResult.getAllErrors());
            throw new ValidationException(bindingResult);
        }
        try {
            TicketReadOnlyDTO createdTicket = userProjectTicketService.createProjectTicket(userUuid, projectUuid, ticketCreateDTO);
            return new ResponseEntity<>(createdTicket, HttpStatus.CREATED);
        } catch (AppObjectNotFoundException e) {
            LOGGER.error("Error creating project ticket", e);
            throw e;
        }
    }

    @GetMapping("/{ticketUuid}")
    @PreAuthorize("@authorizationService.hasOwnership(authentication.principal, #userUuid) || @authorizationService.hasAuthority(authentication.principal, 'READ_TICKET')")
    @Operation(
            summary = "Get a specific ticket by UUID",
            description = "Retrieves a specific ticket belonging to a specified project of a specified user",
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
                    ),
                    @Parameter(
                            name = "ticketUuid",
                            description = "The unique identifier of the ticket. This ticket belongs to the project with the specified projectUuid which is associated with a specified user with user with userUuid.",
                            required = true,
                            example = "baba3f82-7b0f-4440-9893-a2f76169802c",
                            in = ParameterIn.PATH,
                            schema = @Schema(type = "string", format = "uuid")
                    ),

            }
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Ticket retrieved successfully",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = TicketReadOnlyDTO.class)
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
                            description = "User or Project or Ticket not found.",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ApiErrorDTO.class))
                    ),
            }
    )
    public ResponseEntity<TicketReadOnlyDTO> getUserProjectTicketByUuid(
            @PathVariable("userUuid") String userUuid,
            @PathVariable("projectUuid") String projectUuid,
            @PathVariable("ticketUuid") String ticketUuid
    ) throws AppObjectNotFoundException {
        try {
            TicketReadOnlyDTO ticket = userProjectTicketService.getProjectTicketByUuid(userUuid, projectUuid, ticketUuid);
            return new ResponseEntity<>(ticket, HttpStatus.OK);
        } catch (AppObjectNotFoundException e) {
            LOGGER.error("Error getting project ticket with uuid {}", ticketUuid, e);
            throw e;
        }
    }

    @PutMapping("/{ticketUuid}")
    @PreAuthorize("@authorizationService.hasOwnership(authentication.principal, #userUuid) || @authorizationService.hasAuthority(authentication.principal, 'UPDATE_TICKET')")
    @Operation(
            summary = "Update a ticket (full update)",
            description = "Performs a full update of the specified ticket",
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
                    ),
                    @Parameter(
                            name = "ticketUuid",
                            description = "The unique identifier of the ticket. This ticket belongs to the project with the specified projectUuid which is associated with a specified user with user with userUuid.",
                            required = true,
                            example = "baba3f82-7b0f-4440-9893-a2f76169802c",
                            in = ParameterIn.PATH,
                            schema = @Schema(type = "string", format = "uuid")
                    ),
            },
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = TicketUpdateDTO.class)
                    )
            )
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Ticket updated successfully",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = TicketReadOnlyDTO.class)
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
                            description = "User or Project or Ticket with the specified UUIDs not found.",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ApiErrorDTO.class))
                    ),
            }
    )
    public ResponseEntity<TicketReadOnlyDTO> updateUserProjectTicket(
            @PathVariable("userUuid") String userUuid,
            @PathVariable("projectUuid") String projectUuid,
            @PathVariable("ticketUuid") String ticketUuid,
            @Valid @RequestBody TicketUpdateDTO updateDTO,
            BindingResult bindingResult
            ) throws ValidationException, AppObjectNotFoundException {
        if (bindingResult.hasErrors()) {
            LOGGER.warn("Validation errors in updating ticket with uuid={}: {}", ticketUuid, bindingResult.getAllErrors());
            throw new ValidationException(bindingResult);
        }
        try {
            TicketReadOnlyDTO updatedTicket = userProjectTicketService.updateProjectTicket(userUuid, projectUuid, ticketUuid, updateDTO);
            return new ResponseEntity<>(updatedTicket, HttpStatus.OK);
        } catch (AppObjectNotFoundException e) {
            LOGGER.error("Error updating project ticket with uuid={} ", ticketUuid, e);
            throw e;
        }
    }

    @PatchMapping("/{ticketUuid}")
    @PreAuthorize("@authorizationService.hasOwnership(authentication.principal, #userUuid) || @authorizationService.hasAuthority(authentication.principal, 'UPDATE_TICKET')")
    @Operation(
            summary = "Update a ticket (partial update)",
            description = "Performs a partial update of the specified ticket",
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
                    ),
                    @Parameter(
                            name = "ticketUuid",
                            description = "The unique identifier of the ticket. This ticket belongs to the project with the specified projectUuid which is associated with a specified user with user with userUuid.",
                            required = true,
                            example = "baba3f82-7b0f-4440-9893-a2f76169802c",
                            in = ParameterIn.PATH,
                            schema = @Schema(type = "string", format = "uuid")
                    ),
            },
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = TicketPatchDTO.class)
                    )
            )
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Ticket updated successfully",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = TicketReadOnlyDTO.class)
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
                            description = "User or Project or Ticket with the specified UUIDs not found.",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ApiErrorDTO.class))
                    ),
            }
    )
    public ResponseEntity<TicketReadOnlyDTO> updateUserProjectTicket(
            @PathVariable("userUuid") String userUuid,
            @PathVariable("projectUuid") String projectUuid,
            @PathVariable("ticketUuid") String ticketUuid,
            @Valid @RequestBody TicketPatchDTO patchDTO,
            BindingResult bindingResult
    ) throws ValidationException, AppObjectNotFoundException {
        if (bindingResult.hasErrors()) {
            LOGGER.warn("Validation errors in partial updating ticket with uuid={}: {}", ticketUuid, bindingResult.getAllErrors());
            throw new ValidationException(bindingResult);
        }
        try {
            TicketReadOnlyDTO updatedTicket = userProjectTicketService.updateProjectTicket(userUuid, projectUuid, ticketUuid, patchDTO);
            return new ResponseEntity<>(updatedTicket, HttpStatus.OK);
        } catch (AppObjectNotFoundException e) {
            LOGGER.error("Error updating project ticket with uuid={} ", ticketUuid, e);
            throw e;
        }
    }

    @DeleteMapping("/{ticketUuid}")
    @PreAuthorize("@authorizationService.hasOwnership(authentication.principal, #userUuid) || @authorizationService.hasAuthority(authentication.principal, 'DELETE_TICKET')")
    @Operation(
            summary = "Delete a ticket",
            description = "Deletes the specified ticket",
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
                    ),
                    @Parameter(
                            name = "ticketUuid",
                            description = "The unique identifier of the ticket. This ticket belongs to the project with the specified projectUuid which is associated with a specified user with user with userUuid.",
                            required = true,
                            example = "baba3f82-7b0f-4440-9893-a2f76169802c",
                            in = ParameterIn.PATH,
                            schema = @Schema(type = "string", format = "uuid")
                    ),
            }
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            description = "The ticket with the specified uuid deleted successfully.",
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
                            description = "User or Project or Ticket not found.",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ApiErrorDTO.class))
                    )
            }
    )
    public ResponseEntity<Void> deleteUserProjectTicketByUuid(
            @PathVariable("userUuid") String userUuid,
            @PathVariable("projectUuid") String projectUuid,
            @PathVariable("ticketUuid") String ticketUuid
    ) throws AppObjectNotFoundException {
        try {
            userProjectTicketService.deleteProjectTicket(userUuid, projectUuid, ticketUuid);
            LOGGER.info("User project ticket with uuid {} deleted", ticketUuid);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (AppObjectNotFoundException e) {
            LOGGER.error("Error deleting project ticket with uuid {}", ticketUuid, e);
            throw e;
        }
    }
}
