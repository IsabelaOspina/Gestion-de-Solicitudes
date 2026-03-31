package org.example.gestionsolicitudes.Repository;

import static org.junit.jupiter.api.Assertions.*;

import org.example.gestionsolicitudes.Model.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;
import java.util.List;

@DataJpaTest
class SolicitudRepositoryTest {

    @Autowired
    private SolicitudRepository solicitudRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    private Usuario crearUsuario(String correo, Rol rol) {
        Usuario usuario = Usuario.builder()
                .nombreUsuario("Usuario Test")
                .correoElectronico(correo)
                .password("123456")
                .rol(rol)
                .activo(true)
                .build();

        return usuarioRepository.save(usuario);
    }

    private Solicitud crearSolicitud(Usuario solicitante,
                                     EstadoSolicitud estado,
                                     TipoSolicitud tipo,
                                     NivelPrioridad prioridad) {

        Solicitud solicitud = Solicitud.builder()
                .descripcion("Solicitud de prueba")
                .canalOrigen(CanalOrigen.CORREO_ELECTRONICO)
                .tipoSolicitud(tipo)
                .estadoSolicitud(estado)
                .nivelPrioridad(prioridad)
                .solicitante(solicitante)
                .build();

        return solicitudRepository.save(solicitud);
    }

    @Test
    @DisplayName("Debe guardar una solicitud correctamente")
    void guardarSolicitudTest() {
        Usuario usuario = crearUsuario("test@correo.com", Rol.ESTUDIANTE);

        Solicitud solicitud = crearSolicitud(
                usuario,
                EstadoSolicitud.REGISTRADA,
                TipoSolicitud.CONSULTA_ACADEMICA,
                NivelPrioridad.MEDIA
        );

        assertNotNull(solicitud.getIdSolicitud());
        assertNotNull(solicitud.getFechaHoraRegistro());
        assertNotNull(solicitud.getFechaLimite());
    }

    @Test
    @DisplayName("Debe encontrar solicitudes por estado")
    void findByEstadoSolicitudTest() {
        Usuario usuario = crearUsuario("estado@correo.com", Rol.ESTUDIANTE);

        crearSolicitud(usuario, EstadoSolicitud.REGISTRADA, TipoSolicitud.CONSULTA_ACADEMICA, NivelPrioridad.MEDIA);
        crearSolicitud(usuario, EstadoSolicitud.REGISTRADA, TipoSolicitud.HOMOLOGACION, NivelPrioridad.ALTA);

        List<Solicitud> resultados =
                solicitudRepository.findByEstadoSolicitud(EstadoSolicitud.REGISTRADA);

        assertEquals(2, resultados.size());
    }

    @Test
    @DisplayName("Debe encontrar solicitudes por tipo")
    void findByTipoSolicitudTest() {
        Usuario usuario = crearUsuario("tipo@correo.com", Rol.ESTUDIANTE);

        crearSolicitud(usuario, EstadoSolicitud.REGISTRADA, TipoSolicitud.HOMOLOGACION, NivelPrioridad.MEDIA);

        List<Solicitud> resultados =
                solicitudRepository.findByTipoSolicitud(TipoSolicitud.HOMOLOGACION);

        assertEquals(1, resultados.size());
    }

    @Test
    @DisplayName("Debe encontrar solicitudes por prioridad")
    void findByNivelPrioridadTest() {
        Usuario usuario = crearUsuario("prioridad@correo.com", Rol.ESTUDIANTE);

        crearSolicitud(usuario, EstadoSolicitud.REGISTRADA, TipoSolicitud.CONSULTA_ACADEMICA, NivelPrioridad.ALTA);

        List<Solicitud> resultados =
                solicitudRepository.findByNivelPrioridad(NivelPrioridad.ALTA);

        assertEquals(1, resultados.size());
    }

    @Test
    @DisplayName("Debe encontrar solicitudes por solicitante")
    void findBySolicitanteTest() {
        Usuario usuario = crearUsuario("solicitante@correo.com", Rol.ESTUDIANTE);

        crearSolicitud(usuario, EstadoSolicitud.REGISTRADA, TipoSolicitud.CONSULTA_ACADEMICA, NivelPrioridad.MEDIA);

        List<Solicitud> resultados =
                solicitudRepository.findBySolicitante(usuario);

        assertEquals(1, resultados.size());
    }

    @Test
    @DisplayName("Debe encontrar solicitudes por responsable asignado")
    void findByResponsableAsignadoTest() {
        Usuario solicitante = crearUsuario("solicitante2@correo.com", Rol.ESTUDIANTE);
        Usuario responsable = crearUsuario("responsable@correo.com", Rol.ADMINISTRATIVO);

        Solicitud solicitud = crearSolicitud(
                solicitante,
                EstadoSolicitud.REGISTRADA,
                TipoSolicitud.CONSULTA_ACADEMICA,
                NivelPrioridad.MEDIA
        );

        solicitud.setResponsableAsignado(responsable);
        solicitudRepository.save(solicitud);

        List<Solicitud> resultados =
                solicitudRepository.findByResponsableAsignado(responsable);

        assertEquals(1, resultados.size());
    }

    @Test
    @DisplayName("Debe encontrar solicitudes por rango de fechas")
    void findByFechaHoraRegistroBetweenTest() {
        Usuario usuario = crearUsuario("fecha@correo.com", Rol.ESTUDIANTE);

        Solicitud solicitud = crearSolicitud(
                usuario,
                EstadoSolicitud.REGISTRADA,
                TipoSolicitud.CONSULTA_ACADEMICA,
                NivelPrioridad.MEDIA
        );

        LocalDateTime inicio = solicitud.getFechaHoraRegistro().minusMinutes(1);
        LocalDateTime fin = solicitud.getFechaHoraRegistro().plusMinutes(1);

        List<Solicitud> resultados =
                solicitudRepository.findByFechaHoraRegistroBetween(inicio, fin);

        assertFalse(resultados.isEmpty());
    }

    @Test
    @DisplayName("Debe encontrar solicitudes por estado y tipo")
    void findByEstadoSolicitudAndTipoSolicitudTest() {
        Usuario usuario = crearUsuario("combo@correo.com", Rol.ESTUDIANTE);

        crearSolicitud(usuario, EstadoSolicitud.REGISTRADA, TipoSolicitud.HOMOLOGACION, NivelPrioridad.MEDIA);
        crearSolicitud(usuario, EstadoSolicitud.REGISTRADA, TipoSolicitud.CONSULTA_ACADEMICA, NivelPrioridad.MEDIA);

        List<Solicitud> resultados =
                solicitudRepository.findByEstadoSolicitudAndTipoSolicitud(
                        EstadoSolicitud.REGISTRADA,
                        TipoSolicitud.HOMOLOGACION
                );

        assertEquals(1, resultados.size());
    }
}