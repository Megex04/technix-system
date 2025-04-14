package pe.com.lacunza.technix.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class PermissionsUpdateDto {

    private List<String> addPermissions;

    private List<String> removePermissions;
}
