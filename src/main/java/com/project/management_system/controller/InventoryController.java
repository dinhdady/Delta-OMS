package com.project.management_system.controller;

import com.project.management_system.model.InventoryLog;
import com.project.management_system.payload.ApiResponse;
import com.project.management_system.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;

    @GetMapping
    public ApiResponse<List<InventoryLog>> getLogs() {
        return inventoryService.getLogs();
    }

    @PostMapping
    public ApiResponse<InventoryLog> addLog(
            @RequestBody InventoryLog log) {

        return inventoryService.addLog(log);
    }
}