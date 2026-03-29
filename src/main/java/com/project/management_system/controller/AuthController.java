package com.project.management_system.controller;

import com.project.management_system.dto.request.AuthRequestDTO;
import com.project.management_system.dto.request.RegisterRequestDTO;
import com.project.management_system.payload.ApiResponse;
import com.project.management_system.service.AuthenticationService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationService authService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<String>> login(
            @RequestBody AuthRequestDTO request,
            HttpServletResponse response) {

        return authService.login(request, response);
    }

    @PostMapping("/register")
    public ApiResponse<String> register(
            @Valid @RequestBody RegisterRequestDTO request) {

        return authService.register(request);
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<String>> logout(
            @CookieValue(name = "refreshToken", required = false) String refreshToken,
            HttpServletResponse response) {

        return authService.logout(refreshToken, response);
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<String>> refreshToken(
            @CookieValue(name = "refreshToken", required = false) String refreshToken,
            HttpServletResponse response) {

        return authService.refreshToken(refreshToken, response);
    }
}