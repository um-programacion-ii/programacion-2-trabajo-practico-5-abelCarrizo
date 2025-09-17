package com.empresa.gestionempleados.repositorios;

import com.empresa.gestionempleados.entidades.Proyecto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ProyectoRepository extends JpaRepository<Proyecto, Long> {

    @Query("SELECT p FROM Proyecto p WHERE p.fechaFin > :hoy")
    List<Proyecto> findProyectosActivos(@Param("hoy") LocalDate hoy);
}