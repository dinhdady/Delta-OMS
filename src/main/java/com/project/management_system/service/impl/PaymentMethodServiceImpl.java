package com.project.management_system.service.impl;

import com.project.management_system.model.PaymentMethod;
import com.project.management_system.payload.ApiResponse;
import com.project.management_system.repository.PaymentMethodRepository;
import com.project.management_system.service.PaymentMethodService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentMethodServiceImpl implements PaymentMethodService {

    private final PaymentMethodRepository repository;

    @Override
    public ApiResponse<List<PaymentMethod>> getAll() {

        return ApiResponse.<List<PaymentMethod>>builder()
                .status(200)
                .message("Payment methods retrieved")
                .data(repository.findAll())
                .build();
    }

    @Override
    public ApiResponse<PaymentMethod> create(PaymentMethod method) {

        return ApiResponse.<PaymentMethod>builder()
                .status(201)
                .message("Payment method created")
                .data(repository.save(method))
                .build();
    }
}
