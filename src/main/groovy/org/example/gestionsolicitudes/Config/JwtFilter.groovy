package org.example.gestionsolicitudes.Config


import jakarta.servlet.FilterChain
import jakarta.servlet.ServletException
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.example.gestionsolicitudes.Config.JwtService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class JwtFilter extends OncePerRequestFilter {

    @Autowired
    JwtService jwtService

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String header = request.getHeader("Authorization")

        if (header == null || !header.startsWith("Bearer ")) {
            filterChain.doFilter(request, response)
            return
        }

        String token = header.substring(7)

        try {
            String username = jwtService.extraerUsername(token)

        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED)
            return
        }

        filterChain.doFilter(request, response)
    }
}