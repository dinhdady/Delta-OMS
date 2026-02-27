package com.project.management_system.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CustomerResponseDTO {

    private Long id;
    private String code;
    private String name;
    private String phone;
    private String email;
    private String taxCode;
    private String customerTypeName;
}

