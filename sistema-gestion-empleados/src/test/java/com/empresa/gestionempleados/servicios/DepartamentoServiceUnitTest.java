package com.empresa.gestionempleados.servicios;

import com.empresa.gestionempleados.entidades.Departamento;
import com.empresa.gestionempleados.exceptions.DepartamentoNoEncontradoException;
import com.empresa.gestionempleados.repositorios.DepartamentoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DepartamentoServiceUnitTest {

    @Mock
    private DepartamentoRepository departamentoRepository;

    @InjectMocks
    private DepartamentoServiceImpl departamentoService; // tu implementación concreta

    private Departamento departamento;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        departamento = new Departamento();
        departamento.setId(1L);
        departamento.setNombre("Recursos Humanos");
        departamento.setDescripcion("Gestiona el personal");
    }

    @Test
    void cuandoGuardarDepartamento_entoncesSePersisteCorrectamente() {
        when(departamentoRepository.save(any(Departamento.class))).thenReturn(departamento);

        Departamento guardado = departamentoService.guardar(departamento);

        assertNotNull(guardado);
        assertEquals("Recursos Humanos", guardado.getNombre());
        verify(departamentoRepository, times(1)).save(departamento);
    }

    @Test
    void cuandoBuscarPorId_existente_entoncesDevuelveDepartamento() {
        when(departamentoRepository.findById(1L)).thenReturn(Optional.of(departamento));

        Departamento encontrado = departamentoService.buscarPorId(1L);

        assertEquals("Recursos Humanos", encontrado.getNombre());
        verify(departamentoRepository, times(1)).findById(1L);
    }

    @Test
    void cuandoBuscarPorId_inexistente_entoncesLanzaExcepcion() {
        when(departamentoRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(DepartamentoNoEncontradoException.class,
                () -> departamentoService.buscarPorId(999L));
        verify(departamentoRepository, times(1)).findById(999L);
    }

    @Test
    void cuandoObtenerTodos_entoncesDevuelveLista() {
        List<Departamento> lista = List.of(departamento);
        when(departamentoRepository.findAll()).thenReturn(lista);

        List<Departamento> resultado = departamentoService.obtenerTodos();

        assertEquals(1, resultado.size());
        verify(departamentoRepository, times(1)).findAll();
    }

    @Test
    void cuandoActualizar_existente_entoncesDevuelveActualizado() {
        Departamento cambios = new Departamento();
        cambios.setNombre("Finanzas");
        cambios.setDescripcion("Área financiera");

        when(departamentoRepository.existsById(1L)).thenReturn(true); // ✅ clave
        when(departamentoRepository.save(any(Departamento.class))).thenReturn(cambios);

        Departamento actualizado = departamentoService.actualizar(1L, cambios);

        assertEquals("Finanzas", actualizado.getNombre());
        assertEquals("Área financiera", actualizado.getDescripcion());
    }


    @Test
    void cuandoEliminar_existente_entoncesLlamaDelete() {
        when(departamentoRepository.existsById(1L)).thenReturn(true);

        departamentoService.eliminar(1L);

        verify(departamentoRepository, times(1)).deleteById(1L);
    }

    @Test
    void cuandoEliminar_inexistente_entoncesLanzaExcepcion() {
        when(departamentoRepository.existsById(999L)).thenReturn(false);

        assertThrows(DepartamentoNoEncontradoException.class, () -> departamentoService.eliminar(999L));
        verify(departamentoRepository, times(1)).existsById(999L);
        verify(departamentoRepository, never()).deleteById(anyLong());
    }
}