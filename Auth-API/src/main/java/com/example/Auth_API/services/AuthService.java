package com.example.Auth_API.services;

import com.example.Auth_API.common.entities.dtos.TokenResponse;
import com.example.Auth_API.common.entities.dtos.UserRequest;

public interface AuthService {
    TokenResponse createUser(UserRequest userRequest);
}
