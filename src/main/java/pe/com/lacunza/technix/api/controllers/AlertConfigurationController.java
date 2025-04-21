package pe.com.lacunza.technix.api.controllers;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pe.com.lacunza.technix.dtos.AlertConfigurationDto;
import pe.com.lacunza.technix.services.AlertConfigurationService;

import java.util.List;

@RestController
@RequestMapping("/api/alert")
@AllArgsConstructor
public class AlertConfigurationController {

    private AlertConfigurationService alertConfigurationService;

    @GetMapping
    public ResponseEntity<List<AlertConfigurationDto>> getAllAlertConfigurations() {
        List<AlertConfigurationDto> alertConfigurations = alertConfigurationService.getAllAlertConfigurations();
        return new ResponseEntity<>(alertConfigurations, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AlertConfigurationDto> getAlertConfigurationById(@PathVariable Long id) {
        AlertConfigurationDto alertConfiguration = alertConfigurationService.getAlertConfigurationById(id);
        return new ResponseEntity<>(alertConfiguration, HttpStatus.OK);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPERVISOR')")
    public ResponseEntity<AlertConfigurationDto> createAlertConfiguration(@Valid @RequestBody AlertConfigurationDto alertConfigurationDTO) {
        AlertConfigurationDto createdAlertConfiguration = alertConfigurationService.createAlertConfiguration(alertConfigurationDTO);
        return new ResponseEntity<>(createdAlertConfiguration, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPERVISOR')")
    public ResponseEntity<AlertConfigurationDto> updateAlertConfiguration(@PathVariable Long id, @Valid @RequestBody AlertConfigurationDto alertConfigurationDTO) {
        AlertConfigurationDto updatedAlertConfiguration = alertConfigurationService.updateAlertConfiguration(id, alertConfigurationDTO);
        return new ResponseEntity<>(updatedAlertConfiguration, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPERVISOR')")
    public ResponseEntity<Void> deleteAlertConfiguration(@PathVariable Long id) {
        alertConfigurationService.deleteAlertConfiguration(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
