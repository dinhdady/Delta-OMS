package com.project.management_system.service;

import com.project.management_system.dto.request.CustomerRequestDTO;
import com.project.management_system.dto.response.CustomerResponseDTO;
import com.project.management_system.mapper.CustomerMapper;
import com.project.management_system.model.Customer;
import com.project.management_system.model.CustomerType;
import com.project.management_system.repository.CustomerRepository;
import com.project.management_system.repository.CustomerTypeRepository;
import com.project.management_system.service.interfaceService.CustomerService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;
    private final CustomerTypeRepository customerTypeRepository;
    private final CustomerMapper customerMapper;

    @Override
    public CustomerResponseDTO createCustomer(CustomerRequestDTO dto) {

        Customer customer = customerMapper.toEntity(dto);

        CustomerType type = customerTypeRepository.findById(dto.getCustomerTypeId())
                .orElseThrow(() -> new RuntimeException("Customer type not found"));

        customer.setCustomerType(type);

        return customerMapper.toDTO(customerRepository.save(customer));
    }

    @Override
    public CustomerResponseDTO updateCustomer(Long id, CustomerRequestDTO dto) {

        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        customer.setName(dto.getName());
        customer.setPhone(dto.getPhone());
        customer.setEmail(dto.getEmail());
        customer.setTaxCode(dto.getTaxCode());

        return customerMapper.toDTO(customerRepository.save(customer));
    }

    @Override
    public CustomerResponseDTO getCustomerById(Long id) {
        return customerMapper.toDTO(
                customerRepository.findById(id)
                        .orElseThrow(() -> new RuntimeException("Customer not found"))
        );
    }

    @Override
    public List<CustomerResponseDTO> getAllCustomers() {
        return customerRepository.findAll()
                .stream()
                .map(customerMapper::toDTO)
                .toList();
    }

    @Override
    public void deleteCustomer(Long id) {
        customerRepository.deleteById(id);
    }
}

