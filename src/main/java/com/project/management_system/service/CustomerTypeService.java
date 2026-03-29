package com.project.management_system.service;

import com.project.management_system.model.CustomerType;
import com.project.management_system.payload.ApiResponse;

import java.util.List;

public interface CustomerTypeService {

    ApiResponse<List<CustomerType>> getAll();

    ApiResponse<CustomerType> create(CustomerType type);
}