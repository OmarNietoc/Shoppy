package com.onieto.order.service;

import com.onieto.order.client.UserClient;
import com.onieto.order.controller.response.UserResponseDto;
import com.onieto.order.exception.ResourceNotFoundException;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserValidatorService {

    private final UserClient userClient;

    public UserResponseDto getUserById(Long userId) {
        UserResponseDto user;

        try {
            user = userClient.getUserById(userId);
        } catch (FeignException.NotFound e) {
            throw new ResourceNotFoundException("El usuario no existe: " + userId);
        } catch (FeignException e) {
            throw new IllegalArgumentException("Error al obtener el usuario: " + e.getMessage());
        }
        return user;
    }

    public UserResponseDto validateInstructor(Long instructorId) {
        UserResponseDto instructor;
        instructor = getUserById(instructorId);
        validateRoleOfUser(instructor.getRole().getName(), "INSTRUCTOR");

        return instructor;
    }

    public boolean validateRoleOfUser(String roleName, String nameSearched) {
        if (roleName == null || !roleName.equalsIgnoreCase(nameSearched)) {
            throw new IllegalArgumentException("El usuario ingresado no tiene el role de " + nameSearched + ".");
        }
        return true;
    }
}
