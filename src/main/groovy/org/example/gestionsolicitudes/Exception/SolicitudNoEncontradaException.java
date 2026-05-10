package org.example.gestionsolicitudes.Exception;


public class SolicitudNoEncontradaException extends SolicitudException {

    public SolicitudNoEncontradaException() {
        super("Solicitud no encontrada");
    }
}