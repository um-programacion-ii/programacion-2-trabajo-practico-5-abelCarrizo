package com.empresa.gestionempleados.repositorios;

import com.empresa.gestionempleados.entidades.Empleado;
import com.empresa.gestionempleados.entidades.Proyecto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProyectoRepository extends JpaRepository<Proyecto, Long> {
    List<Proyecto> findByEstado(String estado);
    List<Proyecto> findByEmpleadosContaining(Empleado empleado);
}