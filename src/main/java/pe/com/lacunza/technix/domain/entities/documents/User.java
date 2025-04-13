package pe.com.lacunza.technix.domain.entities.documents;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@Document(collection = "users")
public class User {
    @Id
    private String id;

    @Indexed(unique = true)
    private String username;

    @Indexed(unique = true)
    private String email;

    private String passwordHash;
    private String firstName;
    private String lastName;
    private String role; // "ADMIN", "EMPLOYEE", "SUPERVISOR"
    private List<String> permissions;
    private boolean active;
    private LocalDate lastLogin;
    private LocalDate createdAt;
    private LocalDate updatedAt;
    private List<RefreshToken> refreshTokens;
}
