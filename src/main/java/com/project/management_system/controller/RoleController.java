package com.project.management_system.controller;

import com.project.management_system.model.Role;
import com.project.management_system.payload.ApiResponse;
import com.project.management_system.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;

    @GetMapping
    public ApiResponse<List<Role>> getRoles() {
        return roleService.getRoles();
    }
}