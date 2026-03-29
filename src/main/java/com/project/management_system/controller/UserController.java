package com.project.management_system.controller;

import com.project.management_system.model.User;
import com.project.management_system.payload.ApiResponse;
import com.project.management_system.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    public ApiResponse<List<User>> getUsers() {
        return userService.getUsers();
    }

    @DeleteMapping("/{id}")
    public ApiResponse<String> deleteUser(@PathVariable Long id) {
        return userService.deleteUser(id);
    }
}