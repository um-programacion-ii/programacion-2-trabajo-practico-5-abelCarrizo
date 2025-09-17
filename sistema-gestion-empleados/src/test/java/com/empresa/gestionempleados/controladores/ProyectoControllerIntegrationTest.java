package com.empresa.gestionempleados.controladores;

import com.empresa.gestionempleados.entidades.Proyecto;
import com.empresa.gestionempleados.exceptions.ProyectoNoEncontradoException;
import com.empresa.gestionempleados.repositorios.ProyectoRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class ProyectoControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProyectoRepository proyectoRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        proyectoRepository.deleteAll();
    }

    @Test
    void cuandoCrearProyecto_entoncesDevuelveProyectoCreado() throws Exception {
        Proyecto proyecto = new Proyecto();
        proyecto.setNombre("Proyecto Alpha");
        proyecto.setDescripcion("Descripcion Alpha");
        proyecto.setFechaInicio(LocalDate.now());
        proyecto.setFechaFin(LocalDate.now().plusMonths(2));

        mockMvc.perform(post("/api/proyectos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(proyecto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.nombre").value("Proyecto Alpha"))
                .andExpect(jsonPath("$.descripcion").value("Descripcion Alpha"))
                .andExpect(jsonPath("$.fechaInicio").value(proyecto.getFechaInicio().toString()))
                .andExpect(jsonPath("$.fechaFin").value(proyecto.getFechaFin().toString()));
    }

    @Test
    void cuandoObtenerTodosLosProyectos_entoncesDevuelveListaRobusta() throws Exception {
        Proyecto p1 = new Proyecto();
        p1.setNombre("Proyecto A");
        p1.setDescripcion("Desc A");
        p1.setFechaInicio(LocalDate.now());
        p1.setFechaFin(LocalDate.now().plusMonths(1));

        Proyecto p2 = new Proyecto();
        p2.setNombre("Proyecto B");
        p2.setDescripcion("Desc B");
        p2.setFechaInicio(LocalDate.now());
        p2.setFechaFin(LocalDate.now().plusMonths(3));

        List<Proyecto> guardados = proyectoRepository.saveAll(List.of(p1, p2));

        mockMvc.perform(get("/api/proyectos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(guardados.size())))
                .andExpect(jsonPath("$[*].nombre", containsInAnyOrder(
                        guardados.get(0).getNombre(), guardados.get(1).getNombre()
                )))
                .andExpect(jsonPath("$[*].descripcion", containsInAnyOrder(
                        guardados.get(0).getDescripcion(), guardados.get(1).getDescripcion()
                )));
    }

    @Test
    void cuandoObtenerProyectoPorId_entoncesDevuelveProyecto() throws Exception {
        Proyecto proyecto = new Proyecto();
        proyecto.setNombre("Proyecto X");
        proyecto.setDescripcion("Descripcion X");
        proyecto.setFechaInicio(LocalDate.now());
        proyecto.setFechaFin(LocalDate.now().plusMonths(1));
        proyecto = proyectoRepository.save(proyecto);

        mockMvc.perform(get("/api/proyectos/{id}", proyecto.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(proyecto.getId()))
                .andExpect(jsonPath("$.nombre").value(proyecto.getNombre()))
                .andExpect(jsonPath("$.descripcion").value(proyecto.getDescripcion()));
    }

    @Test
    void cuandoActualizarProyecto_entoncesDevuelveProyectoActualizado() throws Exception {
        Proyecto proyecto = new Proyecto();
        proyecto.setNombre("Proyecto Old");
        proyecto.setDescripcion("Descripcion Old");
        proyecto.setFechaInicio(LocalDate.now());
        proyecto.setFechaFin(LocalDate.now().plusMonths(1));
        proyecto = proyectoRepository.save(proyecto);

        proyecto.setNombre("Proyecto Updated");
        proyecto.setDescripcion("Descripcion Updated");

        mockMvc.perform(put("/api/proyectos/{id}", proyecto.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(proyecto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(proyecto.getId()))
                .andExpect(jsonPath("$.nombre").value("Proyecto Updated"))
                .andExpect(jsonPath("$.descripcion").value("Descripcion Updated"));
    }

    @Test
    void cuandoEliminarProyecto_entoncesDevuelveNoContent() throws Exception {
        Proyecto proyecto = new Proyecto();
        proyecto.setNombre("Proyecto Delete");
        proyecto.setDescripcion("Descripcion Delete");
        proyecto.setFechaInicio(LocalDate.now());
        proyecto.setFechaFin(LocalDate.now().plusMonths(1));
        proyecto = proyectoRepository.save(proyecto);

        mockMvc.perform(delete("/api/proyectos/{id}", proyecto.getId()))
                .andExpect(status().isNoContent());
    }

    @Test
    void cuandoObtenerProyectosActivos_entoncesDevuelveSoloActivosRobusto() throws Exception {
        Proyecto activo = new Proyecto();
        activo.setNombre("Activo");
        activo.setDescripcion("Activo Desc");
        activo.setFechaInicio(LocalDate.now().minusDays(1));
        activo.setFechaFin(LocalDate.now().plusDays(10));

        Proyecto terminado = new Proyecto();
        terminado.setNombre("Terminado");
        terminado.setDescripcion("Terminado Desc");
        terminado.setFechaInicio(LocalDate.now().minusMonths(2));
        terminado.setFechaFin(LocalDate.now().minusDays(1));

        proyectoRepository.saveAll(List.of(activo, terminado));

        mockMvc.perform(get("/api/proyectos/activos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].nombre").value(activo.getNombre()))
                .andExpect(jsonPath("$[0].descripcion").value(activo.getDescripcion()))
                .andExpect(jsonPath("$[0].fechaFin").value(activo.getFechaFin().toString()));
    }

    @Test
    void cuandoProyectoNoExiste_entoncesDevuelveNotFound() throws Exception {
        Long idInexistente = 999L;

        mockMvc.perform(get("/api/proyectos/{id}", idInexistente))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString("Proyecto no encontrado")));
    }
}
