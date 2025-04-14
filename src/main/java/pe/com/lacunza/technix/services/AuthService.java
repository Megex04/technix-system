package pe.com.lacunza.technix.services;

import pe.com.lacunza.technix.api.models.response.AuthResponse;

public interface AuthService {
    AuthResponse authenticate(String usernameOrEmail, String password);

    AuthResponse refreshToken(String refreshToken);

    void logout(String userId);
}
