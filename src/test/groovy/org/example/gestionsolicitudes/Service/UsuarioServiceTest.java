package org.example.gestionsolicitudes.Service;

import org.example.gestionsolicitudes.Dtos.CrearUsuarioRequestDTO;
import org.example.gestionsolicitudes.Dtos.UsuarioResponseDTO;
import org.example.gestionsolicitudes.Mapper.UsuarioMapper;
import org.example.gestionsolicitudes.Model.Rol;
import org.example.gestionsolicitudes.Model.Usuario;
import org.example.gestionsolicitudes.Repository.UsuarioRepository;
import org.example.gestionsolicitudes.config.JwtService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UsuarioService — Tests unitarios")
class UsuarioServiceTest {

    @Mock private UsuarioRepository usuarioRepository;
    @Mock private UsuarioMapper usuarioMapper;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private JwtService jwtService;

    @InjectMocks private UsuarioService usuarioService;

    private Usuario usuario;

    @BeforeEach
    void setUp() {
        usuario = Usuario.builder()
                .idUsuario(1L)
                .nombreUsuario("Test User")
                .correoElectronico("test@correo.com")
                .password("encodedPassword")
                .rol(Rol.ESTUDIANTE)
                .activo(true)
                .build();
    }

    @Nested
    @DisplayName("Crear usuario")
    class CrearUsuarioTests {

        @Test
        @DisplayName("Debe crear usuario correctamente")
        void crear_exitoso() {
            CrearUsuarioRequestDTO dto = new CrearUsuarioRequestDTO();
            dto.setCorreo("test@correo.com");
            dto.setPassword("123456");

            when(usuarioRepository.existsByCorreoElectronico(dto.getCorreo())).thenReturn(false);
            when(usuarioMapper.aEntidad(dto)).thenReturn(usuario);
            when(passwordEncoder.encode(dto.getPassword())).thenReturn("encodedPassword");
            when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);
            when(usuarioMapper.aResponseDTO(usuario)).thenReturn(new UsuarioResponseDTO());

            UsuarioResponseDTO res = usuarioService.crearUsuario(dto);

            assertThat(res).isNotNull();
            verify(usuarioRepository).save(any(Usuario.class));
        }

        @Test
        @DisplayName("Debe fallar si el correo ya existe")
        void crear_correoDuplicado() {
            CrearUsuarioRequestDTO dto = new CrearUsuarioRequestDTO();
            dto.setCorreo("test@correo.com");

            when(usuarioRepository.existsByCorreoElectronico(dto.getCorreo())).thenReturn(true);

            assertThatThrownBy(() -> usuarioService.crearUsuario(dto))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("correo");
        }
    }

    @Nested
    @DisplayName("Autenticación")
    class AuthTests {

        @Test
        @DisplayName("Debe autenticar correctamente")
        void autenticacion_exitoso() {
            when(usuarioRepository.findByCorreoElectronico("test@correo.com"))
                    .thenReturn(Optional.of(usuario));
            when(passwordEncoder.matches("123456", usuario.getPassword())).thenReturn(true);
            when(jwtService.generarToken(any(), any())).thenReturn("token123");

            String token = usuarioService.autenticacion("test@correo.com", "123456");

            assertThat(token).isEqualTo("token123");
        }

        @Test
        @DisplayName("Debe fallar si el usuario no existe")
        void autenticacion_usuarioNoExiste() {
            when(usuarioRepository.findByCorreoElectronico("fake@correo.com"))
                    .thenReturn(Optional.empty());

            assertThatThrownBy(() ->
                    usuarioService.autenticacion("fake@correo.com", "123456"))
                    .isInstanceOf(BadCredentialsException.class);
        }

        @Test
        @DisplayName("Debe fallar si la contraseña es incorrecta")
        void autenticacion_passwordIncorrecto() {
            when(usuarioRepository.findByCorreoElectronico("test@correo.com"))
                    .thenReturn(Optional.of(usuario));
            when(passwordEncoder.matches("wrong", usuario.getPassword())).thenReturn(false);

            assertThatThrownBy(() ->
                    usuarioService.autenticacion("test@correo.com", "wrong"))
                    .isInstanceOf(BadCredentialsException.class);
        }
    }

    @Nested
    @DisplayName("Consultas")
    class QueryTests {

        @Test
        @DisplayName("obtenerPorId exitoso")
        void obtenerPorId() {
            when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));

            Usuario res = usuarioService.obtenerPorId(1L);

            assertThat(res).isNotNull();
            assertThat(res.getCorreoElectronico()).isEqualTo("test@correo.com");
        }

        @Test
        @DisplayName("obtenerPorId no encontrado")
        void obtenerPorId_noExiste() {
            when(usuarioRepository.findById(1L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> usuarioService.obtenerPorId(1L))
                    .isInstanceOf(RuntimeException.class);
        }

        @Test
        @DisplayName("obtenerPorCorreo exitoso")
        void obtenerPorCorreo() {
            when(usuarioRepository.findByCorreoElectronico("test@correo.com"))
                    .thenReturn(Optional.of(usuario));

            Usuario res = usuarioService.obtenerPorCorreo("test@correo.com");

            assertThat(res).isNotNull();
        }

        @Test
        @DisplayName("obtenerUsuariosPorRol")
        void obtenerPorRol() {
            when(usuarioRepository.findByRol(Rol.ESTUDIANTE)).thenReturn(List.of(usuario));
            when(usuarioMapper.aDTOLista(any())).thenReturn(List.of(new UsuarioResponseDTO()));

            List<UsuarioResponseDTO> res =
                    usuarioService.obtenerUsuariosPorRol(Rol.ESTUDIANTE);

            assertThat(res).hasSize(1);
        }
    }

    @Nested
    @DisplayName("Validaciones de usuario activo")
    class ActivoTests {

        @Test
        @DisplayName("Debe retornar usuario activo")
        void usuarioActivo() {
            when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));

            Usuario res = usuarioService.obtenerUsuarioActivo(1L);

            assertThat(res.getActivo()).isTrue();
        }

        @Test
        @DisplayName("Debe fallar si usuario está inactivo")
        void usuarioInactivo() {
            usuario.setActivo(false);
            when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));

            assertThatThrownBy(() ->
                    usuarioService.obtenerUsuarioActivo(1L))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("inactivo");
        }
    }
}