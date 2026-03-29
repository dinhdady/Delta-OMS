package com.project.management_system.service.impl;

import com.project.management_system.model.CustomerAddress;
import com.project.management_system.payload.ApiResponse;
import com.project.management_system.repository.CustomerAddressRepository;
import com.project.management_system.service.CustomerAddressService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomerAddressServiceImpl implements CustomerAddressService {

    private final CustomerAddressRepository repository;

    @Override
    public ApiResponse<List<CustomerAddress>> getAddresses(Long customerId) {

        List<CustomerAddress> addresses =
                repository.findByCustomerId(customerId);

        return ApiResponse.<List<CustomerAddress>>builder()
                .status(200)
                .message("Customer addresses retrieved")
                .data(addresses)
                .build();
    }

    @Override
    public ApiResponse<CustomerAddress> create(CustomerAddress address) {

        return ApiResponse.<CustomerAddress>builder()
                .status(201)
                .message("Customer address created")
                .data(repository.save(address))
                .build();
    }
}