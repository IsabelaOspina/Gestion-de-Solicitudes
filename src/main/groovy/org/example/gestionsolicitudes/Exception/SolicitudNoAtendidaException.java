package org.example.gestionsolicitudes.Exception;

public class SolicitudNoAtendidaException extends SolicitudException {

    public SolicitudNoAtendidaException() {
        super("Solo se puede cerrar una solicitud atendida");
    }
}