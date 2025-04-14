package pe.com.lacunza.technix.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class RoleUpdateDto {

    @NotBlank(message = "Role cannot be empty")
    private String role;
}
