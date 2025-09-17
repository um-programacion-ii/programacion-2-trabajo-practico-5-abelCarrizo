# Prompts de Testing

## Prompt 1: Diferencias entre distintos tipos de test

### Prompt Utilizado:
```
Cual es la diferencia entre estos test:
Tests unitarios para servicios
Tests de integración para controladores
Tests de repositorios con diferentes bases de datos
? 
Son lo mismo unitarios y de integracion? El de repositorio es unitario o integracion?
```

### Respuesta Recibida:
~~~text
Vamos a desglosarlo con claridad, porque muchas veces los términos “unitario” e “integración” se confunden, y cada capa en tu proyecto tiene un tipo de test recomendado.

---

### 1️⃣ **Tests unitarios para servicios**

* **Qué prueban:** La **lógica de negocio** dentro de tus servicios (`Service`), aislada del resto de la aplicación.
* **Objetivo:** Comprobar que un método haga lo que debe, **sin depender de la base de datos, controladores, o HTTP**.
* **Cómo se hace:**

  * Se usa **Mockito** o algún mock para reemplazar dependencias (`Repository`, otros servicios, etc.).
  * Se ejecutan rápidamente porque no necesitan levantar Spring completo ni conectarse a la BD.
* **Ejemplo:**

```java
@Test
void cuandoBuscarDepartamentoPorId_entoncesDevuelveDepartamento() {
    when(departamentoRepository.findById(1L))
        .thenReturn(Optional.of(new Departamento("Finanzas", "Depto financiero")));

    Departamento depto = departamentoService.buscarPorId(1L);

    assertEquals("Finanzas", depto.getNombre());
}
```

> Esto es **unitario** porque solo pruebas el servicio, no la base de datos ni el controlador.

---

### 2️⃣ **Tests de integración para controladores**

* **Qué prueban:** Que tu **controlador REST funcione correctamente con toda la pila de Spring**, incluyendo:

  * Validaciones.
  * Serialización/JSON.
  * Manejo de excepciones.
  * Llamadas a servicios.
* **Objetivo:** Validar que el endpoint responda correctamente ante distintas situaciones.
* **Cómo se hace:**

  * Se levanta Spring Boot con `@SpringBootTest` y `MockMvc` o `TestRestTemplate`.
  * Puede usar una base de datos en memoria (H2) para que la integración sea completa.
* **Ejemplo:**

```java
mockMvc.perform(get("/api/departamentos/1"))
       .andExpect(status().isOk())
       .andExpect(jsonPath("$.nombre").value("Finanzas"));
```

> Esto es **de integración**, porque incluye varias capas: controlador → servicio → repositorio.

---

### 3️⃣ **Tests de repositorios con diferentes bases de datos**

* **Qué prueban:** Que tus **repositorios JPA funcionen con la base de datos real**.
* **Objetivo:** Verificar que consultas, constraints y relaciones se comporten igual en distintas bases (H2, MySQL, PostgreSQL).
* **Cómo se hace:**

  * Se levanta solo la capa de datos con `@DataJpaTest`.
  * Se puede usar Docker o perfiles de Spring para apuntar a distintas bases de datos.
* **Ejemplo:**

```java
Departamento depto = departamentoRepository.save(new Departamento("IT", "Soporte"));
assertTrue(departamentoRepository.existsById(depto.getId()));
```

> Esto es **de integración**, porque involucra el repositorio y la base de datos real. No es unitario, aunque sea solo una clase.

---

### ⚡ Resumen

| Tipo de test              | Qué prueba                        | Depende de…                          | Base de datos real |
| ------------------------- | --------------------------------- | ------------------------------------ | ------------------ |
| Unitario (servicio)       | Lógica del servicio               | Solo clases, mocks                   | No                 |
| Integración (controlador) | Endpoint y toda la pila de Spring | Servicio + repositorio + controlador | Puede usar H2      |
| Integración (repositorio) | Repositorios JPA y consultas      | Repositorio + DB                     | Sí                 |

> **Conclusión:** Los tests unitarios y de integración **no son lo mismo**.
> Los tests de repositorio con varias bases son tests de integración, no unitarios, porque requieren **una base de datos real** para validar que JPA funcione correctamente.

---

Si quieres, puedo hacer un **diagrama sencillo que muestre las capas y qué tipo de test va en cada una**, para que visualmente quede clarísimo. Esto ayuda mucho a planificar tests en Spring Boot.

¿Quieres que lo haga?

~~~

### Modificaciones Realizadas:
No se realizaron modificaiones

### Explicación del Prompt:
**Motivo**: Comprender las diferencias entre los test solicitados en consigna.

### Aprendizajes Obtenidos:
Aprendí que los test unitarios y de integración no son lo mismo.

## Prompt 2: Error Test `DepartamentoServiceUnitTest`

### Prompt Utilizado:
```
Realice el siguiente test: DepartamentoServiceUnitTest (puedes observarlo adjuntado). Además, te adjuntaré el código necesario para obtener una respuesta mas precisa.
Me ayudas a comprender el error obtenido al crear el test que he realizado?

Error a continuación: (no copié el error porque era demasiado largo)
```

### Respuesta Recibida:
~~~text
¡Perfecto! 🔎 Ahora se entiende el error.

Tu `actualizar` hace esto:

```java
if (!departamentoRepository.existsById(id)) {
    throw new DepartamentoNoEncontradoException("Departamento no encontrado con ID: " + id);
}
departamento.setId(id);
return departamentoRepository.save(departamento);
```

Entonces el problema es que en el **test** solamente mockeamos `findById(...)`, pero en realidad el servicio está usando `existsById(...)`.
👉 Como no está mockeado, Mockito devuelve `false` por defecto, y se lanza la excepción.

---

### Solución en el test

En el `cuandoActualizar_existente_entoncesDevuelveActualizado`, además de mockear el `save`, tenés que mockear el `existsById`:

```java
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
```

---

📌 Resumen:

* Tu método usa `existsById`, no `findById`.
* Mockito necesita que mockeemos exactamente lo que se usa en el método.
* Con ese `when(departamentoRepository.existsById(1L)).thenReturn(true)` ya se arregla el test. 🚀

---

¿Querés que te reescriba el **DepartamentoServiceUnitTest** completo con esta corrección incluida?

~~~

### Modificaciones Realizadas:
Se actualizó el metodo `cuandoActualizar_existente_entoncesDevuelveActualizado` del test `DepartamentoServiceUnitTest`

### Explicación del Prompt:
*Motivo*: No logré identificar el motivo por el cual obtenia este error.

### Aprendizajes Obtenidos:
Entendí que en los tests unitarios, hay que replicar exactamente las interacciones con los mocks que el método va a llamar.

---

## Prompt 3: Error Test `EmpleadoServiceUnitTest`

### Prompt Utilizado:
```
Realice el siguiente test: EmpleadoServiceUnitTest (puedes observarlo adjuntado). Además, te adjuntaré el código necesario para obtener una respuesta mas precisa.
Me ayudas a comprender el error obtenido al crear el test que he realizado?

Error a continuación: (no copié el error porque era demasiado largo)
```

### Respuesta Recibida:
~~~text
Ah, ahora entiendo exactamente el problema del test fallido:

En el test `cuandoActualizarExistente_entoncesDevuelveActualizado` estabas haciendo esto:

```java
when(empleadoRepository.existsById(empleado.getId())).thenReturn(true);
when(empleadoRepository.save(any(Empleado.class))).thenReturn(empleado); // << problema
```

Aquí está el error: **el mock `save` devuelve siempre el objeto original `empleado`**, que tiene nombre `"Juan"`. Entonces, aunque pases un objeto con nombre `"Carlos"` al servicio, el mock te devuelve `"Juan"` y el test falla.

---

### Solución

Hay que devolver el **mismo objeto que recibe `save`**:

```java
when(empleadoRepository.save(any(Empleado.class))).thenAnswer(invocation -> invocation.getArgument(0));
```

Esto hace que el mock devuelva **el empleado actualizado** que le pasaste al método `save`.

---

### Test corregido:

```java
@Test
void cuandoActualizarExistente_entoncesDevuelveActualizado() {
    when(empleadoRepository.existsById(empleado.getId())).thenReturn(true);
    // Cambiado de .thenReturn(empleado) a .thenAnswer(...)
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
```

---

Con este cambio, el test unitario **ya no fallará** y reflejará correctamente los valores del objeto actualizado. ✅

Si querés, puedo actualizar **todos los tests que usan `save` en `EmpleadoServiceUnitTest`** para que sean consistentes con esta técnica y evitar errores similares.

¿Querés que haga eso?
~~~

### Modificaciones Realizadas:
Se actualizó el metodo `cuandoActualizarExistente_entoncesDevuelveActualizado` del test `EmpleadoServiceUnitTest`

### Explicación del Prompt:
**Motivo**: No logré identificar el motivo por el cual obtenia este error.

### Aprendizajes Obtenidos:
Entendí que en los tests unitarios, hay que replicar exactamente las interacciones con los mocks que el método va a llamar.

---

## Prompt 4: Ejecución de Test

### Prompt Utilizado:
```
Como puedo ejecutar los test?
```

### Respuesta Recibida:
~~~text
Muy buena pregunta 👌.

En tu proyecto **Spring Boot con Maven/Gradle**, los tests que acabamos de escribir están bajo `src/test/java/...`, por lo tanto se pueden ejecutar de varias formas:

---

## 🚀 1. Desde la línea de comandos (Maven o Gradle)

### Si usas **Maven** (lo más común en Spring Boot):

```bash
mvn test
```

👉 Ejecuta **todos los tests** del proyecto.

Para ejecutar un test específico, por ejemplo solo `EmpleadoServiceTest`:

```bash
mvn -Dtest=EmpleadoServiceTest test
```

Incluso puedes correr un único método de test:

```bash
mvn -Dtest=EmpleadoServiceTest#cuandoEliminarEmpleado_entoncesYaNoExisteEnBD test
```

---

### Si usas **Gradle**:

```bash
./gradlew test
```

Un test específico:

```bash
./gradlew test --tests "com.empresa.gestionempleados.servicios.EmpleadoServiceTest"
```

Un método específico:

```bash
./gradlew test --tests "com.empresa.gestionempleados.servicios.EmpleadoServiceTest.cuandoEliminarEmpleado_entoncesYaNoExisteEnBD"
```

---

## 🚀 2. Desde tu IDE (IntelliJ / Eclipse / VS Code)

* Abre la clase de test, por ejemplo `EmpleadoServiceTest`.
* Haz clic derecho sobre la clase o sobre el método.
* Selecciona **Run Test**.
  👉 Esto es muy útil si quieres probar un test puntual mientras desarrollas.

---

## 🚀 3. Con Docker / CI (más adelante)

Cuando tengas tu `docker-compose` configurado y quieras correr los tests en un pipeline CI/CD, normalmente usas el mismo comando Maven/Gradle dentro del contenedor, por ejemplo:

```bash
docker compose run --rm app mvn test
```

(donde `app` es el servicio de tu aplicación definido en el `docker-compose.yml`).

---

👉 Te recomiendo ahora correr:

```bash
mvn test
```

para verificar que los tests de `DepartamentoServiceTest`, `EmpleadoServiceTest` y `ProyectoServiceTest` se ejecuten correctamente.

---

¿Querés que te arme también un **README con instrucciones de ejecución de tests** (como parte de la documentación de la Etapa 4), para que quede bien claro en tu proyecto?

~~~

### Modificaciones Realizadas:
No se realizaron modificaciones.

### Explicación del Prompt:
**Motivo**: Verificar el correcto funciomiento de los test

### Aprendizajes Obtenidos:
Entendí como debo ejecutar test de una manera general e individual.

---

## Prompt 5: Error Test `DepartamentoControllerIntegrationTest`

### Prompt Utilizado:
```
Realice el siguiente test: DepartamentoControllerIntegrationTest (puedes observarlo adjuntado). Además, te adjuntaré el código necesario para obtener una respuesta mas precisa.
Me ayudas a comprender el error obtenido al crear el test que he realizado?

Error a continuación: (no copié el error porque era demasiado largo)
```

### Respuesta Recibida:
~~~text
¡Perfecto! Ahora entiendo exactamente el problema: tus tests que esperan un **404** al consultar, actualizar o eliminar un departamento inexistente **están fallando**, no porque tu lógica de negocio esté mal, sino porque **Spring no está manejando la excepción `DepartamentoNoEncontradoException` para devolver un 404**, sino que la deja “explotar” hacia MockMvc como un error de servlet.

En otras palabras, tu controlador lanza la excepción, pero **no hay un manejador global de excepciones que la transforme en una respuesta HTTP 404**.

---

### 🔹 Cómo solucionarlo

1. Crea un manejador global con `@ControllerAdvice` para tus excepciones personalizadas:

```java
package com.empresa.gestionempleados.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DepartamentoNoEncontradoException.class)
    public ResponseEntity<String> manejarDepartamentoNoEncontrado(DepartamentoNoEncontradoException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    // Puedes agregar más handlers para otras excepciones si quieres
}
```

2. Con esto, cada vez que tu servicio lance `DepartamentoNoEncontradoException`, **MockMvc recibirá un 404** en vez de un error de servlet.

3. Ahora tus tests deberían pasar, porque las llamadas a departamentos inexistentes devolverán 404 como se esperaba:

```java
mockMvc.perform(get("/api/departamentos/{id}", 999L))
       .andExpect(status().isNotFound());
```

---

💡 **Tip:** Esto aplica para todas tus entidades: Empleado, Proyecto, etc. Si quieres, puedo escribir un **`GlobalExceptionHandler` completo** que maneje **Departamento, Empleado y Proyecto** de una sola vez para que todos los tests de integración queden correctos.

¿Quieres que haga eso?
~~~

### Modificaciones Realizadas:
Se añadió `GlobalExceptionHandler` en la carpeta `exceptions`.

### Explicación del Prompt:
**Motivo**: No logré identificar el motivo por el cual obtenia este error.

### Aprendizajes Obtenidos:
Entendí que como los tests de controladores simulan peticiones HTTP (GET, PUT, DELETE) usando MockMvc y el método lanza DepartamentoNoEncontradoException, Spring no sabe automáticamente que eso debería ser un 404, así que el test lo ve como un error de servidor (5xx) y falla.

---

## Prompt 6: Error test `EmpleadoControllerIntegrationTest`

### Prompt Utilizado:
```
Realice el siguiente test: EmpleadoControllerIntegrationTest (puedes observarlo adjuntado). Además, te adjuntaré el código necesario para obtener una respuesta mas precisa.
Me ayudas a comprender el error obtenido al crear el test que he realizado?

Error a continuación: (no copié el error porque era demasiado largo)
```

### Respuesta Recibida:
~~~text
Ah, perfecto. Ahora el problema está claro: el **POST de creación de empleado devuelve `201 Created`**, no `200 OK`. En el test que verifica el rango salarial habíamos puesto `.andExpect(status().isOk())` justo después del `POST`, lo cual genera el error que estás viendo.

La solución es **esperar el 201** al crear el empleado y luego hacer el GET para el rango salarial. Aquí tienes la versión corregida y lista para ejecutar:

```java
@Test
public void cuandoObtenerPorRangoSalario_entoncesDevuelveLista() throws Exception {
    // Crear empleado de prueba
    Empleado empleado = new Empleado();
    empleado.setNombre("Maria");
    empleado.setApellido("Gimenez");
    empleado.setEmail("maria.gimenez@empresa.com");
    empleado.setFechaContratacion(LocalDate.now());
    empleado.setSalario(new BigDecimal("40000"));

    // Guardar el empleado y esperar 201 Created
    mockMvc.perform(post("/api/empleados")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(empleado)))
            .andExpect(status().isCreated());  // <-- Cambio aquí

    // Consultar por rango salarial y esperar 200 OK
    mockMvc.perform(get("/api/empleados/salario")
                    .param("min", "30000")
                    .param("max", "50000"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].salario").value(40000.0));
}
```

✅ Cambios clave:

1. `isOk()` → `isCreated()` después del `POST`.
2. El `GET` mantiene `isOk()` porque ahí sí esperamos 200.

Con esto, **el test debería pasar sin errores ni fallos**.

Si quieres, puedo enviarte **el archivo completo `EmpleadoControllerIntegrationTest.java` actualizado con este cambio listo para copiar y pegar**.

¿Quieres que haga eso?
~~~

### Modificaciones Realizadas:
Se actualizó el metodo `cuandoObtenerPorRangoSalario_entoncesDevuelveLista()` del test `EmpleadoControllerIntegrationTest`.

### Explicación del Prompt:
**Motivo**: No logré identificar el motivo por el cual obtenia este error.

### Aprendizajes Obtenidos:
Entendí que los tests deben reflejar exactamente el comportamiento esperado de la API, incluidos los códigos de estado

---

## Prompt 7: Ejecutar test de diferentes bases de datos

### Prompt Utilizado:
```
Cómo puedo ejecutar los mismos tests de repositorio con H2 (por defecto), MySQL y PostgreSQL?
```

### Respuesta Recibida:
~~~text
Perfecto. Dado que ya tienes tus bases de datos definidas en Docker y perfiles (`application-mysql.yml` y `application-postgres.yml`), podemos adaptar los tests para que sean **parametrizables por perfil** y **ejecuten los mismos tests de repositorio** contra H2 (por defecto), MySQL y PostgreSQL.

Para **MySQL y PostgreSQL**, simplemente duplicamos la clase y cambiamos el `@ActiveProfiles`:

```java
// Para MySQL
@DataJpaTest
@ActiveProfiles("mysql")
class DepartamentoRepositoryIntegrationTestMySQL extends DepartamentoRepositoryIntegrationTestH2 {}

// Para PostgreSQL
@DataJpaTest
@ActiveProfiles("postgres")
class DepartamentoRepositoryIntegrationTestPostgres extends DepartamentoRepositoryIntegrationTestH2 {}
```

✅ Ventajas de este enfoque:

1. No repites los métodos de test: solo heredas la clase base.
2. Cada perfil usa su propia base de datos configurada en Docker.
3. Spring levanta automáticamente el `DataSource` según el perfil (`application-mysql.yml`, `application-postgres.yml`).

---

Si quieres, puedo hacer una **versión unificada usando Testcontainers** que **levante MySQL y PostgreSQL en contenedores automáticamente** sin depender de que ya estén corriendo, así los tests son más portables y reproducibles. Esto evita depender de tu `docker-compose` externo.

¿Quieres que haga esa versión con Testcontainers también?

~~~

### Modificaciones Realizadas:
Se añadieron los test de repositorios correspondientes para su ejecucion en cada base de datos.

### Explicación del Prompt:
**Motivo**: Entender como ejecutar los test en distintas bases de datos.

### Aprendizajes Obtenidos:
Entendí como crear los test para diferentes bases de datos.
