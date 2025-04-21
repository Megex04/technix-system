package pe.com.lacunza.technix.services.impl;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.com.lacunza.technix.api.exception.InsufficientStockException;
import pe.com.lacunza.technix.api.exception.ResourceNotFoundException;
import pe.com.lacunza.technix.api.models.request.StockAdjustRequest;
import pe.com.lacunza.technix.api.models.request.StockInRequest;
import pe.com.lacunza.technix.api.models.request.StockOutRequest;
import pe.com.lacunza.technix.config.jwt.JwtTokenProvider;
import pe.com.lacunza.technix.domain.entities.documents.User;
import pe.com.lacunza.technix.domain.entities.jpa.InventoryMovement;
import pe.com.lacunza.technix.domain.entities.jpa.Product;
import pe.com.lacunza.technix.domain.entities.jpa.Supplier;
import pe.com.lacunza.technix.domain.repositories.jpa.InventoryMovementRepository;
import pe.com.lacunza.technix.domain.repositories.jpa.ProductRepository;
import pe.com.lacunza.technix.domain.repositories.jpa.SupplierRepository;
import pe.com.lacunza.technix.domain.repositories.mongo.UserRepository;
import pe.com.lacunza.technix.dtos.InventoryMovementDto;
import pe.com.lacunza.technix.services.InventoryService;
import pe.com.lacunza.technix.services.ProductService;
import pe.com.lacunza.technix.util.MovementType;
import pe.com.lacunza.technix.util.SortType;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

@Service
@Slf4j
@AllArgsConstructor
public class InventoryServiceImpl implements InventoryService {

    private final InventoryMovementRepository movementRepository;
    private final ProductRepository productRepository;
    private final SupplierRepository supplierRepository;
    private final UserRepository userRepository;

    private final ProductService productService;
    private final JwtTokenProvider tokenProvider;

    @Override
    public Page<InventoryMovementDto> getAllMovements(Integer page, Integer size, SortType sortType, String fieldSorted) {
        PageRequest pageRequest = null;
        switch (sortType) {
            case NONE -> pageRequest = PageRequest.of(page, size);
            case LOWER -> pageRequest = PageRequest.of(page, size, Sort.by(fieldSorted).ascending());
            case UPPER -> pageRequest = PageRequest.of(page, size, Sort.by(fieldSorted).descending());
        }
        return movementRepository.findAll(pageRequest)
                .map(this::convertToDto);
    }

    @Transactional
    @Override
    public InventoryMovementDto registerStockIn(String jwtUser, StockInRequest request) {
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + request.getProductId()));

        Supplier supplier = null;
        if (request.getSupplierId() != null) {
            supplier = supplierRepository.findById(request.getSupplierId())
                    .orElseThrow(() -> new ResourceNotFoundException("Supplier not found with id: " + request.getSupplierId()));
        }
        Integer previousStock = product.getStockQuantity();

        // Update product stock
        Integer currentStock = product.getStockQuantity() + request.getQuantity();
        product.setStockQuantity(currentStock);
        productRepository.save(product);

        // obteniendo los datos del usuario autenticado
        User user = getUserFromJwt(jwtUser);

        // Create movement record
        InventoryMovement movement = new InventoryMovement();
        movement.setProduct(product);
        movement.setType(MovementType.ENTRY);
        movement.setQuantity(request.getQuantity());
        movement.setPreviousStock(previousStock);
        movement.setCurrentStock(currentStock);
        movement.setUserId(user.getId());
        movement.setSupplier(supplier);
        movement.setCreatedAt(LocalDateTime.now());
        movement.setCreatedBy(user.getUsername()); // Replace with actual user when authentication is implemented
        movement.setReferenceNumber(request.getReferenceNumber());
        movement.setComment(request.getNotes());

        InventoryMovement inventoryMovementSaved = movementRepository.save(movement);
        return convertToDto(inventoryMovementSaved);
    }

    @Transactional
    @Override
    public InventoryMovementDto registerStockOut(String jwtUser, StockOutRequest request) {
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + request.getProductId()));

        // validando la cantidad de stock
        if (product.getStockQuantity() < request.getQuantity()) {
            throw new InsufficientStockException("Insufficient stock for product: " + product.getName());
        }
        Integer previousStock = product.getStockQuantity();
        Integer currentStock = product.getStockQuantity() - request.getQuantity();

        // actualizando el stock del producto
        product.setStockQuantity(currentStock);
        productRepository.save(product);

        // revisando si el stock es bajo
        Boolean isStockLowProduct = productService.findLowStockProductById(product.getId());

        if(Boolean.TRUE.equals(isStockLowProduct)){
            log.warn("Stock is low for product: " + product.getName());
        } else {
            log.info("Stock is sufficient for product: " + product.getName());
        }

        // obteniendo los datos del usuario autenticado
        User user = getUserFromJwt(jwtUser);

        // se crea el registro de movimiento
        InventoryMovement movement = new InventoryMovement();
        movement.setProduct(product);
        movement.setType(MovementType.EXIT);
        movement.setQuantity(request.getQuantity());
        movement.setPreviousStock(previousStock);
        movement.setCurrentStock(currentStock);
        movement.setUserId(user.getId());
        movement.setSupplier(null);
        movement.setCreatedAt(LocalDateTime.now());
        movement.setCreatedBy(user.getUsername()); // Replace with actual user when authentication is implemented
        movement.setReferenceNumber(request.getReferenceNumber());
        movement.setComment(request.getReason());

        InventoryMovement  inventoryMovementSaved = movementRepository.save(movement);
        return convertToDto(inventoryMovementSaved);
    }

    @Transactional
    @Override
    public InventoryMovementDto adjustInventory(String jwtUser, StockAdjustRequest request) {
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + request.getProductId()));

        int previousStock = product.getStockQuantity();
        int adjustment = request.getNewQuantity() - previousStock;
        int currentStock = request.getNewQuantity();

        // Update product stock to the new quantity
        product.setStockQuantity(currentStock);
        productRepository.save(product);

        // Comprueba si el stock está por debajo del umbral de alerta después del ajuste
        if (adjustment <= 0) {
            Boolean isStockLowProduct = productService.findLowStockProductById(product.getId());

            if(Boolean.TRUE.equals(isStockLowProduct)){
                log.warn("Stock is low for product: " + product.getName());
            } else {
                log.info("Stock is sufficient for product: " + product.getName());
            }
        }
        User user = getUserFromJwt(jwtUser);

        // Create movement record
        InventoryMovement movement = new InventoryMovement();
        movement.setProduct(product);
        movement.setType(MovementType.ADJUSTMENT);
        movement.setQuantity(Math.abs(adjustment));  // guarda el valor absolute del ajuste
        movement.setPreviousStock(previousStock);
        movement.setCurrentStock(currentStock);
        movement.setUserId(user.getId());
        movement.setSupplier(null);
        movement.setCreatedAt(LocalDateTime.now());
        movement.setCreatedBy(user.getUsername());
        movement.setComment(request.getReason() + (adjustment >= 0 ? " (Aumento)" : " (Disminucion)"));

        InventoryMovement inventoryMovementSaved = movementRepository.save(movement);
        return convertToDto(inventoryMovementSaved);
    }

    public User getUserFromJwt(String jwt) {
        String userEmail = tokenProvider.getUserIdFromJWT(jwt);
        return userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public InventoryMovementDto convertToDto(InventoryMovement movement) {
        Product productFind = null;
        Supplier supplierFind = null;
        Optional<Product> product = productRepository.findById(movement.getProduct().getId());
        if(product.isPresent()) {
            productFind = product.get();
        }
        if(Objects.nonNull(movement.getSupplier())) {
            Optional<Supplier> supplier = supplierRepository.findById(movement.getSupplier().getId());
            if(supplier.isPresent()) {
                supplierFind = supplier.get();
            }
        }

        return InventoryMovementDto.builder()
                .id(movement.getId())
                .product(productFind != null ? ProductServiceImpl.fromModelToProductDto(productFind) : null)
                .movementType(movement.getType().name())
                .quantity(movement.getQuantity())
                .previousStock(movement.getPreviousStock())
                .currentStock(movement.getCurrentStock())
                .userId(movement.getUserId())
                .supplier(supplierFind != null ? SupplierServiceImpl.convertToDTO(supplierFind) : null)
                .createdAt(movement.getCreatedAt())
                .createdBy(movement.getCreatedBy())
                .referenceNumber(movement.getReferenceNumber())
                .reason(movement.getComment())
                .build();
    }
}
