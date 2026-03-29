package com.project.management_system.service.impl;

import com.project.management_system.model.CustomerType;
import com.project.management_system.payload.ApiResponse;
import com.project.management_system.repository.CustomerTypeRepository;
import com.project.management_system.service.CustomerTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomerTypeServiceImpl implements CustomerTypeService {

    private final CustomerTypeRepository repository;

    @Override
    public ApiResponse<List<CustomerType>> getAll() {

        return ApiResponse.<List<CustomerType>>builder()
                .status(200)
                .message("Customer types retrieved")
                .data(repository.findAll())
                .build();
    }

    @Override
    public ApiResponse<CustomerType> create(CustomerType type) {

        return ApiResponse.<CustomerType>builder()
                .status(201)
                .message("Customer type created")
                .data(repository.save(type))
                .build();
    }
}
