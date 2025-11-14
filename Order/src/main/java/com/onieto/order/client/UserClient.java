package com.onieto.order.client;

import com.onieto.order.controller.response.UserResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "users", url = "http://localhost:8080/api/users")
public interface UserClient {

    @GetMapping("/by-email")
    UserResponseDto getUserByEmail(@RequestParam("email") String email);
}
