package com.example.Auth_API.controllers.impl;

import com.example.Auth_API.common.entities.dtos.TokenResponse;
import com.example.Auth_API.common.entities.dtos.UserRequest;
import com.example.Auth_API.controllers.AuthApi;
import com.example.Auth_API.services.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthController implements AuthApi {

    private final AuthService authService;
    @Override
    public ResponseEntity<TokenResponse> createUser(UserRequest userRequest) {
        return ResponseEntity.ok(authService.createUser(userRequest));
    }

    @Override
    public ResponseEntity<String> getUser(String userId) {
        return ResponseEntity.ok(userId);
    }
}
