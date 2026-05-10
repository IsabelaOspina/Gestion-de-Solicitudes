package org.example.gestionsolicitudes.Exception;

public class UsuarioNoEncontradoException extends RuntimeException {

    public UsuarioNoEncontradoException() {
        super("Usuario no encontrado");
    }
}