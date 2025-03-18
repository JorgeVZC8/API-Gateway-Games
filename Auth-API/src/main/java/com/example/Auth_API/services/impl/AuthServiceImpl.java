package com.example.Auth_API.services.impl;

import com.example.Auth_API.common.entities.UserModel;
import com.example.Auth_API.common.entities.dtos.TokenResponse;
import com.example.Auth_API.common.entities.dtos.UserRequest;
import com.example.Auth_API.repositories.UserRepository;
import com.example.Auth_API.services.AuthService;
import com.example.Auth_API.services.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRespository;
    private final JwtService jwtService;

    @Override
    public TokenResponse createUser(UserRequest userRequest) {
        return Optional.of(userRequest)
                .map(this::mapToEntity)
                .map(userRespository::save)
                .map(userCreated -> jwtService.generateToken(userCreated.getId()))
                .orElseThrow(() -> new RuntimeException("Error creating user"));
    }

    private UserModel mapToEntity(UserRequest userRequest) {
        return UserModel.builder()
                .email(userRequest.getEmail())
                .password(userRequest.getPassword())
                .role("USER")
                .build();
    }
}
