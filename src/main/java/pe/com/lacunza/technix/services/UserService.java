package pe.com.lacunza.technix.services;

import pe.com.lacunza.technix.domain.entities.documents.User;
import pe.com.lacunza.technix.dtos.UserRegistrationDto;
import pe.com.lacunza.technix.dtos.UserUpdateDto;

import java.util.List;

public interface UserService {

    User registerUser(UserRegistrationDto registrationDto);

    List<User> getAllUsers();

    User getUserByUsername(String username);

    User updateUser(String id, UserUpdateDto userUpdateDto);

    User updateUserRole(String userId, String newRole);

    User updateUserPermissions(String userId, List<String> addPermissions, List<String> removePermissions);

    void deleteUser(String id);

    List<User> getActiveUsers();

    User changePassword(String email, String currentPassword, String newPassword);
}
