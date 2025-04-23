package pe.com.lacunza.technix.api.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.com.lacunza.technix.api.models.request.AuthRequest;
import pe.com.lacunza.technix.api.models.request.RefreshTokenRequest;
import pe.com.lacunza.technix.api.models.response.AuthResponse;
import pe.com.lacunza.technix.domain.entities.documents.User;
import pe.com.lacunza.technix.dtos.PasswordResetDto;
import pe.com.lacunza.technix.dtos.UserRegistrationDto;
import pe.com.lacunza.technix.services.AuthService;
import pe.com.lacunza.technix.services.UserService;

@RestController
@RequestMapping("/api/auth")
@AllArgsConstructor
@Tag(name = "Public authentication user endpoints")
public class UserAuthController {
    private final AuthService authService;
    private final UserService userService;

    @PostMapping("/register")
    @Operation(summary = "Register user in system")
    @ApiResponse(
            responseCode = "400",
            description = "When the request and his field contains a invalid data to register new user"
    )
    public ResponseEntity<?> registerUser(@Valid @RequestBody UserRegistrationDto registrationDto) {
        try {
            User createdUser = userService.registerUser(registrationDto);
            return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/login")
    @Operation(summary = "Log in user in system with credentials")
    @ApiResponse(
            responseCode = "400",
            description = "When the request and his field contains a invalid data to login user"
    )
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody AuthRequest authRequest) {
        try {
            AuthResponse authResponse = authService.authenticate(authRequest.getUsername(), authRequest.getPassword());
            return ResponseEntity.ok(authResponse);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authentication failed: " + e.getMessage());
        }
    }

    @PostMapping("/refresh-token")
    @Operation(summary = "Ingresated refreshToken to update its token")
    @ApiResponse(
            responseCode = "400",
            description = "When the request and his field contains a invalid data to refresh new token"
    )
    public ResponseEntity<?> refreshToken(@Valid @RequestBody RefreshTokenRequest refreshTokenRequest) {
        try {
            AuthResponse authResponse = authService.refreshToken(refreshTokenRequest.getRefreshToken());
            return ResponseEntity.ok(authResponse);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token refresh failed: " + e.getMessage());
        }
    }

    @PostMapping("/password-reset")
    @Operation(summary = "Change a user password")
    @ApiResponse(
            responseCode = "400",
            description = "When the request and his field contains a invalid data to change password"
    )
    public ResponseEntity<?> changePassword(@Valid @RequestBody PasswordResetDto passwordResetDto) {
        userService.changePassword(passwordResetDto.getEmail(), passwordResetDto.getCurrentPassword(), passwordResetDto.getNewPassword());
        return ResponseEntity.ok("Your password has reset successfully");
    }

    @PostMapping("/logout")
    @Operation(summary = "Log out user from system", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<?> logoutUser(@RequestHeader("Authorization") String token) {
        try {
            // Extract JWT token from the Authorization header
            String jwt = token.substring(7); // Remove "Bearer " prefix
            authService.logout(jwt);
            return ResponseEntity.ok("Logged out successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Logout failed: " + e.getMessage());
        }
    }
}
