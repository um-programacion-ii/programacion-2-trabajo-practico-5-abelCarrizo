package com.empresa.gestionempleados.servicios;

import com.empresa.gestionempleados.entidades.Proyecto;
import com.empresa.gestionempleados.exceptions.ProyectoNoEncontradoException;
import com.empresa.gestionempleados.repositorios.ProyectoRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
class ProyectoServiceIntegrationTest {

    @Autowired
    private ProyectoService proyectoService;

    @Autowired
    private ProyectoRepository proyectoRepository;

    @BeforeEach
    void setUp() {
        proyectoRepository.deleteAll();
    }

    private Proyecto crearProyectoDePrueba(LocalDate fechaFin) {
        Proyecto proyecto = new Proyecto();
        proyecto.setNombre("Proyecto_" + UUID.randomUUID());
        proyecto.setDescripcion("Proyecto de prueba");
        proyecto.setFechaInicio(LocalDate.now().minusDays(5));
        proyecto.setFechaFin(fechaFin);
        return proyectoRepository.save(proyecto);
    }

    @Test
    void cuandoGuardarProyecto_entoncesSePersiste() {
        Proyecto proyecto = crearProyectoDePrueba(LocalDate.now().plusDays(10));

        assertNotNull(proyecto.getId());
        assertTrue(proyectoRepository.existsById(proyecto.getId()));
    }

    @Test
    void cuandoBuscarProyectoPorId_entoncesLoEncuentra() {
        Proyecto proyecto = crearProyectoDePrueba(LocalDate.now().plusDays(10));

        Proyecto encontrado = proyectoService.buscarPorId(proyecto.getId());

        assertEquals(proyecto.getNombre(), encontrado.getNombre());
    }

    @Test
    void cuandoBuscarProyectoInexistente_entoncesLanzaExcepcion() {
        assertThrows(ProyectoNoEncontradoException.class, () -> proyectoService.buscarPorId(999L));
    }

    @Test
    void cuandoBuscarProyectosActivos_entoncesDevuelveSoloLosQueSiguen() {
        crearProyectoDePrueba(LocalDate.now().plusDays(10)); // activo
        crearProyectoDePrueba(LocalDate.now().minusDays(1)); // ya terminó

        List<Proyecto> activos = proyectoService.buscarProyectosActivos();

        assertEquals(1, activos.size());
        assertTrue(activos.get(0).getFechaFin().isAfter(LocalDate.now()));
    }

    @Test
    void cuandoObtenerTodos_entoncesDevuelveListaCompleta() {
        crearProyectoDePrueba(LocalDate.now().plusDays(10));
        crearProyectoDePrueba(LocalDate.now().plusDays(20));

        List<Proyecto> todos = proyectoService.obtenerTodos();

        assertEquals(2, todos.size());
    }

    @Test
    void cuandoActualizarProyecto_entoncesModificaDatos() {
        Proyecto proyecto = crearProyectoDePrueba(LocalDate.now().plusDays(10));

        Proyecto cambios = new Proyecto();
        cambios.setNombre("Proyecto Actualizado");
        cambios.setDescripcion("Nueva descripción");
        cambios.setFechaInicio(LocalDate.now().minusDays(2));
        cambios.setFechaFin(LocalDate.now().plusDays(30));

        Proyecto actualizado = proyectoService.actualizar(proyecto.getId(), cambios);

        assertEquals("Proyecto Actualizado", actualizado.getNombre());
        assertEquals("Nueva descripción", actualizado.getDescripcion());
    }

    @Test
    void cuandoActualizarProyectoInexistente_entoncesLanzaExcepcion() {
        Proyecto cambios = new Proyecto();
        cambios.setNombre("Inexistente");

        assertThrows(ProyectoNoEncontradoException.class, () -> proyectoService.actualizar(999L, cambios));
    }

    @Test
    void cuandoEliminarProyecto_entoncesYaNoExiste() {
        Proyecto proyecto = crearProyectoDePrueba(LocalDate.now().plusDays(10));

        proyectoService.eliminar(proyecto.getId());

        assertFalse(proyectoRepository.existsById(proyecto.getId()));
    }

    @Test
    void cuandoEliminarProyectoInexistente_entoncesLanzaExcepcion() {
        assertThrows(ProyectoNoEncontradoException.class, () -> proyectoService.eliminar(999L));
    }
}
