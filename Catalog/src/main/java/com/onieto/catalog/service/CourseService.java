package com.onieto.catalog.service;

import com.onieto.catalog.client.UserClient;
import com.onieto.catalog.controller.response.MessageResponse;
import com.onieto.catalog.dto.CourseDto;
import com.onieto.catalog.exception.ResourceNotFoundException;
import com.onieto.catalog.model.Category;
import com.onieto.catalog.model.Course;
import com.onieto.catalog.model.Level;
import com.onieto.catalog.repository.CourseRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CourseService {

    private final CourseRepository courseRepository;
    private final CategoryService categoryService;
    private final LevelService levelService;
    private final UserClient userClient;
    private final UserValidatorService userValidatorService;

    public Page<Course> getCourses(Integer page, Integer size, Long categoryId, Long levelId) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").ascending());

        if (categoryId != null && levelId != null) {
            return courseRepository.findByCategoryIdAndLevelId(categoryId, levelId, pageable);
        } else if (categoryId != null) {
            return courseRepository.findByCategoryId(categoryId, pageable);
        } else if (levelId != null) {
            return courseRepository.findByLevelId(levelId, pageable);
        } else {
            return courseRepository.findAll(pageable);
        }
    }


    public Course getCourseById(Long id) {
        return courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Curso no encontrado: "+ id ));
    }


    public ResponseEntity<?> createCourse(@Valid CourseDto dto) {
        Category category = categoryService.getCategoryById(dto.getCategoryId());
        Level level = levelService.getLevelById(dto.getLevelId());

        userValidatorService.validateInstructor(dto.getInstructorId());

        Course course = Course.builder()
                .title(dto.getTitle())
                .description(dto.getDescription())
                .category(category)
                .level(level)
                .instructorId(dto.getInstructorId())
                .price(dto.getPrice())
                .tags(dto.getTags())
                .build();

        courseRepository.save(course);
        return ResponseEntity.ok(new MessageResponse("Curso creado exitosamente"));
    }

    public ResponseEntity<MessageResponse> updateCourse(Long id, CourseDto dto) {
            Course course = getCourseById(id);
            Category category = categoryService.getCategoryById(dto.getCategoryId());
            Level level = levelService.getLevelById(dto.getLevelId());
            userValidatorService.validateInstructor(dto.getInstructorId());

            course.setInstructorId(dto.getInstructorId());
            course.setTitle(dto.getTitle());
            course.setDescription(dto.getDescription());
            course.setPrice(dto.getPrice());
            course.setCategory(category);
            course.setLevel(level);
            course.setTags(dto.getTags());

            courseRepository.save(course);
            return ResponseEntity.ok(new MessageResponse("Curso actualizado correctamente."));
    }

    public ResponseEntity<MessageResponse> deleteCourse(Long id) {
        Course course = getCourseById(id);
        courseRepository.delete(course);
        return ResponseEntity.ok(new MessageResponse("Curso eliminado correctamente."));
    }
}
