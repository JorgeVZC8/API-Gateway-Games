package com.example.service;

import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Service;

import java.awt.image.MultiResolutionImage;
import java.util.List;
import java.util.function.Predicate;

@Service
public class RouterValidator {

    //Creamos una lista con los edpoints publicos de nuestra aplicacion
    public static final List<String> openEndPoints = List.of(
            "/v1/auth"
    );

    //Este metodo recibe la peticion HTTP y comprueba la ruta del endpoint al que va dirigida. Devulve true si requiere autenticacion o false en caso contrario
    public Predicate<ServerHttpRequest> isSecured = serverHttpRequest ->
            openEndPoints.stream().noneMatch(uri -> serverHttpRequest.getURI().getPath().contains(uri));
}
