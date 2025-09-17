package com.empresa.gestionempleados.controladores;

import com.empresa.gestionempleados.entidades.Proyecto;
import com.empresa.gestionempleados.servicios.ProyectoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/proyectos")
@Validated
public class ProyectoController {

    private final ProyectoService proyectoService;

    public ProyectoController(ProyectoService proyectoService) {
        this.proyectoService = proyectoService;
    }

    @GetMapping
    public List<Proyecto> obtenerTodos() {
        return proyectoService.obtenerTodos();
    }

    @GetMapping("/{id}")
    public Proyecto obtenerPorId(@PathVariable Long id) {
        return proyectoService.buscarPorId(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Proyecto crear(@Valid @RequestBody Proyecto proyecto) {
        return proyectoService.guardar(proyecto);
    }

    @PutMapping("/{id}")
    public Proyecto actualizar(@PathVariable Long id, @Valid @RequestBody Proyecto proyecto) {
        return proyectoService.actualizar(id, proyecto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Long id) {
        proyectoService.eliminar(id);
    }

    @GetMapping("/activos")
    public List<Proyecto> obtenerPorDepartamento() {
        return proyectoService.buscarProyectosActivos();
    }
}

