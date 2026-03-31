package org.example.gestionsolicitudes.Repository;
import org.example.gestionsolicitudes.Model.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class HistorialSolicitudesRepositoryTest {

    @Autowired
    private HistorialSolicitudesRepository historialRepository;

    @Autowired
    private SolicitudRepository solicitudRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    private Usuario crearUsuario(String correo) {
        return usuarioRepository.save(
                Usuario.builder()
                        .nombreUsuario("Usuario Test")
                        .correoElectronico(correo)
                        .password("123456")
                        .rol(Rol.ESTUDIANTE)
                        .activo(true)
                        .build()
        );
    }

    private Solicitud crearSolicitud(Usuario usuario) {
        return solicitudRepository.save(
                Solicitud.builder()
                        .descripcion("Solicitud test")
                        .canalOrigen(CanalOrigen.CORREO_ELECTRONICO)
                        .tipoSolicitud(TipoSolicitud.CONSULTA_ACADEMICA)
                        .estadoSolicitud(EstadoSolicitud.REGISTRADA)
                        .nivelPrioridad(NivelPrioridad.MEDIA)
                        .solicitante(usuario)
                        .build()
        );
    }

    private HistorialSolicitud crearHistorial(Solicitud solicitud, String accion) {
        return historialRepository.save(
                HistorialSolicitud.builder()
                        .solicitud(solicitud)
                        .fechaHora(LocalDateTime.now())
                        .accionRealizada(accion)
                        .observaciones("Observación test")
                        .build()
        );
    }

    @Test
    @DisplayName("Debe guardar historial correctamente")
    void guardarHistorialTest() {
        Usuario usuario = crearUsuario("hist1@correo.com");
        Solicitud solicitud = crearSolicitud(usuario);

        HistorialSolicitud historial = crearHistorial(solicitud, "CREADA");

        assertNotNull(historial.getIdHistorial());
        assertEquals("CREADA", historial.getAccionRealizada());
        assertEquals(solicitud.getIdSolicitud(), historial.getSolicitud().getIdSolicitud());
    }

    @Test
    @DisplayName("Debe encontrar historial por id de solicitud")
    void findBySolicitudIdSolicitudTest() {
        Usuario usuario = crearUsuario("hist2@correo.com");
        Solicitud solicitud = crearSolicitud(usuario);

        crearHistorial(solicitud, "CREADA");
        crearHistorial(solicitud, "ACTUALIZADA");

        List<HistorialSolicitud> resultados =
                historialRepository.findBySolicitudIdSolicitud(solicitud.getIdSolicitud());

        assertEquals(2, resultados.size());
    }

    @Test
    @DisplayName("Debe retornar lista vacía si no hay historial para la solicitud")
    void findBySolicitudIdSolicitudVacioTest() {
        List<HistorialSolicitud> resultados =
                historialRepository.findBySolicitudIdSolicitud(999L);

        assertTrue(resultados.isEmpty());
    }
}