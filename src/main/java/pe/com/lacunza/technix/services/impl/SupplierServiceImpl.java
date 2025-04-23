package pe.com.lacunza.technix.services.impl;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.com.lacunza.technix.domain.entities.jpa.Supplier;
import pe.com.lacunza.technix.domain.repositories.jpa.SupplierRepository;
import pe.com.lacunza.technix.dtos.SupplierDto;
import pe.com.lacunza.technix.services.SupplierService;
import pe.com.lacunza.technix.api.exception.ResourceNotFoundException;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Transactional
@Service
@Slf4j
@AllArgsConstructor
public class SupplierServiceImpl implements SupplierService {

    private SupplierRepository supplierRepository;

    @Override
    public List<SupplierDto> getAllSuppliers() {
        return supplierRepository.findAll().stream()
                .map(SupplierServiceImpl::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public SupplierDto getSupplierById(Long id) {
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Proveedor no encontrado con id: " + id));
        return convertToDTO(supplier);
    }

    @Override
    public SupplierDto createSupplier(SupplierDto supplierDto) {
        Supplier supplier = convertToEntity(supplierDto);
        Supplier savedSupplier = supplierRepository.save(supplier);
        return convertToDTO(savedSupplier);
    }

    @Override
    public SupplierDto updateSupplier(Long id, SupplierDto supplierDto) {
        Supplier existingSupplier = supplierRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Proveedor no encontrado con id: " + id));

        // Actualizar campos
        if(Objects.nonNull(supplierDto.getName())) {
            existingSupplier.setName(supplierDto.getName());
        }
        if(Objects.nonNull(supplierDto.getContactPerson())) {
            existingSupplier.setContactPerson(supplierDto.getContactPerson());
        }
        if(Objects.nonNull(supplierDto.getPhone())) {
            existingSupplier.setPhone(supplierDto.getPhone());
        }
        if(Objects.nonNull(supplierDto.getEmail())) {
            existingSupplier.setEmail(supplierDto.getEmail());
        }
        if(Objects.nonNull(supplierDto.getAddress())) {
            existingSupplier.setAddress(supplierDto.getAddress());
        }

        Supplier updatedSupplier = supplierRepository.save(existingSupplier);
        return convertToDTO(updatedSupplier);
    }

    @Override
    public void deleteSupplier(Long id) {
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Proveedor no encontrado con id: " + id));
        supplierRepository.delete(supplier);
    }

    public static SupplierDto convertToDTO(Supplier supplier) {
        return SupplierDto.builder()
                .id(supplier.getId())
                .name(supplier.getName())
                .contactPerson(supplier.getContactPerson())
                .phone(supplier.getPhone())
                .email(supplier.getEmail())
                .address(supplier.getAddress())
                .build();
    }

    private Supplier convertToEntity(SupplierDto supplierDto) {
        return Supplier.builder()
                .id(supplierDto.getId())
                .name(supplierDto.getName())
                .contactPerson(supplierDto.getContactPerson())
                .phone(supplierDto.getPhone())
                .email(supplierDto.getEmail())
                .address(supplierDto.getAddress())
                .build();
    }
}
