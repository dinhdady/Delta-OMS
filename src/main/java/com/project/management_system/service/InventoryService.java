package com.project.management_system.service;

import com.project.management_system.model.InventoryLog;
import com.project.management_system.payload.ApiResponse;

import java.util.List;

public interface InventoryService {

    ApiResponse<List<InventoryLog>> getLogs();

    ApiResponse<InventoryLog> addLog(InventoryLog log);
}
