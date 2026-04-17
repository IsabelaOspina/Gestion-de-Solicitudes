package org.example.gestionsolicitudes.config
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
                            .requestMatchers("/usuarios/crear", "/usuarios/login").permitAll()

                    // Restricciones por rol
                            .requestMatchers("/solicitudes/clasificar/**").hasRole("ADMINISTRATIVO")
                            .requestMatchers("/solicitudes/priorizar/**").hasRole("ADMINISTRATIVO")
                            .requestMatchers("/solicitudes/cerrar/**").hasRole("ADMINISTRATIVO")
                            .requestMatchers("/solicitudes/estado/**").hasRole("ADMINISTRATIVO")
                            .requestMatchers("/solicitudes/tipo/**").hasRole("ADMINISTRATIVO")
                            .requestMatchers("/solicitudes/prioridad/**").hasRole("ADMINISTRATIVO")
                            .requestMatchers("/solicitudes/responsable/**").hasRole("ADMINISTRATIVO")
                            .requestMatchers("/solicitudes/rango").hasRole("ADMINISTRATIVO")
                            .requestMatchers("/solicitudes/estado-tipo").hasRole("ADMINISTRATIVO")
                            .requestMatchers("/solicitudes/solicitante/**").hasRole("ADMINISTRATIVO")
                            .requestMatchers("/historial-solicitudes/**").hasRole("ADMINISTRATIVO")
                            .requestMatchers("/usuarios/{id}").hasRole("ADMINISTRATIVO")
                            .requestMatchers("/usuarios/rol/**").hasRole("ADMINISTRATIVO")
                            .requestMatchers("/usuarios/correo/**").hasRole("ADMINISTRATIVO")
                            .requestMatchers("/usuarios/{id}/activo").hasRole("ADMINISTRATIVO")


                    // Estudiantes y docentes pueden crear solicitudes
                            .requestMatchers("/solicitudes/registrar").hasAnyRole("ESTUDIANTE", "DOCENTE")

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