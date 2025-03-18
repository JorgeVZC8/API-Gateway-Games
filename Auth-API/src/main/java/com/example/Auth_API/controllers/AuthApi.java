package com.example.Auth_API.controllers;

import com.example.Auth_API.common.entities.constants.ApiPathconstants;
import com.example.Auth_API.common.entities.dtos.TokenResponse;
import com.example.Auth_API.common.entities.dtos.UserRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping(ApiPathconstants.V1_ROUTE+ApiPathconstants.Auth_ROUTE)
public interface AuthApi {

    @PostMapping(value = "/register")
    ResponseEntity<TokenResponse> createUser(@RequestBody @Valid UserRequest userRequest);

    @GetMapping
    ResponseEntity<String> getUser(@RequestAttribute(name= "X-User-Id") String userId);
}
