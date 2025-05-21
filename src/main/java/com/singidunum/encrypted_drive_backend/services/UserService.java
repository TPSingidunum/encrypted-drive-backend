package com.singidunum.encrypted_drive_backend.services;

import com.singidunum.encrypted_drive_backend.configs.exceptions.CustomException;
import com.singidunum.encrypted_drive_backend.configs.exceptions.ErrorCode;
import com.singidunum.encrypted_drive_backend.dtos.UserRegisterDto;
import com.singidunum.encrypted_drive_backend.entities.User;
import com.singidunum.encrypted_drive_backend.repositories.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class UserService {

    public final UserRepository userRepository;
    public PasswordEncoder passwordEncoder;

    public User registerUser(UserRegisterDto data) {
        User newUser = new User();
        newUser.setEmail(data.getEmail());
        newUser.setUsername(data.getUsername());
        newUser.setPassword(passwordEncoder.encode(data.getPassword()));

        return userRepository.save(newUser);
    }

    public Optional<User> getUserByUsername(String username) {
        try {
            return userRepository.findByUsername(username);
        } catch (Exception e) {
           throw new CustomException(
                   e.getMessage(),
                   HttpStatus.BAD_REQUEST,
                   ErrorCode.BASE_ERROR
           );
        }
    }

    public List<User> getAllUsers() {
        List<User> users = userRepository.findAll();
        for(User u: users) {
            u.setWorkspaces(null);
        }
        return users;
    }

    public List<User> getAllUsersWithWorkspaces() {
        return userRepository.findAll();
    }

    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }
}
