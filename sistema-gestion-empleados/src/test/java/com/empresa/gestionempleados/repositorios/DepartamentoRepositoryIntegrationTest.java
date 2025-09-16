package com.empresa.gestionempleados.repositorios;

import com.empresa.gestionempleados.entidades.Departamento;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class DepartamentoRepositoryIntegrationTest {

    @Autowired
    private DepartamentoRepository departamentoRepository;

    @BeforeEach
    void setUp() {
        departamentoRepository.deleteAll();
    }

    private Departamento crearDepartamento(String nombre, String descripcion) {
        Departamento departamento = new Departamento();
        departamento.setNombre(nombre);
        departamento.setDescripcion(descripcion);
        return departamentoRepository.save(departamento);
    }

    @Test
    void cuandoGuardarDepartamento_entoncesSeGeneraId() {
        Departamento departamento = crearDepartamento("Recursos Humanos", "Gestiona el personal");

        assertNotNull(departamento.getId());
        assertTrue(departamentoRepository.existsById(departamento.getId()));
    }

    @Test
    void cuandoGuardarDepartamentoConNombreDuplicado_entoncesLanzaExcepcion() {
        crearDepartamento("Finanzas", "Departamento financiero");

        Departamento duplicado = new Departamento();
        duplicado.setNombre("Finanzas"); // mismo nombre
        duplicado.setDescripcion("Duplicado");

        assertThrows(DataIntegrityViolationException.class, () -> {
            departamentoRepository.saveAndFlush(duplicado);
        });
    }

    @Test
    void cuandoBuscarPorId_entoncesLoEncuentra() {
        Departamento departamento = crearDepartamento("Marketing", "Estrategias de mercado");

        Optional<Departamento> encontrado = departamentoRepository.findById(departamento.getId());

        assertTrue(encontrado.isPresent());
        assertEquals("Marketing", encontrado.get().getNombre());
    }

    @Test
    void cuandoBuscarTodos_entoncesDevuelveLista() {
        crearDepartamento("Ventas", "Área comercial");
        crearDepartamento("IT", "Soporte tecnológico");

        List<Departamento> todos = departamentoRepository.findAll();

        assertEquals(2, todos.size());
    }

    @Test
    void cuandoEliminarDepartamento_entoncesNoExisteEnBD() {
        Departamento departamento = crearDepartamento("Logística", "Gestión de envíos");

        departamentoRepository.deleteById(departamento.getId());

        assertFalse(departamentoRepository.existsById(departamento.getId()));
    }
}
