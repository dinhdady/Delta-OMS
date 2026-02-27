package com.project.management_system.service.interfaceService;

import com.project.management_system.dto.request.CustomerRequestDTO;
import com.project.management_system.dto.response.CustomerResponseDTO;

import java.util.List;

public interface CustomerService {

    CustomerResponseDTO createCustomer(CustomerRequestDTO dto);

    CustomerResponseDTO updateCustomer(Long id, CustomerRequestDTO dto);

    CustomerResponseDTO getCustomerById(Long id);

    List<CustomerResponseDTO> getAllCustomers();

    void deleteCustomer(Long id);
}
