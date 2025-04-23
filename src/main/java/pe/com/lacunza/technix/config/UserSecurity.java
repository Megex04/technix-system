package pe.com.lacunza.technix.config;

import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import pe.com.lacunza.technix.domain.entities.documents.User;
import pe.com.lacunza.technix.domain.repositories.mongo.UserRepository;

@Component("userSecurity")
@AllArgsConstructor
public class UserSecurity {

    private UserRepository userRepository;

    // Para verificar por ID del usuario
    public boolean isCurrentUser(String userId) {
        String currentUsername = getCurrentUsername();
        if (currentUsername == null) {
            return false;
        }

        // Buscar el usuario por su username para comparar su id
        User user = userRepository.findByEmail(currentUsername).orElse(null);
        return user != null && user.getId().equals(userId);
    }

    // Para verificar por username directamente
    public boolean isCurrentUsername(String username) {
        String currentUsername = getCurrentUsername();
        return currentUsername != null && currentUsername.equals(username);
    }

    // Método auxiliar para obtener el username actual
    private String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }

        if (authentication.getPrincipal() instanceof org.springframework.security.core.userdetails.UserDetails) {
            return ((UserDetails) authentication.getPrincipal()).getUsername();
        }

        return authentication.getName();
    }
}
