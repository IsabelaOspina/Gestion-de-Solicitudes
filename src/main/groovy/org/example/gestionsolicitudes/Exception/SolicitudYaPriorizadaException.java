package org.example.gestionsolicitudes.Exception;

public class SolicitudYaPriorizadaException extends SolicitudException {

    public SolicitudYaPriorizadaException() {
        super("La prioridad solo se puede asignar una vez cuando la solicitud está en estado REGISTRADA");
    }
}