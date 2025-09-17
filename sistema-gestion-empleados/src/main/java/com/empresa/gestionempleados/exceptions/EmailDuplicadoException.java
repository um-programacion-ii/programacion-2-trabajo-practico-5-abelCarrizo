package com.empresa.gestionempleados.exceptions;

public class EmailDuplicadoException extends RuntimeException {
    public EmailDuplicadoException(String mensaje) {
        super(mensaje);
    }
}