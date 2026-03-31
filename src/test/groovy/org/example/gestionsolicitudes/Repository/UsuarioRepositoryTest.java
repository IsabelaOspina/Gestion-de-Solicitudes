package org.example.gestionsolicitudes.Repository;

import org.example.gestionsolicitudes.Model.Rol;
import org.example.gestionsolicitudes.Model.Usuario;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class UsuarioRepositoryTest {

    @Autowired
    private UsuarioRepository usuarioRepository;

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

    @Test
    @DisplayName("Debe guardar un usuario correctamente")
    void guardarUsuarioTest() {
        Usuario usuario = crearUsuario("test1@correo.com", Rol.ESTUDIANTE);

        assertNotNull(usuario.getIdUsuario());
        assertEquals("test1@correo.com", usuario.getCorreoElectronico());
        assertEquals("Test User", usuario.getNombreUsuario());
        assertEquals(Rol.ESTUDIANTE, usuario.getRol());
        assertTrue(usuario.getActivo());
    }

    @Test
    @DisplayName("Debe encontrar usuario por correo")
    void findByCorreoElectronicoTest() {
        crearUsuario("test2@correo.com", Rol.DOCENTE);

        Optional<Usuario> resultado =
                usuarioRepository.findByCorreoElectronico("test2@correo.com");

        assertTrue(resultado.isPresent());

        Usuario usuario = resultado.get();
        assertEquals("test2@correo.com", usuario.getCorreoElectronico());
        assertEquals(Rol.DOCENTE, usuario.getRol());
        assertTrue(usuario.getActivo());
    }

    @Test
    @DisplayName("Debe retornar vacío si el correo no existe")
    void findByCorreoNoExisteTest() {
        Optional<Usuario> resultado =
                usuarioRepository.findByCorreoElectronico("noexiste@correo.com");

        assertFalse(resultado.isPresent());
    }

    @Test
    @DisplayName("Debe validar existencia por correo")
    void existsByCorreoElectronicoTest() {
        crearUsuario("test3@correo.com", Rol.ESTUDIANTE);

        boolean existe =
                usuarioRepository.existsByCorreoElectronico("test3@correo.com");

        assertTrue(existe);
    }

    @Test
    @DisplayName("Debe retornar false si el correo no existe")
    void existsByCorreoElectronicoFalseTest() {
        boolean existe =
                usuarioRepository.existsByCorreoElectronico("fake@correo.com");

        assertFalse(existe);
    }

    @Test
    @DisplayName("Debe encontrar usuarios por rol")
    void findByRolTest() {
        crearUsuario("admin1@correo.com", Rol.ADMINISTRATIVO);
        crearUsuario("admin2@correo.com", Rol.ADMINISTRATIVO);
        crearUsuario("user@correo.com", Rol.ESTUDIANTE);

        List<Usuario> admins =
                usuarioRepository.findByRol(Rol.ADMINISTRATIVO);

        assertEquals(2, admins.size());

        admins.forEach(usuario ->
                assertEquals(Rol.ADMINISTRATIVO, usuario.getRol())
        );
    }

    @Test
    @DisplayName("No debería permitir correos duplicados")
    void noDebePermitirCorreosDuplicados() {
        crearUsuario("dup@correo.com", Rol.ESTUDIANTE);

        assertThrows(Exception.class, () -> {
            crearUsuario("dup@correo.com", Rol.DOCENTE);
        });
    }
}