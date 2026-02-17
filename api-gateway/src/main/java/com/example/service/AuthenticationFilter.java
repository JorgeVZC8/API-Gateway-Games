package com.example.service;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class AuthenticationFilter extends AbstractGatewayFilterFactory<AuthenticationFilter.Config> {

    private final RouterValidator routerValidator;
    private final JwtUtils jwtUtils;

    public AuthenticationFilter(RouterValidator routerValidator, JwtUtils jwtUtils) {
        super(Config.class);
        this.routerValidator = routerValidator;
        this.jwtUtils = jwtUtils;
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            ServerHttpRequest mutatedRequest = request; // ✅ por defecto, la request original

            // Si la ruta es segura, exigimos JWT
            if (routerValidator.isSecured.test(request)) {

                String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

                // ✅ Sin Authorization o formato incorrecto => 401
                if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                    return onError(exchange, HttpStatus.UNAUTHORIZED);
                }

                String token = authHeader.substring(7).trim();

                // ✅ Token expirado/ inválido => 401
                if (token.isEmpty() || jwtUtils.isExpired(token)) {
                    return onError(exchange, HttpStatus.UNAUTHORIZED);
                }

                // ✅ Añadimos userId si existe
                Integer userId = jwtUtils.extractUserId(token).orElse(null);
                if (userId != null) {
                    mutatedRequest = request.mutate()
                            .header("userIdRequest", String.valueOf(userId))
                            .build();
                }
            }

            return chain.filter(exchange.mutate().request(mutatedRequest).build());
        };
    }

    private Mono<Void> onError(ServerWebExchange exchange, HttpStatus status) {
        exchange.getResponse().setStatusCode(status);
        return exchange.getResponse().setComplete(); // ✅ nunca null
    }

    public static class Config {}
}
