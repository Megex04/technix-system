package pe.com.lacunza.technix.services.impl;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.com.lacunza.technix.domain.entities.documents.User;
import pe.com.lacunza.technix.domain.repositories.mongo.UserRepository;
import pe.com.lacunza.technix.dtos.UserRegistrationDto;
import pe.com.lacunza.technix.dtos.UserUpdateDto;
import pe.com.lacunza.technix.services.UserService;
import pe.com.lacunza.technix.util.InventaryConstants;
import pe.com.lacunza.technix.api.exception.ResourceNotFoundException;

import java.time.LocalDate;
import java.util.List;

@Transactional
@Service
@Slf4j
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;

    @Override
    public User registerUser(UserRegistrationDto registrationDto) {
        if (userRepository.existsByEmail(registrationDto.getEmail())) {
            throw new IllegalArgumentException("El correo ya está registrado.");
        }
        User user = new User();
        user.setUsername(registrationDto.getUsername());
        user.setPasswordHash(passwordEncoder.encode(registrationDto.getPassword()));
        user.setEmail(registrationDto.getEmail());
        user.setFirstName(registrationDto.getFirstName());
        user.setLastName(registrationDto.getLastName());
        if(registrationDto.getRole() != null && registrationDto.getRole().equals("ADMIN")) {
            user.setRole("ADMIN");
            user.setPermissions(InventaryConstants.newPermissionsAdmin);
        } else if(registrationDto.getRole() != null && registrationDto.getRole().equals("SUPERVISOR")) {
            user.setRole("SUPERVISOR");
            user.setPermissions(InventaryConstants.newPermissionsSupervisor);
        } else {
            user.setRole(registrationDto.getRole());
            user.setPermissions(InventaryConstants.newPermissionsEmployee);
        }
        user.setActive(true);
        user.setLastLogin(null);
        user.setCreatedAt(LocalDate.now());
        user.setUpdatedAt(null);
        user.setRefreshTokens(List.of());

        return userRepository.save(user);
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con username: " + username));
    }

    @Override
    public User updateUser(String id, UserUpdateDto userUpdateDto) {
        User user = getUserById(id);

        // Actualizar campos básicos si no son nulos
        if (userUpdateDto.getUsername() != null) {
            user.setUsername(userUpdateDto.getUsername());
        }

        if (userUpdateDto.getFirstName() != null) {
            user.setFirstName(userUpdateDto.getFirstName());
        }

        if (userUpdateDto.getLastName() != null) {
            user.setLastName(userUpdateDto.getLastName());
        }

        if (userUpdateDto.getEmail() != null && !userUpdateDto.getEmail().equals(user.getEmail())) {
            // Verificar que el nuevo email no esté en uso
            userRepository.findByEmail(userUpdateDto.getEmail())
                    .ifPresent(existingUser -> {
                        if (!existingUser.getId().equals(id)) {
                            throw new IllegalArgumentException("Email ya está en uso");
                        }
                    });
            user.setEmail(userUpdateDto.getEmail());
        }

        // Actualizar contraseña si se proporciona
        if (userUpdateDto.getPassword() != null && !userUpdateDto.getPassword().isEmpty()) {
            user.setPasswordHash(passwordEncoder.encode(userUpdateDto.getPassword()));
        }

        // Actualizar estado activo si se proporciona
        if (userUpdateDto.getActive() != null) {
            user.setActive(userUpdateDto.getActive());
        }

        // Actualizar fecha de modificación
        user.setUpdatedAt(LocalDate.now());

        // Guardar y devolver usuario actualizado
        return userRepository.save(user);
    }

    @Override
    public User updateUserRole(String userId, String newRole) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        user.setRole(newRole.toUpperCase());
        return userRepository.save(user);
    }

    @Override
    public User updateUserPermissions(String userId, List<String> addPermissions, List<String> removePermissions) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        List<String> currentPermissions = user.getPermissions();

        // Añadir nuevos permisos
        if (addPermissions != null && !addPermissions.isEmpty()) {
            for (String permissionName : addPermissions) {
                currentPermissions.add(permissionName.toUpperCase());
            }
        }

        // Eliminar permisos
        if (removePermissions != null && !removePermissions.isEmpty()) {
            for (String permissionName : removePermissions) {
                currentPermissions.remove(permissionName.toUpperCase());
            }
        }

        user.setPermissions(currentPermissions);
        return userRepository.save(user);
    }

    @Override
    public void deleteUser(String id) {
        User user = getUserById(id);
        userRepository.delete(user);
    }

    @Override
    public List<User> getActiveUsers() {
        return userRepository.findByActiveTrue();
    }

    @Override
    public User changePassword(String email, String currentPassword, String newPassword) {
        User user = getUserByEmail(email);

        // Verificar contraseña actual
        if (!passwordEncoder.matches(currentPassword, user.getPasswordHash())) {
            throw new AccessDeniedException("Contraseña actual incorrecta");
        }

        // Actualizar contraseña
        user.setPasswordHash(passwordEncoder.encode(newPassword));
        user.setUpdatedAt(LocalDate.now());

        return userRepository.save(user);
    }

    public User getUserById(String id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con id: " + id));
    }
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con id: " + email));
    }
}
