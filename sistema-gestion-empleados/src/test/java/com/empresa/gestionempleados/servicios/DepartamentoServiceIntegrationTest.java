package com.empresa.gestionempleados.servicios;

import com.empresa.gestionempleados.entidades.Departamento;
import com.empresa.gestionempleados.exceptions.DepartamentoNoEncontradoException;
import com.empresa.gestionempleados.repositorios.DepartamentoRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
class DepartamentoServiceIntegrationTest {

    private DepartamentoService departamentoService;
    private DepartamentoRepository departamentoRepository;

    @Autowired
    public DepartamentoServiceIntegrationTest(DepartamentoService departamentoService,
                                              DepartamentoRepository departamentoRepository) {
        this.departamentoService = departamentoService;
        this.departamentoRepository = departamentoRepository;
    }

    @BeforeEach
    void setUp() {
        departamentoRepository.deleteAll();
    }

    @Test
    void cuandoGuardarDepartamento_entoncesSePersisteCorrectamente() {
        Departamento departamento = new Departamento();
        departamento.setNombre("Recursos Humanos");
        departamento.setDescripcion("Gestiona el personal");

        Departamento guardado = departamentoService.guardar(departamento);

        assertNotNull(guardado.getId());
        assertEquals("Recursos Humanos", guardado.getNombre());
        assertTrue(departamentoRepository.existsById(guardado.getId()));
    }

    @Test
    void cuandoBuscarPorIdInexistente_entoncesLanzaExcepcion() {
        assertThrows(DepartamentoNoEncontradoException.class, () -> {
            departamentoService.buscarPorId(999L);
        });
    }

    @Test
    void cuandoActualizarDepartamento_entoncesSeModificanLosDatos() {
        Departamento departamento = new Departamento();
        departamento.setNombre("Ventas");
        departamento.setDescripcion("Departamento de Ventas");
        departamento = departamentoRepository.save(departamento);

        departamento.setDescripcion("Ventas Internacionales");

        Departamento actualizado = departamentoService.actualizar(departamento.getId(), departamento);

        assertEquals("Ventas Internacionales", actualizado.getDescripcion());
    }

    @Test
    void cuandoEliminarDepartamento_entoncesYaNoExisteEnBD() {
        Departamento departamento = new Departamento();
        departamento.setNombre("Marketing");
        departamento.setDescripcion("Estrategias de mercado");
        departamento = departamentoRepository.save(departamento);

        departamentoService.eliminar(departamento.getId());

        assertFalse(departamentoRepository.existsById(departamento.getId()));
    }
}

