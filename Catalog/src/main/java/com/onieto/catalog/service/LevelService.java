package com.onieto.catalog.service;

import com.onieto.catalog.exception.ConflictException;
import com.onieto.catalog.exception.ResourceNotFoundException;
import com.onieto.catalog.model.Level;
import com.onieto.catalog.repository.CourseRepository;
import com.onieto.catalog.repository.LevelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class LevelService {

    private final LevelRepository levelRepository;
    private final CourseRepository courseRepository;


    public Level getLevelById(Long id) {
        return levelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Level no encontrado: "+ id ));
    }

    public Level getLevelByName(String name) {
        return levelRepository.findByNameIgnoreCase(name)
                .orElseThrow(() -> new ResourceNotFoundException("Level no encontrado: " + name));
    }

    public List<Level> getAllLevels() {
        return levelRepository.findAll();
    }

    public boolean validateLevelByName(String name) {
        Optional <Level> existingLevel = levelRepository.findByNameIgnoreCase(name);
        if (existingLevel.isPresent()) {
            throw new IllegalArgumentException("Ya existe un level con ese nombre.");
        }
        return true;
    }

    public void createLevel(Level level) {

        try {
            validateLevelByName(level.getName());
            levelRepository.save(level);
        } catch (DataIntegrityViolationException ex) {
            throw new IllegalArgumentException("Ya existe un level con ese nombre.");
        }

    }

    public void updateLevel(Long id, Level updatedLevel) {
        Level existingLevel = levelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Level no encontrado: " + id));
        validateLevelByName(updatedLevel.getName());
        existingLevel.setName(updatedLevel.getName());
        levelRepository.save(existingLevel);
    }

    public void deleteLevel(Long id) {
        getLevelById(id);
        if(courseRepository.existsCoursesByLevelId(id)){
            throw new ConflictException("No se puede eliminar el nivel porque hay cursos asociados a este.");
        }
        else {
        levelRepository.deleteById(id);
        }
    }

}
