package org.example.gestionsolicitudes.Config


import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import org.springframework.stereotype.Service

@Service
class JwtService {

    private final String SECRET_KEY = "clave_secreta"

    String generarToken(String username, String rol) {
        Jwts.builder()
                .setSubject(username)
                .claim("rol", rol)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60))
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact()
    }

    String extraerUsername(String token) {
        Jwts.parser()
                .setSigningKey(SECRET_KEY)
                .parseClaimsJws(token)
                .body
                .subject
    }

    boolean esValido(String token, String username) {
        extraerUsername(token) == username
    }
}