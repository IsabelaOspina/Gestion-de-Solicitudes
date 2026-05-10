package org.example.gestionsolicitudes.Exception;

public class UsuarioInactivoException extends RuntimeException {

    public UsuarioInactivoException() {
        super("El usuario no se encuentra activo");
    }
}