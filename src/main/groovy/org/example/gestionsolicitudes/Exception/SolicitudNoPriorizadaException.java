package org.example.gestionsolicitudes.Exception;

public class SolicitudNoPriorizadaException extends SolicitudException {

    public SolicitudNoPriorizadaException() {
        super("La solicitud debe ser priorizada antes de asignar responsable");
    }
}
