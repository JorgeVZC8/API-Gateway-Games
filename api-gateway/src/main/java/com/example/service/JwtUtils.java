package com.example.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwt;
import io.jsonwebtoken.Jwts;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

@Service
public class JwtUtils {

    private final String secretKey = "ghuisdtweruitywfkjsfbdgscnvbalsdkjfghfadlsjkfvbjbvsdfghjsdfhjghkasdfukasdsxcvhjkgawsducfshasfgkdk";

    //Este metodo comprueba que el token es válido, es decir, valída la firma del token, decodifica las claims y devulve un objeto Jws<Claims>
    public Claims getClaims(String token){
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    //Este metodo comprueba si el token ha expirado
    public boolean isExpired(String token){
        try{
            return getClaims(token).getExpiration().before(new Date());
        }catch (Exception e){
            return true;
        }
    }

    //Este metodo extrae el userId del token
    public Optional<Integer> extractUserId(String token){
        try{
            return Optional.of(Integer.parseInt(getClaims(token).getSubject()));
        }catch (Exception e){
            return  Optional.empty();
        }
    }
}
