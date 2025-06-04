package com.singidunum.encrypted_drive_backend.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.singidunum.encrypted_drive_backend.configs.exceptions.CustomException;
import com.singidunum.encrypted_drive_backend.configs.exceptions.ErrorCode;
import com.singidunum.encrypted_drive_backend.configs.mapper.MapperConfig;
import com.singidunum.encrypted_drive_backend.configs.security.JwtClaims;
import com.singidunum.encrypted_drive_backend.dtos.PublicKeyDto;
import com.singidunum.encrypted_drive_backend.dtos.UserDto;
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
    public final PasswordEncoder passwordEncoder;
    public final JwtClaims jwtClaims;
    public final MapperConfig mapperConfig;

    public User registerUser(UserRegisterDto data) {
        User newUser = new User();
        newUser.setEmail(data.getEmail());
        newUser.setUsername(data.getUsername());
        newUser.setPassword(passwordEncoder.encode(data.getPassword()));

        try {
            return userRepository.save(newUser);
        } catch (Exception e) {
            throw new CustomException(
                    e.getMessage(),
                    HttpStatus.BAD_REQUEST,
                    ErrorCode.BASE_ERROR
            );
        }    }

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

    public String getPublicKey() {
        Optional<User> user = userRepository.findByUsername(jwtClaims.getUsername());

        if (user.isEmpty()) {
            throw new CustomException(
                    "User not found",
                    HttpStatus.BAD_REQUEST,
                    ErrorCode.USER_NOT_EXIST
            );
        }

        return user.get().getPublicKey();
    }

    public UserDto getUserInformation() {
        Optional<User> user = userRepository.findByUsername(jwtClaims.getUsername());

        if (user.isEmpty()) {
            throw new CustomException(
                    "User not found",
                    HttpStatus.BAD_REQUEST,
                    ErrorCode.USER_NOT_EXIST
            );
        }

        return mapperConfig.modelMapper().map(user.get(), UserDto.class);
    }


    public void setPublicKey(PublicKeyDto data) {
        Optional<User> user = userRepository.findByUsername(jwtClaims.getUsername());

        if (user.isEmpty()) {
            throw new CustomException(
                    "User not found",
                    HttpStatus.BAD_REQUEST,
                    ErrorCode.USER_NOT_EXIST
            );
        }

        user.get().setPublicKey(data.getPublicKey());
        userRepository.save(user.get());
    }
}
