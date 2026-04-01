package com.project.management_system.service.impl;

import com.project.management_system.dto.request.AuthRequestDTO;
import com.project.management_system.dto.request.RegisterRequestDTO;
import com.project.management_system.model.RefreshToken;
import com.project.management_system.model.Role;
import com.project.management_system.model.User;
import com.project.management_system.payload.ApiResponse;
import com.project.management_system.repository.RefreshTokenRepository;
import com.project.management_system.repository.RoleRepository;
import com.project.management_system.repository.UserRepository;
import com.project.management_system.security.util.JwtUtil;
import com.project.management_system.service.AuthenticationService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;

@Service
@AllArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenRepository refreshTokenRepository;

    @Transactional
    @Override
    public ResponseEntity<ApiResponse<String>> login(AuthRequestDTO request, HttpServletResponse response) {

        // 1. AUTHENTICATE
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword())
        );

        // 2. GET USER
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 3. DELETE OLD TOKENS
        refreshTokenRepository.deleteByUser(user);

        // 🔥 QUAN TRỌNG: flush ngay để tránh duplicate
        refreshTokenRepository.flush();

        // 4. GENERATE TOKENS
        String accessToken = jwtUtil.generateAccessToken(user.getUsername());
        String refreshToken = jwtUtil.generateRefreshToken(user.getUsername());

        // 5. SAVE REFRESH TOKEN
        RefreshToken token = new RefreshToken();
        token.setToken(refreshToken);
        token.setUser(user);
        token.setExpiryDate(LocalDateTime.now().plusDays(7));
        token.setRevoked(false); // 👈 nên có

        refreshTokenRepository.save(token);

        // 6. SET COOKIE
        setCookies(response, accessToken, refreshToken);

        // 7. RESPONSE
        return ResponseEntity.ok(
                ApiResponse.<String>builder()
                        .status(200)
                        .message("Login successful")
                        .data("OK")
                        .build()
        );
    }

    @Override
    public ApiResponse<String> register(RegisterRequestDTO request) {

        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username already exists");
        }

        Role role = roleRepository.findByName("STAFF")
                .orElseThrow(() -> new RuntimeException("Default role not found"));

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setFullName(request.getFullName());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setEnabled(true);
        user.setRoles(Collections.singleton(role));

        userRepository.save(user);

        return ApiResponse.<String>builder()
                .status(201)
                .message("User registered")
                .data("Success")
                .build();
    }

    @Override
    public ResponseEntity<ApiResponse<String>> logout(String refreshToken, HttpServletResponse response) {

        if (refreshToken != null) {
            refreshTokenRepository.findByToken(refreshToken)
                    .ifPresent(token -> {
                        token.setRevoked(true);
                        refreshTokenRepository.save(token);
                    });
        }

        return ResponseEntity.ok(
                ApiResponse.<String>builder()
                        .status(200)
                        .message("Logged out")
                        .data("Success")
                        .build()
        );
    }

    @Override
    public ResponseEntity<ApiResponse<String>> refreshToken(String refreshToken, HttpServletResponse response) {

        RefreshToken stored = refreshTokenRepository.findByToken(refreshToken)
                .orElseThrow(() -> new RuntimeException("Invalid refresh token"));

        if (stored.isRevoked() || stored.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Refresh token expired");
        }

        String newAccessToken = jwtUtil.generateAccessToken(stored.getUser().getUsername());

        return ResponseEntity.ok(
                ApiResponse.<String>builder()
                        .status(200)
                        .message("Token refreshed")
                        .data(newAccessToken)
                        .build()
        );
    }
    private void setCookies(HttpServletResponse response,
                            String accessToken,
                            String refreshToken) {

        ResponseCookie accessCookie = ResponseCookie.from("accessToken", accessToken)
                .httpOnly(true)
                .secure(false) // true nếu deploy HTTPS
                .path("/")
                .maxAge(15 * 60)
                .sameSite("Strict")
                .build();

        ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true)
                .secure(false)
                .path("/api/auth/refresh")
                .maxAge(7 * 24 * 60 * 60)
                .sameSite("Strict")
                .build();

        response.addHeader("Set-Cookie", accessCookie.toString());
        response.addHeader("Set-Cookie", refreshCookie.toString());
    }
}