package com.empresa.gestionempleados.controladores;

import com.empresa.gestionempleados.exceptions.DepartamentoNoEncontradoException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DepartamentoNoEncontradoException.class)
    public ResponseEntity<String> manejarDepartamentoNoEncontrado(DepartamentoNoEncontradoException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }
}
