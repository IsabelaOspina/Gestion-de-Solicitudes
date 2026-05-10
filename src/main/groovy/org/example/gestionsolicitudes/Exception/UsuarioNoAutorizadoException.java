package org.example.gestionsolicitudes.Exception;

public class UsuarioNoAutorizadoException extends RuntimeException {

    public UsuarioNoAutorizadoException() {
        super("El usuario no tiene permisos para realizar esta acción");
    }
}