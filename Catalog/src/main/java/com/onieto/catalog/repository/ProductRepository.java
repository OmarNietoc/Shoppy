package com.onieto.catalog.repository;

import com.onieto.catalog.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, String> {
    boolean existsProductsByCategoriaId(Long categoryId);

    boolean existsProductsByUnidId(Long unitId);

    Page<Product> findByCategoriaId(Long categoryId, Pageable pageable);

    Page<Product> findByUnidId(Long unitId, Pageable pageable);

    Page<Product> findByCategoriaIdAndUnidId(Long categoryId, Long unitId, Pageable pageable);
}
