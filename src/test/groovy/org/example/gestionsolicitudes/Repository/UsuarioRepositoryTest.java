package org.example.gestionsolicitudes.Repository;

import org.example.gestionsolicitudes.Model.Rol;
import org.example.gestionsolicitudes.Model.Usuario;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
@DisplayName("UsuarioRepository — Tests de persistencia")
class UsuarioRepositoryTest {

    @Autowired
    private UsuarioRepository usuarioRepository;

    private Usuario usuarioBase;

    @BeforeEach
    void setUp() {
        usuarioBase = usuarioRepository.save(
                Usuario.builder()
                        .nombreUsuario("Test User")
                        .correoElectronico("base@correo.com")
                        .password("123456")
                        .rol(Rol.ESTUDIANTE)
                        .activo(true)
                        .build()
        );
    }

    // 🔹 Helper
    private Usuario crearUsuario(String correo, Rol rol) {
        return usuarioRepository.save(
                Usuario.builder()
                        .nombreUsuario("Test User")
                        .correoElectronico(correo)
                        .password("123456")
                        .rol(rol)
                        .activo(true)
                        .build()
        );
    }

    @Nested
    @DisplayName("CRUD básico")
    class CrudTests {

        @Test
        @DisplayName("Guardar usuario correctamente")
        void guardarUsuario() {
            Usuario usuario = crearUsuario("test1@correo.com", Rol.ESTUDIANTE);

            assertThat(usuario.getIdUsuario()).isNotNull();
            assertThat(usuario.getCorreoElectronico()).isEqualTo("test1@correo.com");
            assertThat(usuario.getNombreUsuario()).isEqualTo("Test User");
            assertThat(usuario.getRol()).isEqualTo(Rol.ESTUDIANTE);
            assertThat(usuario.getActivo()).isTrue();
        }
    }

    @Nested
    @DisplayName("Consultas")
    class QueryTests {

        @Test
        @DisplayName("findByCorreoElectronico existente")
        void findByCorreo() {
            Optional<Usuario> resultado =
                    usuarioRepository.findByCorreoElectronico("base@correo.com");

            assertThat(resultado).isPresent();
            assertThat(resultado.get().getRol()).isEqualTo(Rol.ESTUDIANTE);
        }

        @Test
        @DisplayName("findByCorreoElectronico inexistente")
        void findByCorreoNoExiste() {
            Optional<Usuario> resultado =
                    usuarioRepository.findByCorreoElectronico("noexiste@correo.com");

            assertThat(resultado).isEmpty();
        }

        @Test
        @DisplayName("existsByCorreoElectronico true/false")
        void existsByCorreo() {
            assertThat(usuarioRepository.existsByCorreoElectronico("base@correo.com")).isTrue();
            assertThat(usuarioRepository.existsByCorreoElectronico("fake@correo.com")).isFalse();
        }

        @Test
        @DisplayName("findByRol retorna solo ese rol")
        void findByRol() {
            crearUsuario("admin1@correo.com", Rol.ADMINISTRATIVO);
            crearUsuario("admin2@correo.com", Rol.ADMINISTRATIVO);

            List<Usuario> admins =
                    usuarioRepository.findByRol(Rol.ADMINISTRATIVO);

            assertThat(admins).hasSize(2);
            assertThat(admins)
                    .extracting(Usuario::getRol)
                    .containsOnly(Rol.ADMINISTRATIVO);
        }
    }

    @Nested
    @DisplayName("Restricciones")
    class ConstraintTests {

        @Test
        @DisplayName("No debe permitir correos duplicados")
        void noCorreosDuplicados() {

            assertThatThrownBy(() ->
                    crearUsuario("base@correo.com", Rol.DOCENTE)
            ).isInstanceOf(DataIntegrityViolationException.class);
        }
    }
}