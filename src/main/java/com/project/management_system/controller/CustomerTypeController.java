package com.project.management_system.controller;

import com.project.management_system.model.CustomerType;
import com.project.management_system.payload.ApiResponse;
import com.project.management_system.service.CustomerTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customer-types")
@RequiredArgsConstructor
public class CustomerTypeController {

    private final CustomerTypeService customerTypeService;

    @GetMapping
    public ApiResponse<List<CustomerType>> getAll() {
        return customerTypeService.getAll();
    }

    @PostMapping
    public ApiResponse<CustomerType> create(@RequestBody CustomerType type) {
        return customerTypeService.create(type);
    }
}