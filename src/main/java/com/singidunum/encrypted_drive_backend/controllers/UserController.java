package com.singidunum.encrypted_drive_backend.controllers;

import com.singidunum.encrypted_drive_backend.entities.User;
import com.singidunum.encrypted_drive_backend.services.UserService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/admin")
@AllArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/users")
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }
}
