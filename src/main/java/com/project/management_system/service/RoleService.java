package com.project.management_system.service;

import com.project.management_system.model.Role;
import com.project.management_system.payload.ApiResponse;

import java.util.List;

public interface RoleService {
    ApiResponse<List<Role>> getRoles();
}
