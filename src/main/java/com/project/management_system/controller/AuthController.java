package com.project.management_system.controller;

import com.project.management_system.dto.request.AuthRequestDTO;
import com.project.management_system.dto.request.RegisterRequestDTO;
import com.project.management_system.dto.response.AuthResponseDTO;
import com.project.management_system.model.RefreshToken;
import com.project.management_system.model.Role;
import com.project.management_system.model.User;
import com.project.management_system.repository.RefreshTokenRepository;
import com.project.management_system.repository.RoleRepository;
import com.project.management_system.repository.UserRepository;
import com.project.management_system.security.util.JwtUtil;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Collections;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenRepository refreshTokenRepository;
    // LOGIN
    @PostMapping("/login")
    public ResponseEntity<?> login(
            @RequestBody AuthRequestDTO request,
            HttpServletResponse response) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(), request.getPassword())
        );

        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow();

        // Remove old refresh tokens
        refreshTokenRepository.deleteByUser(user);

        String accessToken = jwtUtil.generateAccessToken(user.getUsername());
        String refreshToken = jwtUtil.generateRefreshToken(user.getUsername());

        RefreshToken refreshTokenEntity = new RefreshToken();
        refreshTokenEntity.setToken(refreshToken);
        refreshTokenEntity.setUser(user);
        refreshTokenEntity.setExpiryDate(LocalDateTime.now().plusDays(7));
        refreshTokenRepository.save(refreshTokenEntity);

        setCookies(response, accessToken, refreshToken);

        return ResponseEntity.ok("Login successful");
    }
    // REGISTER
    @PostMapping("/register")
    public String register(@Valid @RequestBody RegisterRequestDTO request) {

        if (userRepository.existsByUsername(request.getUsername())) {
            return "Username already exists";
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            return "Email already exists";
        }

        Role userRole = roleRepository.findByName("USER")
                .orElseThrow(() -> new RuntimeException("Default role not found"));

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setFullName(request.getFullName());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setEnabled(true);
        user.setRoles(Collections.singleton(userRole));

        userRepository.save(user);

        return "User registered successfully";
    }
    @PostMapping("/logout")
    public ResponseEntity<?> logout(
            @CookieValue(name = "refreshToken", required = false) String refreshToken,
            HttpServletResponse response) {

        if (refreshToken != null) {
            refreshTokenRepository.findByToken(refreshToken)
                    .ifPresent(token -> {
                        token.setRevoked(true);
                        refreshTokenRepository.save(token);
                    });
        }

        clearCookies(response);

        return ResponseEntity.ok("Logged out");
    }
    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(
            @CookieValue(name = "refreshToken", required = false) String refreshToken,
            HttpServletResponse response) {

        if (refreshToken == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Missing refresh token");
        }

        RefreshToken storedToken = refreshTokenRepository.findByToken(refreshToken)
                .orElseThrow(() -> new RuntimeException("Invalid refresh token"));

        if (storedToken.isRevoked() ||
                storedToken.getExpiryDate().isBefore(LocalDateTime.now())) {

            refreshTokenRepository.delete(storedToken);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Refresh token expired");
        }

        User user = storedToken.getUser();

        // ROTATE TOKEN
        refreshTokenRepository.delete(storedToken);

        String newAccessToken = jwtUtil.generateAccessToken(user.getUsername());
        String newRefreshToken = jwtUtil.generateRefreshToken(user.getUsername());

        RefreshToken newToken = new RefreshToken();
        newToken.setToken(newRefreshToken);
        newToken.setUser(user);
        newToken.setExpiryDate(LocalDateTime.now().plusDays(7));
        refreshTokenRepository.save(newToken);

        setCookies(response, newAccessToken, newRefreshToken);

        return ResponseEntity.ok("Token refreshed");
    }
    private void setCookies(HttpServletResponse response,
                            String accessToken,
                            String refreshToken) {

        ResponseCookie accessCookie = ResponseCookie.from("accessToken", accessToken)
                .httpOnly(true)
                .secure(false) // true in production
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

    private void clearCookies(HttpServletResponse response) {

        ResponseCookie accessCookie = ResponseCookie.from("accessToken", "")
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(0)
                .build();

        ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", "")
                .httpOnly(true)
                .secure(false)
                .path("/api/auth/refresh")
                .maxAge(0)
                .build();

        response.addHeader("Set-Cookie", accessCookie.toString());
        response.addHeader("Set-Cookie", refreshCookie.toString());
    }
}