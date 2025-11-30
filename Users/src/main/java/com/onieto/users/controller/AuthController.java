package com.onieto.users.controller;

import com.onieto.users.controller.response.JwtResponse;
import com.onieto.users.controller.response.MessageResponse;
import com.onieto.users.dto.LoginRequest;
import com.onieto.users.dto.UserDto;
import com.onieto.users.model.Role;
import com.onieto.users.model.User;
import com.onieto.users.repository.ComunaRepository;
import com.onieto.users.repository.RegionRepository;
import com.onieto.users.repository.RoleRepository;
import com.onieto.users.repository.UserRepository;
import com.onieto.users.security.jwt.JwtUtils;
import com.onieto.users.security.services.UserDetailsImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
        @Autowired
        AuthenticationManager authenticationManager;

        @Autowired
        UserRepository userRepository;

        @Autowired
        RoleRepository roleRepository;

        @Autowired
        RegionRepository regionRepository;

        @Autowired
        ComunaRepository comunaRepository;

        @Autowired
        PasswordEncoder encoder;

        @Autowired
        JwtUtils jwtUtils;

        @PostMapping("/login")
        public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {

                Authentication authentication = authenticationManager.authenticate(
                                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(),
                                                loginRequest.getPassword()));

                SecurityContextHolder.getContext().setAuthentication(authentication);
                String jwt = jwtUtils.generateJwtToken(authentication);

                UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
                List<String> roles = userDetails.getAuthorities().stream()
                                .map(item -> item.getAuthority())
                                .collect(Collectors.toList());

                return ResponseEntity.ok(new JwtResponse(jwt,
                                userDetails.getId(),
                                userDetails.getUsername(),
                                userDetails.getEmail(),
                                roles));
        }

        @PostMapping("/register")
        public ResponseEntity<?> registerUser(@Valid @RequestBody UserDto signUpRequest) {
                if (userRepository.existsByEmail(signUpRequest.getEmail())) {
                        return ResponseEntity
                                        .badRequest()
                                        .body(new MessageResponse("Error: Email is already in use!"));
                }

                // Create new user's account
                User user = new User(signUpRequest.getName(),
                                signUpRequest.getEmail(),
                                encoder.encode(signUpRequest.getPassword()),
                                roleRepository.findById(signUpRequest.getRole())
                                                .orElseThrow(() -> new RuntimeException("Error: Role is not found.")),
                                signUpRequest.getStatus(),
                                null, // imagen
                                null, // firebaseId
                                signUpRequest.getPhone(),
                                signUpRequest.getRegion() != null
                                                ? regionRepository.findById(signUpRequest.getRegion()).orElse(null)
                                                : null,
                                signUpRequest.getComuna() != null
                                                ? comunaRepository.findById(signUpRequest.getComuna()).orElse(null)
                                                : null);

                userRepository.save(user);

                return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
        }
}
