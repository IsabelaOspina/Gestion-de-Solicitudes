package org.example.gestionsolicitudes.config

import javax.crypto.SecretKey
import java.time.Instant

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.Keys
import org.springframework.stereotype.Service
import java.time.temporal.ChronoUnit

import java.nio.charset.StandardCharsets
import java.security.Key

@Service
class JwtService {

    private final String secretKey = "miclaveultrasecreta123ultrasecreta123"

    private SecretKey getKey() {
        return Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8))
    }

    String generarToken(String correo, String rol) {
        return Jwts.builder()
                .setSubject(correo)
                .claim("role", rol)
                .setIssuedAt(new Date())
                .setExpiration(Date.from(Instant.now().plus(1, ChronoUnit.HOURS)))
                .signWith(getKey(), SignatureAlgorithm.HS256)
                .compact()
    }

    String extraerUsername(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject()
    }

    String extraerRol(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .get("role", String.class)
    }
}