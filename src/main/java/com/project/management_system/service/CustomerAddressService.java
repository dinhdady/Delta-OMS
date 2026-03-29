package com.project.management_system.service;

import com.project.management_system.model.CustomerAddress;
import com.project.management_system.payload.ApiResponse;

import java.util.List;

public interface CustomerAddressService {

    ApiResponse<List<CustomerAddress>> getAddresses(Long customerId);

    ApiResponse<CustomerAddress> create(CustomerAddress address);
}