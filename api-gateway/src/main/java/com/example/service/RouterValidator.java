package com.example.service;

import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Service;

import java.awt.image.MultiResolutionImage;
import java.util.List;
import java.util.function.Predicate;

@Service
public class RouterValidator {

    public static final List<String> openEndPoints = List.of(
            "/auth"
    );

    public Predicate<ServerHttpRequest> isSecured = serverHttpRequest ->
            openEndPoints.stream().noneMatch(uri -> serverHttpRequest.getURI().getPath().contains(uri));
}
