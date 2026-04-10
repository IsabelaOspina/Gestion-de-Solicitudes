package org.example.gestionsolicitudes.Controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.gestionsolicitudes.Dtos.CrearUsuarioRequestDTO;
import org.example.gestionsolicitudes.Dtos.LoginRequestDTO;
import org.example.gestionsolicitudes.Dtos.UsuarioResponseDTO;
import org.example.gestionsolicitudes.Model.Rol;
import org.example.gestionsolicitudes.Service.UsuarioService;
import org.example.gestionsolicitudes.config.JwtFilter;
import org.example.gestionsolicitudes.config.JwtService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;

@WebMvcTest(UsuarioController.class)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
@DisplayName("UsuarioController — Tests de integracion web")
class UsuarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UsuarioService usuarioService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private JwtFilter jwtFilter;


    private UsuarioResponseDTO mockUsuarioResponse() {

        UsuarioResponseDTO dto = new UsuarioResponseDTO();
        dto.setIdUsuario(1L);
        dto.setNombreUsuario("Juan");
        dto.setCorreoElectronico("juan@gmail.com");
        dto.setRol(Rol.ESTUDIANTE);

        return dto;
    }

    @Test
    @DisplayName("201 — Crear usuario exitosamente")
    void crearUsuario_exitoso() throws Exception {

        CrearUsuarioRequestDTO request = new CrearUsuarioRequestDTO();
        request.setNombre("Juan");
        request.setCorreo("juan@gmail.com");
        request.setPassword("123456");
        request.setRol(Rol.ESTUDIANTE);

        when(usuarioService.crearUsuario(any()))
                .thenReturn(mockUsuarioResponse());

        mockMvc.perform(post("/usuarios/crear")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nombreUsuario").value("Juan"))
                .andExpect(jsonPath("$.correoElectronico").value("juan@gmail.com"));
    }

    @Test
    @DisplayName("200 — Login exitoso")
    void login_exitoso() throws Exception {

        LoginRequestDTO login = new LoginRequestDTO();
        login.setCorreoElectronico("juan@gmail.com");
        login.setPassword("123456");

        when(usuarioService.autenticacion(anyString(), anyString()))
                .thenReturn("token-falso");

        mockMvc.perform(post("/usuarios/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(login)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("token-falso"));
    }

    @Test
    @DisplayName("401 — Login con credenciales incorrectas")
    void login_credencialesIncorrectas() throws Exception {

        LoginRequestDTO login = new LoginRequestDTO();
        login.setCorreoElectronico("juan@gmail.com");
        login.setPassword("incorrecta");

        when(usuarioService.autenticacion(anyString(), anyString()))
                .thenThrow(new BadCredentialsException("Credenciales incorrectas"));

        mockMvc.perform(post("/usuarios/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(login)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("Credenciales incorrectas"));
    }

    @Test
    @DisplayName("200 — Obtener usuario por ID")
    void obtenerPorId() throws Exception {

        UsuarioResponseDTO dto = new UsuarioResponseDTO();
        dto.setIdUsuario(1L);
        dto.setNombreUsuario("Juan");
        dto.setCorreoElectronico("juan@test.com");

        when(usuarioService.obtenerUsuarioDTO(1L)).thenReturn(dto);

        mockMvc.perform(get("/usuarios/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombreUsuario").value("Juan"))
                .andExpect(jsonPath("$.correoElectronico").value("juan@test.com"));

    }

    @Test
    @DisplayName("200 — Obtener usuarios por rol")
    void obtenerUsuariosPorRol() throws Exception {

        when(usuarioService.obtenerUsuariosPorRol(any(Rol.class)))
                .thenReturn(List.of(mockUsuarioResponse()));

        mockMvc.perform(get("/usuarios/rol/ESTUDIANTE")
                        .with(user("admin").roles("ADMINISTRATIVO")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nombreUsuario").value("Juan"));
    }

    @Test
    @DisplayName("200 — Obtener usuario por correo")
    void obtenerPorCorreo() throws Exception {

        when(usuarioService.obtenerUsuarioPorCorreoDTO(anyString()))
                .thenReturn(mockUsuarioResponse());

        mockMvc.perform(get("/usuarios/correo/juan@gmail.com")
                        .with(user("admin").roles("ADMINISTRATIVO")))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("200 — Validar usuario activo")
    void validarUsuarioActivo() throws Exception {

        doNothing().when(usuarioService).validarUsuarioActivo(anyLong());

        mockMvc.perform(get("/usuarios/1/activo")
                        .with(user("admin").roles("ADMINISTRATIVO")))
                .andExpect(status().isOk())
                .andExpect(content().string("Usuario activo"));
    }
}