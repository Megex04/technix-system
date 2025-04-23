package pe.com.lacunza.technix.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class SupplierDto {
    private Long id;

    @NotBlank(message = "El nombre del proveedor es obligatorio")
    private String name;

    private String contactPerson;
    private String phone;

    @Email(message = "El formato del email no es válido")
    private String email;

    private String address;
}
