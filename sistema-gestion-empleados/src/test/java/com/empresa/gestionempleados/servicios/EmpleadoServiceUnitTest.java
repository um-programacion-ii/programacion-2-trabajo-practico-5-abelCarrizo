package com.empresa.gestionempleados.servicios;

import com.empresa.gestionempleados.entidades.Departamento;
import com.empresa.gestionempleados.entidades.Empleado;
import com.empresa.gestionempleados.exceptions.EmailDuplicadoException;
import com.empresa.gestionempleados.exceptions.EmpleadoNoEncontradoException;
import com.empresa.gestionempleados.repositorios.DepartamentoRepository;
import com.empresa.gestionempleados.repositorios.EmpleadoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class EmpleadoServiceUnitTest {

    @Mock
    private EmpleadoRepository empleadoRepository;

    @Mock
    private DepartamentoRepository departamentoRepository;

    @InjectMocks
    private EmpleadoServiceImpl empleadoService;

    private Empleado empleado;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        Departamento departamento = new Departamento();
        departamento.setId(1L);
        departamento.setNombre("IT");

        empleado = new Empleado();
        empleado.setId(1L);
        empleado.setNombre("Juan");
        empleado.setApellido("Pérez");
        empleado.setEmail("juan@empresa.com");
        empleado.setSalario(BigDecimal.valueOf(50000));
        empleado.setFechaContratacion(LocalDate.now());
        empleado.setDepartamento(departamento);
    }

    @Test
    void cuandoGuardarEmpleadoNoDuplicado_entoncesSeGuarda() {
        when(empleadoRepository.findByEmail(empleado.getEmail())).thenReturn(Optional.empty());
        when(empleadoRepository.save(empleado)).thenReturn(empleado);

        Empleado guardado = empleadoService.guardar(empleado);

        assertEquals(empleado.getEmail(), guardado.getEmail());
        verify(empleadoRepository, times(1)).save(empleado);
    }

    @Test
    void cuandoGuardarEmpleadoConEmailDuplicado_entoncesLanzaExcepcion() {
        when(empleadoRepository.findByEmail(empleado.getEmail())).thenReturn(Optional.of(empleado));

        assertThrows(EmailDuplicadoException.class, () -> empleadoService.guardar(empleado));
        verify(empleadoRepository, never()).save(any());
    }

    @Test
    void cuandoBuscarPorIdExistente_entoncesDevuelveEmpleado() {
        when(empleadoRepository.findById(1L)).thenReturn(Optional.of(empleado));

        Empleado encontrado = empleadoService.buscarPorId(1L);

        assertEquals("Juan", encontrado.getNombre());
    }

    @Test
    void cuandoBuscarPorIdInexistente_entoncesLanzaExcepcion() {
        when(empleadoRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(EmpleadoNoEncontradoException.class, () -> empleadoService.buscarPorId(2L));
    }

    @Test
    void cuandoActualizarExistente_entoncesDevuelveActualizado() {
        when(empleadoRepository.existsById(empleado.getId())).thenReturn(true);
        when(empleadoRepository.save(any(Empleado.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Empleado cambios = new Empleado();
        cambios.setNombre("Carlos");
        cambios.setApellido("Ramírez");

        Empleado actualizado = empleadoService.actualizar(1L, cambios);

        assertEquals("Carlos", actualizado.getNombre());
        assertEquals("Ramírez", actualizado.getApellido());
        verify(empleadoRepository, times(1)).save(cambios);
    }


    @Test
    void cuandoActualizarInexistente_entoncesLanzaExcepcion() {
        when(empleadoRepository.existsById(2L)).thenReturn(false);

        assertThrows(EmpleadoNoEncontradoException.class, () -> empleadoService.actualizar(2L, empleado));
        verify(empleadoRepository, never()).save(any());
    }

    @Test
    void cuandoEliminarExistente_entoncesSeElimina() {
        when(empleadoRepository.existsById(empleado.getId())).thenReturn(true);

        empleadoService.eliminar(1L);

        verify(empleadoRepository, times(1)).deleteById(1L);
    }

    @Test
    void cuandoEliminarInexistente_entoncesLanzaExcepcion() {
        when(empleadoRepository.existsById(2L)).thenReturn(false);

        assertThrows(EmpleadoNoEncontradoException.class, () -> empleadoService.eliminar(2L));
        verify(empleadoRepository, never()).deleteById(anyLong());
    }
}
