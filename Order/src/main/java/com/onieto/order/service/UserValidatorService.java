package com.onieto.order.service;

import com.onieto.order.client.UserClient;
import com.onieto.order.controller.response.UserResponseDto;
import com.onieto.order.exception.ResourceNotFoundException;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class UserValidatorService {

    private final UserClient userClient;

    public UserResponseDto getUserByEmail(String email) {
        if (!StringUtils.hasText(email)) {
            throw new IllegalArgumentException("El email del usuario es obligatorio.");
        }

        try {
            UserResponseDto user = userClient.getUserByEmail(email);
            if (user.getStatus() != null && user.getStatus() == 0) {
                throw new IllegalArgumentException("El usuario con email " + email + " no está activo.");
            }
            return user;
        } catch (FeignException.NotFound e) {
            throw new ResourceNotFoundException("El usuario no existe: " + email);
        } catch (FeignException e) {
            throw new IllegalArgumentException("Error al obtener el usuario: " + e.getMessage());
        }
    }
}
