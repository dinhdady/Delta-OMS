package com.project.management_system.service.impl;

import com.project.management_system.model.User;
import com.project.management_system.payload.ApiResponse;
import com.project.management_system.repository.UserRepository;
import com.project.management_system.service.UserService;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public ApiResponse<List<User>> getUsers() {

        return ApiResponse.<List<User>>builder()
                .status(200)
                .message("Users retrieved")
                .data(userRepository.findAll())
                .build();
    }

    @Override
    public ApiResponse<String> deleteUser(Long id) {

        userRepository.deleteById(id);

        return ApiResponse.<String>builder()
                .status(200)
                .message("User deleted")
                .data("Deleted")
                .build();
    }
}
