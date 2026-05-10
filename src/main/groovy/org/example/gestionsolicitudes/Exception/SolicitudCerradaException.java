package org.example.gestionsolicitudes.Exception;

public class SolicitudCerradaException extends SolicitudException {

    public SolicitudCerradaException() {
        super("No se puede realizar la operación porque la solicitud está cerrada");
    }
}