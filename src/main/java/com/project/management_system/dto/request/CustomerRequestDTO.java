package com.project.management_system.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class CustomerRequestDTO {

    @NotBlank(message = "Customer code is required")
    private String code;

    @NotBlank(message = "Customer name is required")
    private String name;

    @Pattern(regexp = "^[0-9]{9,11}$", message = "Invalid phone number")
    private String phone;

    @Email(message = "Invalid email format")
    private String email;

    private String taxCode;

    @NotNull(message = "Customer type is required")
    private Long customerTypeId;
}
