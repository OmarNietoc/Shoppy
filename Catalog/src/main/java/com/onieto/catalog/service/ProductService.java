package com.onieto.catalog.service;

import com.onieto.catalog.controller.response.MessageResponse;
import com.onieto.catalog.dto.ProductDto;
import com.onieto.catalog.exception.ResourceNotFoundException;
import com.onieto.catalog.model.Category;
import com.onieto.catalog.model.Product;
import com.onieto.catalog.model.Unit;
import com.onieto.catalog.repository.ProductRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryService categoryService;
    private final UnitService unitService;

    public Page<Product> getProducts(Integer page, Integer size, Long categoryId, Long unitId) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").ascending());

        if (categoryId != null && unitId != null) {
            return productRepository.findByCategoriaIdAndUnidId(categoryId, unitId, pageable);
        } else if (categoryId != null) {
            return productRepository.findByCategoriaId(categoryId, pageable);
        } else if (unitId != null) {
            return productRepository.findByUnidId(unitId, pageable);
        } else {
            return productRepository.findAll(pageable);
        }
    }


    public Product getProductById(String id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado: " + id));
    }


    public ResponseEntity<?> createProduct(@Valid ProductDto dto) {
        Category category = categoryService.getCategoryById(dto.getCategoriaId());
        Unit unit = unitService.getUnitById(dto.getUnidadId());

        Product product = Product.builder()
                .id(dto.getId())
                .nombre(dto.getNombre())
                .descripcion(dto.getDescripcion())
                .precio(dto.getPrecio())
                .stock(dto.getStock())
                .stockMinimo(resolveStockMinimo(dto.getStockMinimo()))
                .activo(resolveActivo(dto.getActivo()))
                .categoria(category)
                .unid(unit)
                .imagen(dto.getImagen())
                .build();

        productRepository.save(product);
        return ResponseEntity.status(HttpStatus.CREATED).body(new MessageResponse("Producto creado exitosamente."));
    }

    public ResponseEntity<MessageResponse> updateProduct(String id, ProductDto dto) {
        Product product = getProductById(id);
        Category category = categoryService.getCategoryById(dto.getCategoriaId());
        Unit unit = unitService.getUnitById(dto.getUnidadId());

        product.setNombre(dto.getNombre());
        product.setDescripcion(dto.getDescripcion());
        product.setPrecio(dto.getPrecio());
        product.setStock(dto.getStock());
        product.setStockMinimo(resolveStockMinimo(dto.getStockMinimo()));
        product.setActivo(resolveActivo(dto.getActivo()));
        product.setCategoria(category);
        product.setUnid(unit);
        product.setImagen(dto.getImagen());

        productRepository.save(product);
        return ResponseEntity.ok(new MessageResponse("Producto actualizado correctamente."));
    }

    public ResponseEntity<MessageResponse> deleteProduct(String id) {
        Product product = getProductById(id);
        productRepository.delete(product);
        return ResponseEntity.ok(new MessageResponse("Producto eliminado correctamente."));
    }

    private Integer resolveStockMinimo(Integer value) {
        return value != null ? value : Product.DEFAULT_STOCK_MINIMO;
    }

    private Integer resolveActivo(Integer value) {
        return value != null ? value : Product.DEFAULT_ACTIVO;
    }
}
