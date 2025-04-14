package pe.com.lacunza.technix.domain.entities.documents;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@Document(collection = "refresh_tokens")
public class RefreshToken {
    private String token;
    private LocalDate expiryDate;
}
