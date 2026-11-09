package com.avicola.monolith.iam.controller;

import com.avicola.monolith.iam.dto.LoginRequest;
import com.avicola.monolith.iam.dto.LoginResponse;
import com.avicola.monolith.iam.dto.RegisterRequest;
import com.avicola.monolith.iam.model.Usuario;
import com.avicola.monolith.iam.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = userService.authenticate(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {
        Usuario usuario = userService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(usuario);
    }
}
