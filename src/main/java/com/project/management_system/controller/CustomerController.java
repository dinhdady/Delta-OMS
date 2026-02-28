package com.project.management_system.controller;

import com.project.management_system.dto.request.CustomerRequestDTO;
import com.project.management_system.dto.response.CustomerResponseDTO;
import com.project.management_system.service.CustomerServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerServiceImpl customerService;

    @PostMapping
    public CustomerResponseDTO createCustomer(
            @RequestBody CustomerRequestDTO dto) {
        return customerService.createCustomer(dto);
    }

    @PutMapping("/{id}")
    public CustomerResponseDTO updateCustomer(
            @PathVariable Long id,
            @RequestBody CustomerRequestDTO dto) {
        return customerService.updateCustomer(id, dto);
    }

    @GetMapping("/{id}")
    public CustomerResponseDTO getCustomerById(
            @PathVariable Long id) {
        return customerService.getCustomerById(id);
    }

    @GetMapping
    public List<CustomerResponseDTO> getAllCustomers() {
        return customerService.getAllCustomers();
    }

    @DeleteMapping("/{id}")
    public void deleteCustomer(@PathVariable Long id) {
        customerService.deleteCustomer(id);
    }
}
