package com.empresa.gestionempleados.servicios;

import com.empresa.gestionempleados.entidades.Departamento;
import com.empresa.gestionempleados.entidades.Empleado;
import com.empresa.gestionempleados.exceptions.EmpleadoNoEncontradoException;
import com.empresa.gestionempleados.repositorios.DepartamentoRepository;
import com.empresa.gestionempleados.repositorios.EmpleadoRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
class EmpleadoServiceIntegrationTest {

    private final EmpleadoService empleadoService;
    private final EmpleadoRepository empleadoRepository;
    private final DepartamentoRepository departamentoRepository;

    @Autowired
    public EmpleadoServiceIntegrationTest(EmpleadoService empleadoService,
                                          EmpleadoRepository empleadoRepository,
                                          DepartamentoRepository departamentoRepository) {
        this.empleadoService = empleadoService;
        this.empleadoRepository = empleadoRepository;
        this.departamentoRepository = departamentoRepository;
    }

    @BeforeEach
    void setUp() {
        empleadoRepository.deleteAll();
        departamentoRepository.deleteAll();
    }

    private Empleado crearEmpleadoDePrueba() {
        Departamento departamento = new Departamento();
        departamento.setNombre("IT_" + UUID.randomUUID());
        departamento.setDescripcion("Tecnología");
        departamento = departamentoRepository.save(departamento);

        Empleado empleado = new Empleado();
        empleado.setNombre("Juan");
        empleado.setApellido("Pérez");
        empleado.setEmail("juan" + UUID.randomUUID() + "@empresa.com");
        empleado.setSalario(BigDecimal.valueOf(50000));
        empleado.setDepartamento(departamento);

        empleado.setFechaContratacion(LocalDate.now());

        return empleadoRepository.save(empleado);
    }

    @Test
    void cuandoGuardarEmpleadoConEmailDuplicado_entoncesLanzaExcepcion() {

        Empleado empleado1 = crearEmpleadoDePrueba();

        Empleado empleado2 = new Empleado();
        empleado2.setNombre("Ana");
        empleado2.setApellido("Gómez");
        empleado2.setEmail(empleado1.getEmail());
        empleado2.setSalario(BigDecimal.valueOf(50000));
        empleado2.setDepartamento(empleado1.getDepartamento());

        assertThrows(Exception.class, () -> empleadoService.guardar(empleado2));
    }

    @Test
    void cuandoActualizarEmpleadoInexistente_entoncesLanzaExcepcion() {
        assertThrows(EmpleadoNoEncontradoException.class, () -> {
            empleadoService.actualizar(999L, new Empleado());
        });
    }

    @Test
    void cuandoEliminarEmpleado_entoncesYaNoExisteEnBD() {
        Empleado empleado = crearEmpleadoDePrueba();

        empleadoService.eliminar(empleado.getId());

        assertFalse(empleadoRepository.existsById(empleado.getId()));
    }

    //a
    @Test
    void cuandoGuardarEmpleado_entoncesSePersisteEnBD() {
        Empleado empleado = crearEmpleadoDePrueba();

        Optional<Empleado> encontrado = empleadoRepository.findById(empleado.getId());

        assertTrue(encontrado.isPresent());
        assertEquals(empleado.getEmail(), encontrado.get().getEmail());
    }

    @Test
    void cuandoBuscarEmpleadoPorId_entoncesLoEncuentra() {
        Empleado empleado = crearEmpleadoDePrueba();

        Empleado encontrado = empleadoService.buscarPorId(empleado.getId());

        assertNotNull(encontrado);
        assertEquals(empleado.getId(), encontrado.getId());
    }

    @Test
    void cuandoBuscarEmpleadoInexistente_entoncesLanzaExcepcion() {
        assertThrows(EmpleadoNoEncontradoException.class, () -> empleadoService.buscarPorId(999L));
    }

    @Test
    void cuandoListarEmpleados_entoncesDevuelveListaCorrecta() {
        crearEmpleadoDePrueba();
        crearEmpleadoDePrueba();

        List<Empleado> empleados = empleadoService.obtenerTodos();

        assertEquals(2, empleados.size());
    }

    @Test
    void cuandoActualizarEmpleado_entoncesSeModificanLosDatos() {
        Empleado empleado = crearEmpleadoDePrueba();

        Empleado cambios = new Empleado();
        cambios.setNombre("Carlos");
        cambios.setApellido("Ramírez");
        cambios.setEmail("carlos" + UUID.randomUUID() + "@empresa.com");
        cambios.setSalario(BigDecimal.valueOf(70000));
        cambios.setDepartamento(empleado.getDepartamento());

        Empleado actualizado = empleadoService.actualizar(empleado.getId(), cambios);

        assertEquals("Carlos", actualizado.getNombre());
        assertEquals("Ramírez", actualizado.getApellido());
        assertEquals(BigDecimal.valueOf(70000), actualizado.getSalario());
    }
}
