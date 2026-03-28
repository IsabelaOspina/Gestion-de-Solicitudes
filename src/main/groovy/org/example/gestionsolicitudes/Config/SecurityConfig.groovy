package org.example.gestionsolicitudes.Config

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.*
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

@Configuration
class SecurityConfig {

    @Autowired
    JwtFilter jwtFilter

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .csrf { it.disable() }
                .authorizeHttpRequests {
                    it
                    // Endpoints públicos
                            .requestMatchers("/auth/**").permitAll()

                    // Restricciones por rol
                            .requestMatchers("/solicitudes/clasificar/**").hasRole("ADMINISTRATIVO")
                            .requestMatchers("/solicitudes/priorizar/**").hasRole("ADMINISTRATIVO")
                            .requestMatchers("/solicitudes/cerrar/**").hasRole("ADMINISTRATIVO")

                    // Estudiantes y docentes pueden crear solicitudes
                            .requestMatchers("/solicitudes/nueva/**").hasAnyRole("ESTUDIANTE", "DOCENTE")

                    // Cualquier otra accion requiere autenticación
                            .anyRequest().authenticated()
                }
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter)

        return http.build()
    }



    @Bean
    PasswordEncoder passwordEncoder() {
        new BCryptPasswordEncoder()
    }
}