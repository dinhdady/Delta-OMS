package com.project.management_system.service;

import com.project.management_system.model.PaymentMethod;
import com.project.management_system.payload.ApiResponse;

import java.util.List;

public interface PaymentMethodService {

    ApiResponse<List<PaymentMethod>> getAll();

    ApiResponse<PaymentMethod> create(PaymentMethod method);
}