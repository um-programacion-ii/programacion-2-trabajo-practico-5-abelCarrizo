package com.empresa.gestionempleados.servicios;

import com.empresa.gestionempleados.entidades.Empleado;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface EmpleadoService {
    Empleado guardar(Empleado empleado);
    Empleado buscarPorId(Long id);
    List<Empleado> buscarPorDepartamento(String nombreDepartamento);
    List<Empleado> buscarPorRangoSalario(BigDecimal salarioMin, BigDecimal salarioMax);
    BigDecimal obtenerSalarioPromedioPorDepartamento(Long departamentoId);
    List<Empleado> buscarPorFechaContratacionPosteriorA(LocalDate fecha);
    List<Empleado> obtenerTodos();
    Empleado actualizar(Long id, Empleado empleado);
    void eliminar(Long id);
}