package com.project.management_system.service;

import com.project.management_system.model.User;
import com.project.management_system.payload.ApiResponse;

import java.util.List;

public interface UserService {

    ApiResponse<List<User>> getUsers();

    ApiResponse<String> deleteUser(Long id);
}