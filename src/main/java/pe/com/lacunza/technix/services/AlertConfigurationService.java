package pe.com.lacunza.technix.services;

import pe.com.lacunza.technix.dtos.AlertConfigurationDto;

import java.util.List;

public interface AlertConfigurationService {
    List<AlertConfigurationDto> getAllAlertConfigurations();

    AlertConfigurationDto getAlertConfigurationById(Long id);

    AlertConfigurationDto createAlertConfiguration(AlertConfigurationDto alertConfigurationDTO);

    AlertConfigurationDto updateAlertConfiguration(Long id, AlertConfigurationDto alertConfigurationDTO);

    void deleteAlertConfiguration(Long id);
}
