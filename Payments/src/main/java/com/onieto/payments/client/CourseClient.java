package com.onieto.payments.client;

import com.onieto.payments.dto.CourseDto;
import com.onieto.payments.dto.EnrollmentDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "courses", url = "http://localhost:8081/api/courses")
public interface CourseClient {

    @GetMapping("/{id}")
    CourseDto getCourseById(@PathVariable("id") Long id);

    @GetMapping("/enrollments/{id}")
    EnrollmentDto getEnrollmentDtoById(@PathVariable("id") Long id);
}
