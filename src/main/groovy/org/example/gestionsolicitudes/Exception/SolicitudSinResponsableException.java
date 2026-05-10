package org.example.gestionsolicitudes.Exception;

public class SolicitudSinResponsableException extends SolicitudException {

    public SolicitudSinResponsableException() {
        super("La solicitud no tiene responsable asignado");
    }
}