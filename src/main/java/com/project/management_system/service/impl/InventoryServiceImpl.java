package com.project.management_system.service.impl;

import com.project.management_system.model.InventoryLog;
import com.project.management_system.payload.ApiResponse;
import com.project.management_system.repository.InventoryLogRepository;
import com.project.management_system.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class InventoryServiceImpl implements InventoryService {

    private final InventoryLogRepository repository;

    @Override
    public ApiResponse<List<InventoryLog>> getLogs() {

        return ApiResponse.<List<InventoryLog>>builder()
                .status(200)
                .message("Inventory logs retrieved")
                .data(repository.findAll())
                .build();
    }

    @Override
    public ApiResponse<InventoryLog> addLog(InventoryLog log) {

        return ApiResponse.<InventoryLog>builder()
                .status(201)
                .message("Inventory log added")
                .data(repository.save(log))
                .build();
    }
}