package com.project.management_system.service.impl;

import com.project.management_system.model.Role;
import com.project.management_system.payload.ApiResponse;
import com.project.management_system.repository.RoleRepository;
import com.project.management_system.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;

    @Override
    public ApiResponse<List<Role>> getRoles() {

        return ApiResponse.<List<Role>>builder()
                .status(200)
                .message("Roles retrieved")
                .data(roleRepository.findAll())
                .build();
    }
}
