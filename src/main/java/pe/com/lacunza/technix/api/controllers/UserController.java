package pe.com.lacunza.technix.api.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pe.com.lacunza.technix.domain.entities.documents.User;
import pe.com.lacunza.technix.dtos.PasswordResetDto;
import pe.com.lacunza.technix.dtos.PermissionsUpdateDto;
import pe.com.lacunza.technix.dtos.RoleUpdateDto;
import pe.com.lacunza.technix.dtos.UserUpdateDto;
import pe.com.lacunza.technix.services.UserService;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@AllArgsConstructor
@Tag(name = "User management endpoints controller")
@SecurityRequirement(name = "bearerAuth")
public class UserController {

    private final UserService userService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Obtains all list users")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/here")
    @PreAuthorize("hasRole('ADMIN') or @userSecurity.isCurrentUsername(#email)")
    @Operation(summary = "Obtain a user by email / get your user in same session user")
    public ResponseEntity<?> getUserById(@RequestParam String email) {
        try {
            User user = userService.getUserByEmail(email);
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @userSecurity.isCurrentUser(#id)")
    @Operation(summary = "Update user by new data / update your user in same session user")
    public ResponseEntity<?> updateUser(@PathVariable String id, @Valid @RequestBody UserUpdateDto userUpdateDto) {
        try {
            User updatedUser = userService.updateUser(id, userUpdateDto);
            return ResponseEntity.ok(updatedUser);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Update failed: " + e.getMessage());
        }
    }

    @PatchMapping("/{id}/role")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Change role from user id")
    public ResponseEntity<?> updateUserRole(@PathVariable String id, @Valid @RequestBody RoleUpdateDto roleUpdateDto) {
        try {
            User updatedUser = userService.updateUserRole(id, roleUpdateDto.getRole());
            return ResponseEntity.ok(updatedUser);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Role update failed: " + e.getMessage());
        }
    }

    @PatchMapping("/{id}/permissions")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update permissions from user id")
    public ResponseEntity<?> updateUserPermissions(
            @PathVariable String id,
            @Valid @RequestBody PermissionsUpdateDto permissionsUpdateDto) {
        try {
            User updatedUser = userService.updateUserPermissions(
                    id,
                    permissionsUpdateDto.getAddPermissions(),
                    permissionsUpdateDto.getRemovePermissions()
            );
            return ResponseEntity.ok(updatedUser);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Permissions update failed: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete user by id")
    public ResponseEntity<?> deleteUser(@PathVariable String id) {
        try {
            userService.deleteUser(id);
            return ResponseEntity.ok("User deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Delete failed: " + e.getMessage());
        }
    }

    @GetMapping("/active")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "List all active users")
    public ResponseEntity<List<User>> getActiveUsers() {
        List<User> activeUsers = userService.getActiveUsers();
        return ResponseEntity.ok(activeUsers);
    }
}
