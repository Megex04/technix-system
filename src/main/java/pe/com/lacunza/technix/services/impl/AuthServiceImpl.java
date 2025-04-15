package pe.com.lacunza.technix.services.impl;

import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import pe.com.lacunza.technix.api.models.response.AuthResponse;
import pe.com.lacunza.technix.config.jwt.JwtTokenProvider;
import pe.com.lacunza.technix.domain.entities.documents.RefreshToken;
import pe.com.lacunza.technix.domain.entities.documents.User;
import pe.com.lacunza.technix.domain.repositories.mongo.UserRepository;
import pe.com.lacunza.technix.services.AuthService;

import java.time.LocalDate;

@Service
@AllArgsConstructor
public class AuthServiceImpl implements AuthService {

    private AuthenticationManager authenticationManager;
    private JwtTokenProvider tokenProvider;
    private UserRepository userRepository;

    @Override
    public AuthResponse authenticate(String usernameOrEmail, String password) {
        try {
            // Autenticar contra Spring Security
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(usernameOrEmail, password)
            );

            // Establecer la autenticación en el contexto
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Obtener usuario autenticado
            User user = userRepository.findByUsernameOrEmail(usernameOrEmail, usernameOrEmail)
                    .orElseThrow(() -> new BadCredentialsException("Usuario no encontrado"));

            // Generar token de acceso
            String accessToken = tokenProvider.generateToken(authentication);

            // Generar token de refresco
            String refreshToken = tokenProvider.generateRefreshToken(user.getId());

            // Guardar el token de refresco en la base de datos
            saveRefreshToken(user, refreshToken);

            // Actualizar la fecha de último login
            user.setLastLogin(LocalDate.now());
            userRepository.save(user);

            // Crear y devolver la respuesta de autenticación
            return AuthResponse.builder()
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .tokenType("Bearer")
                    .id(user.getId())
                    .username(user.getUsername())
                    .email(user.getEmail())
                    .firstName(user.getFirstName())
                    .lastName(user.getLastName())
                    .role(user.getRole())
                    .permissions(user.getPermissions())
                    .build();

        } catch (BadCredentialsException e) {
            throw new BadCredentialsException("Credenciales inválidas");
        } catch (Exception e) {
            throw new RuntimeException("Error durante la autenticación: " + e.getMessage());
        }
    }

    /**
     * Guarda o actualiza el token de refresco para un usuario
     */
    private void saveRefreshToken(User user, String refreshToken) {
        // Crear un nuevo token de refresco o actualizar si ya existe
        RefreshToken tokenEntity = new RefreshToken();
        tokenEntity.setToken(refreshToken);
        tokenEntity.setExpiryDate(LocalDate.now().plusDays(1)); // Validez de 1 día

        // Si el usuario ya tiene tokens de refresco, añadir este a la lista
        if (user.getRefreshTokens() == null || user.getRefreshTokens().isEmpty()) {
            user.setRefreshTokens(java.util.Collections.singletonList(tokenEntity));
        } else {
            user.getRefreshTokens().add(tokenEntity);
        }

        userRepository.save(user);
    }

    @Override
    public AuthResponse refreshToken(String refreshToken) {
        // Validar el token de refresco
        if (!tokenProvider.validateToken(refreshToken)) {
            throw new RuntimeException("Token de refresco inválido");
        }

        // Extraer ID de usuario del token
        String userId = tokenProvider.getUserIdFromJWT(refreshToken);

        // Buscar usuario y verificar si tiene este token en su lista
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Verificar si el token existe y no ha expirado
        boolean tokenExists = user.getRefreshTokens().stream()
                .anyMatch(token -> token.getToken().equals(refreshToken) &&
                        token.getExpiryDate().isAfter(LocalDate.now()));

        if (!tokenExists) {
            throw new RuntimeException("Token de refresco expirado o inválido");
        }

        // Generar nuevo token de acceso
        String newAccessToken = tokenProvider.generateTokenFromUserId(userId);

        // Crear y devolver respuesta
        return AuthResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(refreshToken) // Mantenemos el mismo token de refresco
                .tokenType("Bearer")
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .role(user.getRole())
                .permissions(user.getPermissions())
                .build();
    }

    @Override
    public void logout(String jwtFromUser) {
        String userEmail = tokenProvider.getUserIdFromJWT(jwtFromUser);

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Limpiar todos los tokens de refresco
        if (user.getRefreshTokens() != null) {
            user.setRefreshTokens(null);
            userRepository.save(user);
        }
    }
}
