package com.empresa.gestionempleados.controladores;

import com.empresa.gestionempleados.SistemaGestionEmpleadosApplication;
import com.empresa.gestionempleados.entidades.Empleado;
import com.empresa.gestionempleados.repositorios.EmpleadoRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = SistemaGestionEmpleadosApplication.class)
@AutoConfigureMockMvc
public class EmpleadoControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EmpleadoRepository empleadoRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        empleadoRepository.deleteAll();
    }

    private Empleado crearEmpleadoDePrueba(String nombre, String email) {
        Empleado e = new Empleado();
        e.setNombre(nombre);
        e.setApellido("Apellido");
        e.setEmail(email);
        e.setFechaContratacion(LocalDate.now());
        e.setSalario(new BigDecimal("35000"));
        return empleadoRepository.save(e);
    }

    @Test
    void cuandoCrearEmpleado_entoncesDevuelveEmpleadoCreado() throws Exception {
        Empleado empleado = new Empleado();
        empleado.setNombre("Ana");
        empleado.setApellido("Lopez");
        empleado.setEmail("ana.lopez@empresa.com");
        empleado.setFechaContratacion(LocalDate.now());
        empleado.setSalario(new BigDecimal("38000"));

        mockMvc.perform(post("/api/empleados")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(empleado)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.nombre").value("Ana"))
                .andExpect(jsonPath("$.apellido").value("Lopez"))
                .andExpect(jsonPath("$.email").value("ana.lopez@empresa.com"))
                .andExpect(jsonPath("$.salario").value(38000.0));
    }

    @Test
    void cuandoObtenerTodosLosEmpleados_entoncesDevuelveLista() throws Exception {
        Empleado e1 = crearEmpleadoDePrueba("Juan", "juan@test.com");
        Empleado e2 = crearEmpleadoDePrueba("Maria", "maria@test.com");

        mockMvc.perform(get("/api/empleados"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[*].nombre", containsInAnyOrder(e1.getNombre(), e2.getNombre())));
    }

    @Test
    void cuandoObtenerEmpleadoPorIdExistente_entoncesDevuelveEmpleado() throws Exception {
        Empleado e = crearEmpleadoDePrueba("Pedro", "pedro@test.com");

        mockMvc.perform(get("/api/empleados/{id}", e.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(e.getId()))
                .andExpect(jsonPath("$.nombre").value(e.getNombre()))
                .andExpect(jsonPath("$.email").value(e.getEmail()));
    }

    @Test
    void cuandoObtenerEmpleadoPorIdInexistente_entoncesDevuelve404() throws Exception {
        mockMvc.perform(get("/api/empleados/{id}", 999L))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString("Empleado no encontrado")));
    }

    @Test
    void cuandoActualizarEmpleadoExistente_entoncesDevuelveEmpleadoActualizado() throws Exception {
        Empleado e = crearEmpleadoDePrueba("Luis", "luis@test.com");
        e.setSalario(new BigDecimal("40000"));

        mockMvc.perform(put("/api/empleados/{id}", e.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(e)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(e.getId()))
                .andExpect(jsonPath("$.salario").value(40000.0));
    }

    @Test
    void cuandoActualizarEmpleadoInexistente_entoncesDevuelve404() throws Exception {
        Empleado e = new Empleado();
        e.setNombre("NoExiste");
        e.setApellido("Test");
        e.setEmail("noexiste@empresa.com");
        e.setFechaContratacion(LocalDate.now());
        e.setSalario(new BigDecimal("30000"));

        mockMvc.perform(put("/api/empleados/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(e)))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString("Empleado no encontrado")));
    }

    @Test
    void cuandoEliminarEmpleadoExistente_entoncesDevuelveNoContent() throws Exception {
        Empleado e = crearEmpleadoDePrueba("Carlos", "carlos@test.com");

        mockMvc.perform(delete("/api/empleados/{id}", e.getId()))
                .andExpect(status().isNoContent());
    }

    @Test
    void cuandoEliminarEmpleadoInexistente_entoncesDevuelve404() throws Exception {
        mockMvc.perform(delete("/api/empleados/{id}", 999L))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString("Empleado no encontrado")));
    }
}
