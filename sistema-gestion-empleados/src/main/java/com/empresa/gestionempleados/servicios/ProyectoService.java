package com.empresa.gestionempleados.servicios;

import com.empresa.gestionempleados.entidades.Proyecto;

import java.util.List;

public interface ProyectoService {
    Proyecto guardar(Proyecto proyecto);
    Proyecto buscarPorId(Long id);
    List<Proyecto> buscarProyectosActivos();
    List<Proyecto> obtenerTodos();
    Proyecto actualizar(Long id, Proyecto proyecto);
    void eliminar(Long id);
}
