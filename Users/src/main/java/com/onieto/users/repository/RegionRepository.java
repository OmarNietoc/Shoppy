package com.onieto.users.repository;

import com.onieto.users.model.Comuna;
import com.onieto.users.model.Region;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RegionRepository extends JpaRepository<Region, Long> {
    Region getRegionById(Long region);
}
