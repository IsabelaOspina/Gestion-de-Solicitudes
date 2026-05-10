package org.example.gestionsolicitudes.Exception;

import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {


    private ResponseEntity<Object> construirError(
            HttpStatus status,
            String mensaje) {

        Map<String, Object> error = new HashMap<>();

        error.put("timestamp", LocalDateTime.now());
        error.put("status", status.value());
        error.put("error", mensaje);

        return ResponseEntity
                .status(status)
                .body(error);
    }


    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> manejarValidaciones(
            MethodArgumentNotValidException ex) {

        Map<String, Object> response = new HashMap<>();
        Map<String, String> errores = new HashMap<>();

        ex.getBindingResult().getFieldErrors().forEach(error ->
                errores.put(error.getField(), error.getDefaultMessage())
        );

        response.put("timestamp", LocalDateTime.now());
        response.put("status", HttpStatus.BAD_REQUEST.value());
        response.put("errores", errores);

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response);
    }
    @ExceptionHandler(UsuarioYaExisteException.class)
    public ResponseEntity<Object> manejarUsuarioYaExiste(
            UsuarioYaExisteException ex) {

        return construirError(
                HttpStatus.CONFLICT,
                ex.getMessage()
        );
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Object> manejarConstraintViolation(
            ConstraintViolationException ex) {

        return construirError(
                HttpStatus.BAD_REQUEST,
                ex.getMessage()
        );
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<Object> manejarParametrosFaltantes(
            MissingServletRequestParameterException ex) {

        return construirError(
                HttpStatus.BAD_REQUEST,
                "Falta el parámetro requerido: " + ex.getParameterName()
        );
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Object> manejarTipoIncorrecto(
            MethodArgumentTypeMismatchException ex) {

        return construirError(
                HttpStatus.BAD_REQUEST,
                "Valor inválido para el parámetro: " + ex.getName()
        );
    }

    @ExceptionHandler(SolicitudNoEncontradaException.class)
    public ResponseEntity<Object> manejarSolicitudNoEncontrada(
            SolicitudNoEncontradaException ex) {

        return construirError(
                HttpStatus.NOT_FOUND,
                ex.getMessage()
        );
    }

    @ExceptionHandler(SolicitudNoPriorizadaException.class)
    public ResponseEntity<Object> manejarSolicitudNoPriorizada(
            SolicitudNoPriorizadaException ex) {

        return construirError(
                HttpStatus.CONFLICT,
                ex.getMessage()
        );
    }

    @ExceptionHandler(SolicitudYaPriorizadaException.class)
    public ResponseEntity<Object> manejarSolicitudYaPriorizada(
            SolicitudYaPriorizadaException ex) {

        return construirError(
                HttpStatus.CONFLICT,
                ex.getMessage()
        );
    }

    @ExceptionHandler(SolicitudCerradaException.class)
    public ResponseEntity<Object> manejarSolicitudCerrada(
            SolicitudCerradaException ex) {

        return construirError(
                HttpStatus.CONFLICT,
                ex.getMessage()
        );
    }

    @ExceptionHandler(SolicitudNoAtendidaException.class)
    public ResponseEntity<Object> manejarSolicitudNoAtendida(
            SolicitudNoAtendidaException ex) {

        return construirError(
                HttpStatus.CONFLICT,
                ex.getMessage()
        );
    }

    @ExceptionHandler(SolicitudNoEnAtencionException.class)
    public ResponseEntity<Object> manejarSolicitudNoEnAtencion(
            SolicitudNoEnAtencionException ex) {

        return construirError(
                HttpStatus.CONFLICT,
                ex.getMessage()
        );
    }

    @ExceptionHandler(SolicitudSinResponsableException.class)
    public ResponseEntity<Object> manejarSolicitudSinResponsable(
            SolicitudSinResponsableException ex) {

        return construirError(
                HttpStatus.CONFLICT,
                ex.getMessage()
        );
    }


    @ExceptionHandler(UsuarioNoEncontradoException.class)
    public ResponseEntity<Object> manejarUsuarioNoEncontrado(
            UsuarioNoEncontradoException ex) {

        return construirError(
                HttpStatus.NOT_FOUND,
                ex.getMessage()
        );
    }

    @ExceptionHandler(UsuarioInactivoException.class)
    public ResponseEntity<Object> manejarUsuarioInactivo(
            UsuarioInactivoException ex) {

        return construirError(
                HttpStatus.FORBIDDEN,
                ex.getMessage()
        );
    }

    @ExceptionHandler(UsuarioNoAutorizadoException.class)
    public ResponseEntity<Object> manejarUsuarioNoAutorizado(
            UsuarioNoAutorizadoException ex) {

        return construirError(
                HttpStatus.FORBIDDEN,
                ex.getMessage()
        );
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Object> manejarBadCredentials(
            BadCredentialsException ex) {

        return construirError(
                HttpStatus.UNAUTHORIZED,
                "Credenciales incorrectas"
        );
    }


    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<Object> manejarMetodoNoPermitido(
            HttpRequestMethodNotSupportedException ex) {

        return construirError(
                HttpStatus.METHOD_NOT_ALLOWED,
                "Método HTTP no permitido: " + ex.getMethod()
        );
    }


    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Object> manejarIllegalArgument(
            IllegalArgumentException ex) {

        return construirError(
                HttpStatus.BAD_REQUEST,
                ex.getMessage()
        );
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Object> manejarIllegalState(
            IllegalStateException ex) {

        return construirError(
                HttpStatus.CONFLICT,
                ex.getMessage()
        );
    }

    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<Object> manejarNullPointer(
            NullPointerException ex) {

        return construirError(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Se produjo un error interno de datos nulos"
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> manejarGeneral(Exception ex) {

        return construirError(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Error interno del servidor"
        );
    }
}