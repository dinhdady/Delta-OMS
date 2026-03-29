package com.project.management_system.service;

import com.project.management_system.model.Unit;
import com.project.management_system.payload.ApiResponse;

import java.util.List;

public interface UnitService {

    ApiResponse<List<Unit>> getAll();

    ApiResponse<Unit> create(Unit unit);
}