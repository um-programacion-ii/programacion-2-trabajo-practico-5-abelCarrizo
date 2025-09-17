package com.empresa.gestionempleados.servicios;

import com.empresa.gestionempleados.entidades.Proyecto;
import com.empresa.gestionempleados.exceptions.ProyectoNoEncontradoException;
import com.empresa.gestionempleados.repositorios.ProyectoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProyectoServiceUnitTest {

    @Mock
    private ProyectoRepository proyectoRepository;

    @InjectMocks
    private ProyectoServiceImpl proyectoService;

    private Proyecto proyecto;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        proyecto = new Proyecto();
        proyecto.setId(1L);
        proyecto.setNombre("Proyecto Test");
        proyecto.setDescripcion("Descripción Test");
        proyecto.setFechaInicio(LocalDate.now().minusDays(5));
        proyecto.setFechaFin(LocalDate.now().plusDays(10));
    }

    @Test
    void cuandoGuardar_entoncesDevuelveProyectoGuardado() {
        when(proyectoRepository.save(any(Proyecto.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Proyecto guardado = proyectoService.guardar(proyecto);

        assertNotNull(guardado);
        assertEquals("Proyecto Test", guardado.getNombre());
        verify(proyectoRepository, times(1)).save(proyecto);
    }

    @Test
    void cuandoBuscarPorIdExistente_entoncesDevuelveProyecto() {
        when(proyectoRepository.findById(1L)).thenReturn(Optional.of(proyecto));

        Proyecto encontrado = proyectoService.buscarPorId(1L);

        assertNotNull(encontrado);
        assertEquals("Proyecto Test", encontrado.getNombre());
    }

    @Test
    void cuandoBuscarPorIdInexistente_entoncesLanzaExcepcion() {
        when(proyectoRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ProyectoNoEncontradoException.class, () -> proyectoService.buscarPorId(999L));
    }

    @Test
    void cuandoBuscarProyectosActivos_entoncesDevuelveLista() {
        Proyecto proyecto2 = new Proyecto();
        proyecto2.setId(2L);
        proyecto2.setNombre("Proyecto Activo");
        proyecto2.setFechaFin(LocalDate.now().plusDays(5));

        when(proyectoRepository.findProyectosActivos(any(LocalDate.class)))
                .thenReturn(Arrays.asList(proyecto, proyecto2));

        List<Proyecto> activos = proyectoService.buscarProyectosActivos();

        assertEquals(2, activos.size());
        verify(proyectoRepository, times(1)).findProyectosActivos(any(LocalDate.class));
    }

    @Test
    void cuandoObtenerTodos_entoncesDevuelveListaCompleta() {
        when(proyectoRepository.findAll()).thenReturn(Arrays.asList(proyecto));

        List<Proyecto> todos = proyectoService.obtenerTodos();

        assertEquals(1, todos.size());
        verify(proyectoRepository, times(1)).findAll();
    }

    @Test
    void cuandoActualizarExistente_entoncesDevuelveActualizado() {
        when(proyectoRepository.existsById(proyecto.getId())).thenReturn(true);
        when(proyectoRepository.save(any(Proyecto.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Proyecto cambios = new Proyecto();
        cambios.setNombre("Proyecto Actualizado");
        cambios.setDescripcion("Nueva descripción");

        Proyecto actualizado = proyectoService.actualizar(proyecto.getId(), cambios);

        assertEquals("Proyecto Actualizado", actualizado.getNombre());
        assertEquals("Nueva descripción", actualizado.getDescripcion());
        verify(proyectoRepository, times(1)).save(cambios);
    }

    @Test
    void cuandoActualizarInexistente_entoncesLanzaExcepcion() {
        when(proyectoRepository.existsById(999L)).thenReturn(false);

        Proyecto cambios = new Proyecto();
        cambios.setNombre("Inexistente");

        assertThrows(ProyectoNoEncontradoException.class,
                () -> proyectoService.actualizar(999L, cambios));
    }

    @Test
    void cuandoEliminarExistente_entoncesSeElimina() {
        when(proyectoRepository.existsById(proyecto.getId())).thenReturn(true);

        proyectoService.eliminar(proyecto.getId());

        verify(proyectoRepository, times(1)).deleteById(proyecto.getId());
    }

    @Test
    void cuandoEliminarInexistente_entoncesLanzaExcepcion() {
        when(proyectoRepository.existsById(999L)).thenReturn(false);

        assertThrows(ProyectoNoEncontradoException.class,
                () -> proyectoService.eliminar(999L));
    }
}
