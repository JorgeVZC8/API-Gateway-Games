package com.example.service;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class AuthenticationFilter extends AbstractGatewayFilterFactory<AuthenticationFilter.Config> {
    private final RouterValidator routerValidator;
    private final JwtUtils jwtUtils;

    public AuthenticationFilter(RouterValidator routerValidator, JwtUtils jwtUtils){
        super(Config.class);
        this.routerValidator= routerValidator;
        this.jwtUtils = jwtUtils;
    }

    /*Este método recibe la petición y verifica si es válida. En dicho caso la modifica, añadiendo el id del usuario que la realizó a la cabecera de la petición
    y se la pasa al próximo filtro o servicio backend */
    @Override
    public GatewayFilter apply(Config config) {
        return ((exchange, chain) -> {
            var request = exchange.getRequest();
            ServerHttpRequest serverHttpRequest = null;

            //Comprobamos si el endpoint requiere atenticacion
            if(routerValidator.isSecured.test(request)){
                //Comprobamos que la peticion contiene un token
                if(authMissing(request)){
                    return onError(exchange, HttpStatus.UNAUTHORIZED);
                }

                //Extraemos el token
                String authHeader = exchange.getRequest().getHeaders().get(HttpHeaders.AUTHORIZATION).get(0);
                //Comprobamos que sea valido y no haya expirado
                if (authHeader != null && authHeader.startsWith("Bearer ")){
                   authHeader = authHeader.substring(7);
                } else {
                    return onError(exchange, HttpStatus.UNAUTHORIZED);
                }
                if (jwtUtils.isExpired(authHeader)){
                    return onError(exchange, HttpStatus.UNAUTHORIZED);
                }

                //Añadimos el ID del usuario que ha realizado la petición a la cabecera de dicha petición.
                serverHttpRequest = exchange.getRequest()
                        .mutate()
                        .header("userIdRequest", jwtUtils.extractUserId(authHeader).toString())
                        .build();
            }

            return chain.filter(exchange.mutate().request(serverHttpRequest).build());
        });
    }

    //Modifica el estatus de una petición
    private Mono<Void> onError(ServerWebExchange exchange, HttpStatus httpStatus){
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(httpStatus);
        return null;
    }

    //Comprueba si la cabecera de la petición tiene la etiqueta authorization
    private boolean authMissing(ServerHttpRequest request){
        return !request.getHeaders().containsKey("Authorization");
    }

    public static class Config{}
}
