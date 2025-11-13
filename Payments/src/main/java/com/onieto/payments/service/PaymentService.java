package com.onieto.payments.service;

import com.onieto.payments.client.CourseClient;
import com.onieto.payments.client.UserClient;
import com.onieto.payments.controller.response.MessageResponse;
import com.onieto.payments.dto.EnrollmentDto;
import com.onieto.payments.dto.PaymentDto;
import com.onieto.payments.exception.ResourceNotFoundException;
import com.onieto.payments.model.Payment;
import com.onieto.payments.model.PaymentStatus;
import com.onieto.payments.repository.PaymentRepository;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final UserClient userClient;
    private final CourseClient courseClient;

    public List<Payment> getAllPayments() {
        return paymentRepository.findAll();
    }

    public Payment getPaymentById(Long id) {
        return paymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pago no encontrado: " + id));
    }

    public ResponseEntity<MessageResponse> createPayment(PaymentDto dto) {

        String transactionCode = UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase();

        EnrollmentDto enrollment = validateEnrollment(dto.getEnrollmentId());
        Payment payment = new Payment();
        payment.setTransactionCode(transactionCode);
        payment.setEnrollmentId(enrollment.getId());
        payment.setAmount(enrollment.getFinalPrice());
        payment.setTransactionDate(LocalDateTime.now());
        payment.setStatus(PaymentStatus.PENDING);

        paymentRepository.save(payment);

        return ResponseEntity.ok(new MessageResponse("Pago creado exitosamente."));
    }

    public ResponseEntity<MessageResponse> updatePayment(Long id, PaymentDto dto) {

        EnrollmentDto enrollment = validateEnrollment(dto.getEnrollmentId());

        Payment payment = getPaymentById(id);
        payment.setEnrollmentId(dto.getEnrollmentId());
        payment.setAmount(enrollment.getFinalPrice());

        paymentRepository.save(payment);

        return ResponseEntity.ok(new MessageResponse("Pago actualizado exitosamente."));
    }

    public ResponseEntity<MessageResponse> updatePaymentStatus(Long id,PaymentStatus newStatus) {
        Payment payment = getPaymentById(id);
        payment.setStatus(newStatus);
        paymentRepository.save(payment);
        return ResponseEntity.ok(new MessageResponse("Estado de pago actualizado a: " + newStatus));
    }

    public void deletePaymentById(Long id) {
        Payment payment = getPaymentById(id);
        paymentRepository.delete(payment);
    }

    public void savePayment(Payment payment) {
        paymentRepository.save(payment);
    }

    private void validateUser(Long userId) {
        try {
            userClient.getUserById(userId);
        } catch (FeignException.NotFound e) {
            throw new ResourceNotFoundException("Usuario no encontrado.");
        }
    }

    private void validateCourse(Long courseId) {
        try {
            courseClient.getCourseById(courseId);
        } catch (FeignException.NotFound e) {
            throw new ResourceNotFoundException("Curso no encontrado");
        }
    }

    private EnrollmentDto validateEnrollment(Long enrollmentId) {
        try {
            return courseClient.getEnrollmentDtoById(enrollmentId);

        } catch (FeignException.NotFound e) {
            throw new ResourceNotFoundException("Inscripción no encontrada");
        }
    }

}
