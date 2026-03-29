package com.project.management_system.controller;

import com.project.management_system.model.CustomerAddress;
import com.project.management_system.payload.ApiResponse;
import com.project.management_system.service.CustomerAddressService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customer-addresses")
@RequiredArgsConstructor
public class CustomerAddressController {

    private final CustomerAddressService customerAddressService;

    @GetMapping("/{customerId}")
    public ApiResponse<List<CustomerAddress>> getAddresses(
            @PathVariable Long customerId) {

        return customerAddressService.getAddresses(customerId);
    }

    @PostMapping
    public ApiResponse<CustomerAddress> create(
            @RequestBody CustomerAddress address) {

        return customerAddressService.create(address);
    }
}