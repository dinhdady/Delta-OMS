package com.project.management_system.controller;

import com.project.management_system.model.PaymentMethod;
import com.project.management_system.payload.ApiResponse;
import com.project.management_system.service.PaymentMethodService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payment-methods")
@RequiredArgsConstructor
public class PaymentMethodController {

    private final PaymentMethodService paymentMethodService;

    @GetMapping
    public ApiResponse<List<PaymentMethod>> getAll() {
        return paymentMethodService.getAll();
    }

    @PostMapping
    public ApiResponse<PaymentMethod> create(
            @RequestBody PaymentMethod method) {

        return paymentMethodService.create(method);
    }
}