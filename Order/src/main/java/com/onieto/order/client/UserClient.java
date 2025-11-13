package com.onieto.order.client;

import com.onieto.order.controller.response.UserResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "users", url = "http://localhost:8080/api/users")
public interface UserClient {

    @GetMapping("/{id}")
    UserResponseDto getUserById(@PathVariable("id") Long id);
}
