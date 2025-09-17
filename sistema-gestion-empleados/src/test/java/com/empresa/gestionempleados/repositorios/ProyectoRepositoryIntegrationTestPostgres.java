package com.empresa.gestionempleados.repositorios;

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles("postgres")
public class ProyectoRepositoryIntegrationTestPostgres extends ProyectoRepositoryIntegrationTest {
}
