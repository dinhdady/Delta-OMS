package com.project.management_system.service;

import com.project.management_system.dto.request.CustomerRequestDTO;
import com.project.management_system.dto.response.CustomerResponseDTO;
import org.springframework.data.domain.Page;

import java.util.List;

public interface CustomerService {

    CustomerResponseDTO createCustomer(CustomerRequestDTO dto);

    CustomerResponseDTO updateCustomer(Long id, CustomerRequestDTO dto);

    CustomerResponseDTO getCustomerById(Long id);

    List<CustomerResponseDTO> getAllCustomers();

    void deleteCustomer(Long id);
    Page<CustomerResponseDTO> getCustomers(int page, int size, String sortBy);
}
