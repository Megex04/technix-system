package pe.com.lacunza.technix.services.impl;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.com.lacunza.technix.api.exception.IllegalStateAlertException;
import pe.com.lacunza.technix.api.exception.ResourceNotFoundException;
import pe.com.lacunza.technix.domain.entities.jpa.AlertConfiguration;
import pe.com.lacunza.technix.domain.entities.jpa.Product;
import pe.com.lacunza.technix.domain.repositories.jpa.AlertConfigurationRepository;
import pe.com.lacunza.technix.domain.repositories.jpa.ProductRepository;
import pe.com.lacunza.technix.dtos.AlertConfigurationDto;
import pe.com.lacunza.technix.services.AlertConfigurationService;

import java.util.List;
import java.util.stream.Collectors;

@Transactional
@Service
@Slf4j
@AllArgsConstructor
public class AlertConfigurationServiceImpl implements AlertConfigurationService {

    private AlertConfigurationRepository alertConfigurationRepository;
    private ProductRepository productRepository;

    @Override
    public List<AlertConfigurationDto> getAllAlertConfigurations() {
        return alertConfigurationRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public AlertConfigurationDto getAlertConfigurationById(Long id) {
        AlertConfiguration alertConfiguration = alertConfigurationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Configuración de alerta no encontrada con id: " + id));
        return convertToDTO(alertConfiguration);
    }

    @Override
    public AlertConfigurationDto createAlertConfiguration(AlertConfigurationDto alertConfigurationDTO) {
        Product product = productRepository.findById(alertConfigurationDTO.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con id: " + alertConfigurationDTO.getProductId()));

        // Verificar si ya existe una configuración para este producto
        alertConfigurationRepository.findByProductId(product.getId())
                .ifPresent(existingConfig -> {
                    throw new IllegalStateAlertException("Ya existe una configuración de alerta para el producto con id: " + product.getId());
                });

        AlertConfiguration alertConfiguration = new AlertConfiguration();
        alertConfiguration.setProduct(product);
        alertConfiguration.setMinimumStock(alertConfigurationDTO.getMinimumStock());

        AlertConfiguration savedAlertConfiguration = alertConfigurationRepository.save(alertConfiguration);
        return convertToDTO(savedAlertConfiguration);
    }

    @Override
    public AlertConfigurationDto updateAlertConfiguration(Long id, AlertConfigurationDto alertConfigurationDTO) {
        AlertConfiguration existingAlertConfiguration = alertConfigurationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Configuración de alerta no encontrada con id: " + id));

        if (!existingAlertConfiguration.getProduct().getId().equals(alertConfigurationDTO.getProductId())) {
            Product newProduct = productRepository.findById(alertConfigurationDTO.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con id: " + alertConfigurationDTO.getProductId()));

            // Verificar si ya existe otra configuración para el nuevo producto
            alertConfigurationRepository.findByProductId(newProduct.getId())
                    .ifPresent(otherConfig -> {
                        if (!otherConfig.getId().equals(id)) {
                            throw new IllegalStateAlertException("Ya existe una configuración de alerta para el producto con id: " + newProduct.getId());
                        }
                    });

            existingAlertConfiguration.setProduct(newProduct);
        }

        existingAlertConfiguration.setMinimumStock(alertConfigurationDTO.getMinimumStock());

        AlertConfiguration updatedAlertConfiguration = alertConfigurationRepository.save(existingAlertConfiguration);
        return convertToDTO(updatedAlertConfiguration);
    }

    @Override
    public void deleteAlertConfiguration(Long id) {
        AlertConfiguration alertConfiguration = alertConfigurationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Configuración de alerta no encontrada con id: " + id));
        alertConfigurationRepository.delete(alertConfiguration);
    }

    private AlertConfigurationDto convertToDTO(AlertConfiguration alertConfiguration) {
        AlertConfigurationDto dto = new AlertConfigurationDto();
        dto.setId(alertConfiguration.getId());
        dto.setProductId(alertConfiguration.getProduct().getId());
        dto.setMinimumStock(alertConfiguration.getMinimumStock());
        return dto;
    }
}
