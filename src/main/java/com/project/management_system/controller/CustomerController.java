package com.project.management_system.controller;

import com.project.management_system.dto.request.CustomerRequestDTO;
import com.project.management_system.dto.response.CustomerResponseDTO;
import com.project.management_system.payload.ApiResponse;
import com.project.management_system.service.impl.CustomerServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerServiceImpl customerService;

    @PostMapping
    public ApiResponse<CustomerResponseDTO> createCustomer(
            @RequestBody CustomerRequestDTO dto) {
        CustomerResponseDTO customer = customerService.createCustomer(dto);
        return ApiResponse.<CustomerResponseDTO>builder()
                .status(201)
                .message("Customer created successfully!")
                .data(customer)
                .build();
    }

    @PutMapping("/{id}")
    public CustomerResponseDTO updateCustomer(
            @PathVariable Long id,
            @RequestBody CustomerRequestDTO dto) {

        return customerService.updateCustomer(id, dto);
    }

    @GetMapping("/{id}")
    public ApiResponse<CustomerResponseDTO> getCustomerById(@PathVariable Long id) {

        CustomerResponseDTO customer = customerService.getCustomerById(id);

        return ApiResponse.<CustomerResponseDTO>builder()
                .status(200)
                .message("Customer retrieved successfully")
                .data(customer)
                .build();
    }

    @GetMapping
    public List<CustomerResponseDTO> getAllCustomers() {
        return customerService.getAllCustomers();
    }

    @DeleteMapping("/{id}")
    public void deleteCustomer(@PathVariable Long id) {
        customerService.deleteCustomer(id);
    }
    @GetMapping("/pagination")
    public Page<CustomerResponseDTO> getCustomersWithPagination(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "id") String sortBy) {
        
        return customerService.getCustomers(page, size, sortBy);
    }
}
