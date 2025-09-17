package com.empresa.gestionempleados.repositorios;

import com.empresa.gestionempleados.entidades.Proyecto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class ProyectoRepositoryIntegrationTest {

    @Autowired
    private ProyectoRepository proyectoRepository;

    @BeforeEach
    void setUp() {
        proyectoRepository.deleteAll();
    }

    private Proyecto crearProyecto(String nombre, LocalDate fechaInicio, LocalDate fechaFin) {
        Proyecto proyecto = new Proyecto();
        proyecto.setNombre(nombre);
        proyecto.setDescripcion("Descripción de " + nombre);
        proyecto.setFechaInicio(fechaInicio);
        proyecto.setFechaFin(fechaFin);
        return proyectoRepository.save(proyecto);
    }

    @Test
    void cuandoGuardarProyecto_entoncesSeGeneraId() {
        Proyecto proyecto = crearProyecto(
                "Sistema Contable",
                LocalDate.now().minusDays(10),
                LocalDate.now().plusDays(30)
        );

        assertNotNull(proyecto.getId());
        assertTrue(proyectoRepository.existsById(proyecto.getId()));
    }

    @Test
    void cuandoBuscarProyectosActivos_entoncesDevuelveSoloLosConFechaFinPosteriorAHoy() {
        crearProyecto("App Móvil", LocalDate.now().minusDays(5), LocalDate.now().plusDays(20));
        crearProyecto("Web Empresarial", LocalDate.now().minusMonths(2), LocalDate.now().minusDays(1));
        crearProyecto("API REST", LocalDate.now().minusWeeks(1), LocalDate.now().plusDays(1));

        List<Proyecto> proyectosActivos = proyectoRepository.findProyectosActivos(LocalDate.now());

        assertEquals(2, proyectosActivos.size());
        assertTrue(proyectosActivos.stream().anyMatch(p -> p.getNombre().equals("App Móvil")));
        assertTrue(proyectosActivos.stream().anyMatch(p -> p.getNombre().equals("API REST")));
    }

    @Test
    void cuandoNoHayProyectosActivos_entoncesDevuelveListaVacia() {
        crearProyecto("Sistema Antiguo", LocalDate.now().minusMonths(6), LocalDate.now().minusDays(10));

        List<Proyecto> proyectosActivos = proyectoRepository.findProyectosActivos(LocalDate.now());

        assertTrue(proyectosActivos.isEmpty());
    }

    @Test
    void cuandoBuscarPorIdExistente_entoncesDevuelveProyecto() {
        Proyecto proyecto = crearProyecto("CRM", LocalDate.now(), LocalDate.now().plusMonths(2));

        Optional<Proyecto> encontrado = proyectoRepository.findById(proyecto.getId());

        assertTrue(encontrado.isPresent());
        assertEquals("CRM", encontrado.get().getNombre());
    }

    @Test
    void cuandoBuscarPorIdInexistente_entoncesDevuelveVacio() {
        Optional<Proyecto> encontrado = proyectoRepository.findById(999L);

        assertFalse(encontrado.isPresent());
    }

    @Test
    void cuandoActualizarProyecto_entoncesSePersistenLosCambios() {
        Proyecto proyecto = crearProyecto("ERP", LocalDate.now(), LocalDate.now().plusMonths(3));

        proyecto.setDescripcion("Nueva descripción actualizada");
        Proyecto actualizado = proyectoRepository.save(proyecto);

        assertEquals("Nueva descripción actualizada", actualizado.getDescripcion());
    }

    @Test
    void cuandoEliminarProyecto_entoncesYaNoExisteEnRepositorio() {
        Proyecto proyecto = crearProyecto("Portal Web", LocalDate.now(), LocalDate.now().plusWeeks(5));

        proyectoRepository.deleteById(proyecto.getId());

        assertFalse(proyectoRepository.existsById(proyecto.getId()));
    }

    @Test
    void cuandoGuardarMultiplesProyectos_entoncesFindAllDevuelveTodos() {
        crearProyecto("Proyecto A", LocalDate.now(), LocalDate.now().plusDays(10));
        crearProyecto("Proyecto B", LocalDate.now(), LocalDate.now().plusDays(20));

        List<Proyecto> proyectos = proyectoRepository.findAll();

        assertEquals(2, proyectos.size());
    }
}
