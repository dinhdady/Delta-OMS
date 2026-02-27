package com.project.management_system.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CustomerRequestDTO {

    private String code;
    private String name;
    private String phone;
    private String email;
    private String taxCode;
    private Long customerTypeId;
}

