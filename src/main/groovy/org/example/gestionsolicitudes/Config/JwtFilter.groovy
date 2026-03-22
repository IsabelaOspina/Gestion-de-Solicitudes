package org.example.gestionsolicitudes.config
import org.example.gestionsolicitudes.config.JwtService
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class JwtFilter extends OncePerRequestFilter {

    package org.example.gestionsolicitudes.config
import org.example.gestionsolicitues.config.JwtService
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class JwtFilter extends OncePerRequestFilter {
    @Autowired
    JwtService jwtService

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) {

        String header = request.getHeader("Authorization")

        if (header == null || !header.startsWith("Bearer ")) {
            filterChain.doFilter(request, response)
            return
        }

        String token = header.substring(7)
        String username = jwtService.extraerUsername(token)

        // Aquí después validaremos el usuario desde base de datos

        filterChain.doFilter(request, response)
    }
}
    JwtService jwtService

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) {

        String header = request.getHeader("Authorization")

        if (header == null || !header.startsWith("Bearer ")) {
            filterChain.doFilter(request, response)
            return
        }

        String token = header.substring(7)
        String username = jwtService.extraerUsername(token)

        // Aquí después validaremos el usuario desde base de datos

        filterChain.doFilter(request, response)
    }
}