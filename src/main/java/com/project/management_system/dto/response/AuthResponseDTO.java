package com.project.management_system.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class AuthResponseDTO {
    private int status;
    private String message;
    private String accessToken;
    private String refreshToken;
    private String tokenType;
    private String username;
    private String role;
}
