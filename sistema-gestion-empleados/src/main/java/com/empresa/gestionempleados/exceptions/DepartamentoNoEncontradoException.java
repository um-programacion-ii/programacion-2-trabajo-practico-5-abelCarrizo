package com.empresa.gestionempleados.exceptions;

public class DepartamentoNoEncontradoException extends RuntimeException {
    public DepartamentoNoEncontradoException(String mensaje) {
        super(mensaje);
    }
}
