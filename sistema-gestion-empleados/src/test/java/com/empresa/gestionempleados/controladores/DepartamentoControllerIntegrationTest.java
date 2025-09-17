package com.empresa.gestionempleados.controladores;

import com.empresa.gestionempleados.entidades.Departamento;
import com.empresa.gestionempleados.repositorios.DepartamentoRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class DepartamentoControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private DepartamentoRepository departamentoRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        departamentoRepository.deleteAll();
    }

    private Departamento crearDepartamentoDePrueba() {
        Departamento d = new Departamento();
        d.setNombre("IT");
        d.setDescripcion("Tecnología");
        return departamentoRepository.save(d);
    }

    @Test
    void cuandoCrearDepartamento_entoncesDevuelve201() throws Exception {
        Departamento d = new Departamento();
        d.setNombre("RRHH");
        d.setDescripcion("Recursos Humanos");

        mockMvc.perform(post("/api/departamentos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(d)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.nombre").value("RRHH"))
                .andExpect(jsonPath("$.descripcion").value("Recursos Humanos"));
    }

    @Test
    void cuandoObtenerTodos_entoncesDevuelveLista() throws Exception {
        crearDepartamentoDePrueba();

        mockMvc.perform(get("/api/departamentos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].nombre", is("IT")));
    }

    @Test
    void cuandoObtenerPorIdExistente_entoncesDevuelveDepartamento() throws Exception {
        Departamento d = crearDepartamentoDePrueba();

        mockMvc.perform(get("/api/departamentos/{id}", d.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(d.getId()))
                .andExpect(jsonPath("$.nombre").value("IT"));
    }

    @Test
    void cuandoObtenerPorIdInexistente_entoncesDevuelve404() throws Exception {
        mockMvc.perform(get("/api/departamentos/{id}", 999L))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString("Departamento no encontrado")));
    }

    @Test
    void cuandoActualizarDepartamentoExistente_entoncesDevuelveActualizado() throws Exception {
        Departamento d = crearDepartamentoDePrueba();
        d.setNombre("IT_Modificado");

        mockMvc.perform(put("/api/departamentos/{id}", d.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(d)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("IT_Modificado"));
    }

    @Test
    void cuandoActualizarDepartamentoInexistente_entoncesDevuelve404() throws Exception {
        Departamento d = new Departamento();
        d.setNombre("NoExiste");

        mockMvc.perform(put("/api/departamentos/{id}", 999L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(d)))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString("Departamento no encontrado")));
    }

    @Test
    void cuandoEliminarDepartamentoExistente_entoncesDevuelve204() throws Exception {
        Departamento d = crearDepartamentoDePrueba();

        mockMvc.perform(delete("/api/departamentos/{id}", d.getId()))
                .andExpect(status().isNoContent());
    }

    @Test
    void cuandoEliminarDepartamentoInexistente_entoncesDevuelve404() throws Exception {
        mockMvc.perform(delete("/api/departamentos/{id}", 999L))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString("Departamento no encontrado")));
    }
}