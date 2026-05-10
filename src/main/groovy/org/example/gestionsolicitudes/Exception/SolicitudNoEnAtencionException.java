package org.example.gestionsolicitudes.Exception;

public class SolicitudNoEnAtencionException extends SolicitudException {

    public SolicitudNoEnAtencionException() {
        super("Solo se pueden atender solicitudes en estado EN_ATENCION");
    }
}