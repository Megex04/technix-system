package pe.com.lacunza.technix.api.controllers;

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
public class UserController {

    private final UserService userService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{username}")
    @PreAuthorize("hasRole('ADMIN') or @userSecurity.isCurrentUser(#id)")
    public ResponseEntity<?> getUserById(@PathVariable String username) {
        try {
            User user = userService.getUserByUsername(username);
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @userSecurity.isCurrentUser(#id)")
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
    public ResponseEntity<List<User>> getActiveUsers() {
        List<User> activeUsers = userService.getActiveUsers();
        return ResponseEntity.ok(activeUsers);
    }

    @PostMapping("/password-reset")
    public ResponseEntity<?> changePassword(@Valid @RequestBody PasswordResetDto passwordResetDto) {
        userService.changePassword(passwordResetDto.getEmail(), passwordResetDto.getCurrentPassword(), passwordResetDto.getNewPassword());
        return ResponseEntity.ok("Your password has reset successfully");
    }
}
