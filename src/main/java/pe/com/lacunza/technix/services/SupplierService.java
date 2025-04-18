package pe.com.lacunza.technix.services;

import pe.com.lacunza.technix.dtos.SupplierDto;

import java.util.List;

public interface SupplierService {
    List<SupplierDto> getAllSuppliers();

    SupplierDto getSupplierById(Long id);

    SupplierDto createSupplier(SupplierDto SupplierDto);

    SupplierDto updateSupplier(Long id, SupplierDto SupplierDto);

    void deleteSupplier(Long id);
}
