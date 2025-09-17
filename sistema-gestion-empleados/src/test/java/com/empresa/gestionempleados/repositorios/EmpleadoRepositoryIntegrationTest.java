package com.empresa.gestionempleados.repositorios;

import com.empresa.gestionempleados.entidades.Departamento;
import com.empresa.gestionempleados.entidades.Empleado;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class EmpleadoRepositoryIntegrationTest {

    @Autowired
    private EmpleadoRepository empleadoRepository;

    @Autowired
    private DepartamentoRepository departamentoRepository;

    private Departamento departamento;

    @BeforeEach
    void setUp() {
        empleadoRepository.deleteAll();
        departamentoRepository.deleteAll();

        departamento = new Departamento();
        departamento.setNombre("IT");
        departamento.setDescripcion("Tecnología de la Información");
        departamento = departamentoRepository.save(departamento);
    }

    private Empleado crearEmpleado(String nombre, String apellido, String email, BigDecimal salario, LocalDate fecha) {
        Empleado empleado = new Empleado();
        empleado.setNombre(nombre);
        empleado.setApellido(apellido);
        empleado.setEmail(email);
        empleado.setSalario(salario);
        empleado.setFechaContratacion(fecha);
        empleado.setDepartamento(departamento);
        return empleadoRepository.save(empleado);
    }

    @Test
    void cuandoGuardarEmpleado_entoncesSeGeneraId() {
        Empleado empleado = crearEmpleado("Juan", "Pérez", "juan@test.com",
                BigDecimal.valueOf(45000), LocalDate.now());

        assertNotNull(empleado.getId());
        assertTrue(empleadoRepository.existsById(empleado.getId()));
    }

    @Test
    void cuandoGuardarEmpleadoConEmailDuplicado_entoncesLanzaExcepcion() {
        crearEmpleado("Ana", "Gómez", "ana@test.com",
                BigDecimal.valueOf(50000), LocalDate.now());

        Empleado duplicado = new Empleado();
        duplicado.setNombre("Ana2");
        duplicado.setApellido("Gómez2");
        duplicado.setEmail("ana@test.com"); // duplicado
        duplicado.setSalario(BigDecimal.valueOf(55000));
        duplicado.setFechaContratacion(LocalDate.now());
        duplicado.setDepartamento(departamento);

        assertThrows(DataIntegrityViolationException.class, () -> {
            empleadoRepository.saveAndFlush(duplicado);
        });
    }

    @Test
    void cuandoBuscarPorEmail_entoncesLoEncuentra() {
        crearEmpleado("Pedro", "López", "pedro@test.com",
                BigDecimal.valueOf(40000), LocalDate.now());

        Optional<Empleado> encontrado = empleadoRepository.findByEmail("pedro@test.com");

        assertTrue(encontrado.isPresent());
        assertEquals("Pedro", encontrado.get().getNombre());
    }

    @Test
    void cuandoBuscarPorDepartamento_entoncesDevuelveLista() {
        crearEmpleado("Carlos", "Ramírez", "carlos@test.com",
                BigDecimal.valueOf(60000), LocalDate.now());

        List<Empleado> empleados = empleadoRepository.findByDepartamento(departamento);

        assertEquals(1, empleados.size());
        assertEquals("Carlos", empleados.get(0).getNombre());
    }

    @Test
    void cuandoBuscarPorRangoDeSalario_entoncesDevuelveLista() {
        crearEmpleado("Laura", "Martínez", "laura@test.com",
                BigDecimal.valueOf(30000), LocalDate.now());
        crearEmpleado("Sofía", "García", "sofia@test.com",
                BigDecimal.valueOf(70000), LocalDate.now());

        List<Empleado> empleados = empleadoRepository.findBySalarioBetween(
                BigDecimal.valueOf(25000), BigDecimal.valueOf(50000));

        assertEquals(1, empleados.size());
        assertEquals("Laura", empleados.get(0).getNombre());
    }

    @Test
    void cuandoBuscarPorFechaContratacion_entoncesDevuelveLista() {
        crearEmpleado("Luis", "Torres", "luis@test.com",
                BigDecimal.valueOf(50000), LocalDate.now().minusDays(10));
        crearEmpleado("Clara", "Mendoza", "clara@test.com",
                BigDecimal.valueOf(50000), LocalDate.now());

        List<Empleado> empleados = empleadoRepository.findByFechaContratacionAfter(LocalDate.now().minusDays(5));

        assertEquals(1, empleados.size());
        assertEquals("Clara", empleados.get(0).getNombre());
    }

    @Test
    void cuandoBuscarPorNombreDepartamento_entoncesDevuelveLista() {
        crearEmpleado("Javier", "Fernández", "javier@test.com",
                BigDecimal.valueOf(48000), LocalDate.now());

        List<Empleado> empleados = empleadoRepository.findByNombreDepartamento("IT");

        assertEquals(1, empleados.size());
        assertEquals("Javier", empleados.get(0).getNombre());
    }

    @Test
    void cuandoCalcularPromedioSalarioPorDepartamento_entoncesDevuelveValorCorrecto() {
        crearEmpleado("Raúl", "Núñez", "raul@test.com",
                BigDecimal.valueOf(40000), LocalDate.now());
        crearEmpleado("Elena", "Silva", "elena@test.com",
                BigDecimal.valueOf(60000), LocalDate.now());

        Optional<BigDecimal> promedio = empleadoRepository.findAverageSalarioByDepartamento(departamento.getId());

        assertTrue(promedio.isPresent());
        assertEquals(BigDecimal.valueOf(50000.00).setScale(2), promedio.get().setScale(2));
    }
}

