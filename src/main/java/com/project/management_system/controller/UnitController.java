package com.project.management_system.controller;

import com.project.management_system.model.Unit;
import com.project.management_system.payload.ApiResponse;
import com.project.management_system.service.UnitService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/units")
@RequiredArgsConstructor
public class UnitController {

    private final UnitService unitService;

    @GetMapping
    public ApiResponse<List<Unit>> getAll() {
        return unitService.getAll();
    }

    @PostMapping
    public ApiResponse<Unit> create(@RequestBody Unit unit) {
        return unitService.create(unit);
    }
}