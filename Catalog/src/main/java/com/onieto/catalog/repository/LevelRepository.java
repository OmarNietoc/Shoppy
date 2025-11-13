package com.onieto.catalog.repository;

import com.onieto.catalog.model.Level;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LevelRepository extends JpaRepository<Level, Long> {
Optional<Level> findByNameIgnoreCase(String name);
}
