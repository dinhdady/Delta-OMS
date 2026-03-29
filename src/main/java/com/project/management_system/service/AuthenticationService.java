package com.project.management_system.service;

import com.project.management_system.dto.request.AuthRequestDTO;
import com.project.management_system.dto.request.RegisterRequestDTO;
import com.project.management_system.dto.response.AuthResponseDTO;
import com.project.management_system.payload.ApiResponse;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;

public interface AuthenticationService {
    ResponseEntity<ApiResponse<String>> login(AuthRequestDTO request, HttpServletResponse response);

    ApiResponse<String> register(RegisterRequestDTO request);

    ResponseEntity<ApiResponse<String>> logout(String refreshToken, HttpServletResponse response);

    ResponseEntity<ApiResponse<String>> refreshToken(String refreshToken, HttpServletResponse response);
}
