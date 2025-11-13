package com.onieto.catalog.repository;

import com.onieto.catalog.model.Course;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {
    boolean existsCoursesByCategoryId(Long categoryId);
    boolean existsCoursesByLevelId(Long levelId);
    Page<Course> findByCategoryId(Long categoryId, Pageable pageable);
    Page<Course> findByLevelId(Long levelId, Pageable pageable);
    Page<Course> findByCategoryIdAndLevelId(Long categoryId, Long levelId, Pageable pageable);
}
