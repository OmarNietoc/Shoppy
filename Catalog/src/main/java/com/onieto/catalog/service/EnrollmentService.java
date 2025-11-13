package com.onieto.catalog.service;

import com.onieto.catalog.controller.response.*;
import com.onieto.catalog.dto.EnrollmentDto;
import com.onieto.catalog.exception.ResourceNotFoundException;
import com.onieto.catalog.model.*;
import com.onieto.catalog.repository.EnrollmentRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;
    private final CourseService courseService;
    private final CouponService couponService;
    private final UserValidatorService userValidatorService;

    public ResponseEntity<EnrollmentResponse> getEnrollmentDtoById(Long id) {
        Enrollment enrollment = getEnrollmentById(id);
        return ResponseEntity.ok(convertToDTO(enrollment));
    }

    public Enrollment getEnrollmentById(Long id) {
        return enrollmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Inscripción no encontrada: " + id));
    }



    public ResponseEntity<List<EnrollmentResponse>> getAllEnrollments() {
        return ResponseEntity.ok(enrollmentRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList()));
    }

    private EnrollmentResponse convertToDTO(Enrollment enrollment) {
        return EnrollmentResponse.builder()
                .id(enrollment.getId())
                .userId(enrollment.getUserId())
                .course(convertCourseToResponse(enrollment.getCourse()))
                .coupon(enrollment.getCoupon() != null ?
                        convertCouponToResponse(enrollment.getCoupon()) : null)
                .finalPrice(enrollment.getFinalPrice())
                .enrollmentDate(enrollment.getEnrollmentDate())
                .active(enrollment.isActive())
                .build();
    }

    private CourseResponse convertCourseToResponse(Course course) {
        return CourseResponse.builder()
                .id(course.getId())
                .title(course.getTitle())
                .description(course.getDescription())
                .category(course.getCategory())
                .level(course.getLevel())
                .instructorId(course.getInstructorId())// Se obtiene del microservicio de usuarios
                .price(course.getPrice())
                .tags(course.getTags())
                .build();
    }

    private CouponResponse convertCouponToResponse(Coupon coupon) {
        return CouponResponse.builder()
                .id(coupon.getId())
                .code(coupon.getCode())
                .discountAmount(coupon.getDiscountAmount())
                .active(coupon.isActive())
                .build();
    }

    public ResponseEntity<MessageResponse> createEnrollment(@Valid EnrollmentDto dto) {
        Enrollment enrollment = buildEnrollmentFromDto(dto);
        enrollmentRepository.save(enrollment);
        return ResponseEntity.status(201).body(new MessageResponse("Inscripción creada exitosamente."));
    }

    public ResponseEntity<MessageResponse> updateEnrollment(Long id, @Valid EnrollmentDto dto) {
        Enrollment existing = getEnrollmentById(id);
        Enrollment updated = buildEnrollmentFromDto(dto);
        updated.setId(existing.getId());
        enrollmentRepository.save(updated);

        return ResponseEntity.ok(new MessageResponse("Inscripción actualizada exitosamente."));
    }

    public void updateEnrollmentStatusById(Long id, Boolean active){

        Enrollment enrollment = getEnrollmentById(id);
        if (active == null) {
            throw new IllegalArgumentException("El estado de la inscripción no puede ser nulo.");
        }
        enrollment.setActive(active);
        enrollmentRepository.save(enrollment);
    }

    public ResponseEntity<MessageResponse> deleteEnrollment(Long id) {
        Enrollment enrollment = enrollmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Inscripción no encontrada: " + id));
        enrollmentRepository.delete(enrollment);
        return ResponseEntity.ok(new MessageResponse("Inscripción eliminada correctamente."));
    }

    private Enrollment buildEnrollmentFromDto(EnrollmentDto dto) {
        UserResponseDto user = userValidatorService.getUserById(dto.getUserId());
        Course course = courseService.getCourseById(dto.getCourseId());
        BigDecimal coursePrice = course.getPrice();
        BigDecimal discount = BigDecimal.ZERO;
        Coupon coupon = null;

        if (dto.getCouponCode() != null && !dto.getCouponCode().isEmpty()) {
            coupon = couponService.getCouponByCode(dto.getCouponCode());

            if (!coupon.isActive()) {
                throw new IllegalArgumentException("Error al usar cupón.");
            }
            discount = coupon.getDiscountAmount();
            couponService.updateCouponStatusByCode(coupon.getCode(), false);
        }

        BigDecimal finalPrice = coursePrice.subtract(discount);
        if (finalPrice.compareTo(BigDecimal.ZERO) < 0) {
            finalPrice = BigDecimal.ZERO;
        }

        return Enrollment.builder()
                .userId(user.getId())
                .course(course)
                .coupon(coupon)
                .finalPrice(finalPrice)
                .enrollmentDate(LocalDateTime.now())
                .active(false)
                .build();
    }


}