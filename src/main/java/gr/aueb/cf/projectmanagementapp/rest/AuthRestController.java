package gr.aueb.cf.projectmanagementapp.rest;

import gr.aueb.cf.projectmanagementapp.authentication.AuthenticationService;
import gr.aueb.cf.projectmanagementapp.core.exceptions.*;
import gr.aueb.cf.projectmanagementapp.dto.*;
import gr.aueb.cf.projectmanagementapp.model.PasswordResetToken;
import gr.aueb.cf.projectmanagementapp.model.User;
import gr.aueb.cf.projectmanagementapp.model.VerificationToken;
import gr.aueb.cf.projectmanagementapp.service.EmailService;
import gr.aueb.cf.projectmanagementapp.service.IUserService;
import gr.aueb.cf.projectmanagementapp.service.PasswordResetTokenService;
import gr.aueb.cf.projectmanagementapp.service.VerificationTokenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication")
public class AuthRestController {
    private final IUserService userService;
    private final EmailService emailService;
    private final AuthenticationService authenticationService;
    private final PasswordResetTokenService passwordResetTokenService;
    private final VerificationTokenService verificationTokenService;
    private static final Logger LOGGER = LoggerFactory.getLogger(AuthRestController.class);


    @PostMapping("/register/open")
    @Operation(
            summary = "Register a new user from the OPEN registration endpoint.",
            description = "User registration endpoint. User created from this endpoint has not any extra authority. With the default configuration, he can access only his own resources.",
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
                            responseCode = "404",
                            description = "User or token not found.",
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
                    @ApiResponse(
                            responseCode = "500",
                            description = "User registration failed. Mail service failed to be executed.",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ApiErrorDTO.class))
                    ),
            }
    )
    public ResponseEntity<UserReadOnlyDTO> registerUser(
            @Valid @RequestBody UserRegisterDTO dto,
            BindingResult bindingResult
            ) throws ValidationException, AppObjectAlreadyExistsException, AppServerException, AppObjectNotFoundException {
        if (bindingResult.hasErrors()) {
            LOGGER.warn("Validation errors for user registration dto: {}", bindingResult.getAllErrors());
            throw new ValidationException(bindingResult);
        }
        try {
            //Register the new user
            UserReadOnlyDTO user = userService.registerUser(dto);
            LOGGER.info("User registered: {}", user);

            // Get the user verification token
            VerificationToken token = userService.getVerificationToken(user.username());
            LOGGER.info("Verification token for user with username={} generated: {}", user.username(), token);

            // Send the verification token via email
            emailService.sendVerificationEmail(user.username(), token.getToken());
            LOGGER.info("Verification email sent for user with username={}", user.username());
            // Send the successful response
            return new ResponseEntity<>(user, HttpStatus.CREATED);
        } catch (AppObjectAlreadyExistsException e) {
            // User registration failed due to business logic constraints (e.g. user with username already exists)
            LOGGER.error("User registration failed. {}", e.getMessage(), e);
            throw e;
        } catch (AppServerException | AppObjectNotFoundException e) {
            LOGGER.error("User registration failed. {}", e.getMessage(), e);
            // Rollback user registration. User registration failed due to email service failure
            // We delete directly the created user if the email service failed to be executed
            // in order to keep consistent state with the DB
            userService.deleteUser(dto.username());
            LOGGER.info("User deleted after email service error: {}", dto.username());
            throw e;
        }
    }

    @PostMapping("/login/access-token")
    @Operation(
            summary = "Authenticate a user. After successful authentication, it returns the signed JWT Bearer Token.",
            description = "Authentication endpoint that provides the verified Bearer JWT token.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = AuthenticationRequestDTO.class)
                    )
            )
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "User authenticated successfully",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = AuthenticationResponseDTO.class)
                            )
                    ),
                    @ApiResponse(
                    responseCode = "400",
                    description = "Validation errors for AuthenticationRequestDTO",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiErrorDTO.class))
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Invalid credentials. Authentication failed.",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ApiErrorDTO.class)
                            )
                    )
            }
    )
    public ResponseEntity<AuthenticationResponseDTO> loginUser(
            @Valid @RequestBody AuthenticationRequestDTO dto,
            BindingResult bindingResult
            ) throws ValidationException, AppObjectNotAuthorizedException {
        if (bindingResult.hasErrors()) {
            LOGGER.warn("Validation errors for AuthenticationRequestDTO: {}", bindingResult.getAllErrors());
            throw new ValidationException(bindingResult);
        }
        // Get the signed JWT token if the authentication service executed without errors
        AuthenticationResponseDTO authenticationResponseDTO = authenticationService.authenticate(dto);
        LOGGER.info("User authenticated: {}", authenticationResponseDTO);
        return new ResponseEntity<>(authenticationResponseDTO, HttpStatus.OK);
    }


    @PostMapping("/verify-account")
    @Operation(
            summary = "Verify user account after successful registration.",
            description = "Using token verification from email service, the new registered user activate its own account.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ActivationTokenDTO.class)
                    )
            )
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "User verify its own account successfully",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ApiMessageResponseDTO.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Validation errors for ActivationTokenDTO",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ApiErrorDTO.class))
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Verification failed. Invalid or expired token for verification account.",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ApiErrorDTO.class)
                            )
                    )
            }
    )
    public ResponseEntity<ApiMessageResponseDTO> verifyAccount(
            @Valid @RequestBody ActivationTokenDTO dto,
            BindingResult bindingResult
    ) throws ValidationException, AppObjectNotAuthorizedException {

        if (bindingResult.hasErrors()) {
            LOGGER.warn("Validation errors for token activation: {}", bindingResult.getAllErrors());
            throw new ValidationException(bindingResult);
        }
        try {
            // Fetch the user from verification token
            User user = verificationTokenService.getUserForValidToken(dto.token());
            // Verify user if the retrieve is successful and the token is valid
            userService.updateUserAfterSuccessfulVerification(user);
            // Send successful response
            return new ResponseEntity<>(new ApiMessageResponseDTO(HttpStatus.OK.value(), "Account has been verified successfully"), HttpStatus.OK);

        } catch (AppObjectNotFoundException e) {
            LOGGER.error("Invalid or expired token for verification account: {}", dto.token());
            throw new AppObjectNotAuthorizedException(
                    "Token",
                    "The verification reset link is invalid or has expired. Please Register again."
            );
        }
    }

    @PostMapping("/password-recovery/{username}")
    @Operation(
            summary = "Request password recovery for a user",
            description = "Generates a password reset token and sends a recovery link to the user's email address if the username exists.",
            parameters = {
                    @Parameter(
                            name = "username",
                            description = "The username of the user requesting a password reset in email format",
                            required = true,
                            example = "admin@test.com",
                            in = ParameterIn.PATH
                    )
            }
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Password reset link sent successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiMessageResponseDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Failed to send password reset email",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiErrorDTO.class)
                    )
            )
    })
    public ResponseEntity<ApiMessageResponseDTO> requestPasswordRecovery(
            @PathVariable("username") String username
    ) throws AppServerException {
        try {
            // Generate token and link it with the user if the username exists
            PasswordResetToken token = passwordResetTokenService.generateTokenForUser(username);
            LOGGER.info("Password reset token: {}", token);

            // Send the reset password token via email
            emailService.sendPasswordResetEmail(username, token.getToken());
            LOGGER.info("Password reset email sent to user: {}", username);

            // Send successful response
            return new ResponseEntity<>(
                    new ApiMessageResponseDTO(HttpStatus.OK.value(), "Password reset link has been sent to your email"),
                    HttpStatus.OK
            );
        } catch (AppServerException e) {
            LOGGER.error("Failed to send password reset email to: {}", username, e);
            throw new AppServerException(
                    "EmailSendFailed",
                    "Failed to send password reset email. Please try again later."
            );
        }
    }


    @PostMapping("/reset-password")
    @Operation(
            summary = "Reset user password using a valid recovery token",
            description = "This endpoint resets the user's password if a valid password recovery token is provided.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ResetPasswordAfterRecoveryDTO.class)
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Password reset successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiMessageResponseDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Validation errors for ResetPasswordAfterRecoveryDTO",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiErrorDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Token is invalid or has expired",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiErrorDTO.class)
                    )
            )
    })
    public ResponseEntity<ApiMessageResponseDTO> resetPassword(
            @Valid @RequestBody ResetPasswordAfterRecoveryDTO dto,
            BindingResult bindingResult
    ) throws ValidationException, AppObjectNotAuthorizedException {

        if (bindingResult.hasErrors()) {
            LOGGER.warn("Validation errors for ResetPasswordAfterRecoveryDTO: {}", bindingResult.getAllErrors());
            throw new ValidationException(bindingResult);
        }
        try {
            // Fetch the user from password reset token
            User user = passwordResetTokenService.getUserForValidToken(dto.token());
            // Update user's password if the retrieve of the token is successful and the token is valid
            userService.updateUserPasswordAfterSuccessfulRecovery(user, dto.newPassword());

            // Send successful response
            return new ResponseEntity<>(
                    new ApiMessageResponseDTO(HttpStatus.OK.value(), "Password has been reset successfully"),
                    HttpStatus.OK
            );
        } catch (AppObjectNotFoundException e) {
            LOGGER.error("Invalid or expired token for resetting the password: {}", dto.token());
            throw new AppObjectNotAuthorizedException(
                    "Token",
                    "The password reset link is invalid or has expired. Please request a new one."
            );
        }
    }


}
