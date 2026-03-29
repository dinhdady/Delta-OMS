package com.project.management_system.service.impl;

import com.project.management_system.model.Unit;
import com.project.management_system.payload.ApiResponse;
import com.project.management_system.repository.UnitRepository;
import com.project.management_system.service.UnitService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UnitServiceImpl implements UnitService {

    private final UnitRepository unitRepository;

    @Override
    public ApiResponse<List<Unit>> getAll() {

        return ApiResponse.<List<Unit>>builder()
                .status(200)
                .message("Units retrieved")
                .data(unitRepository.findAll())
                .build();
    }

    @Override
    public ApiResponse<Unit> create(Unit unit) {

        return ApiResponse.<Unit>builder()
                .status(201)
                .message("Unit created")
                .data(unitRepository.save(unit))
                .build();
    }
}
