# Prompts de Desarrollo

## Prompt 1: Relaciones entre entidades

### Prompt Utilizado:
```
Me ayudas a comprender como funciona las relaciones entre las distintas entidades, segun el ejemplo de implementacion que se me ha compartido?

Ejemplo de implementación:

```java
@Entity
@Table(name = "empleados")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Empleado {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 100)
    private String nombre;
    
    @Column(nullable = false, length = 100)
    private String apellido;
    
    @Column(unique = true, nullable = false)
    private String email;
    
    @Column(name = "fecha_contratacion", nullable = false)
    @Temporal(TemporalType.DATE)
    private LocalDate fechaContratacion;
    
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal salario;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "departamento_id")
    private Departamento departamento;
    
    @ManyToMany
    @JoinTable(
        name = "empleado_proyecto",
        joinColumns = @JoinColumn(name = "empleado_id"),
        inverseJoinColumns = @JoinColumn(name = "proyecto_id")
    )
    private Set<Proyecto> proyectos = new HashSet<>();
}

@Entity
@Table(name = "departamentos")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Departamento {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 100, unique = true)
    private String nombre;
    
    @Column(length = 500)
    private String descripcion;
    
    @OneToMany(mappedBy = "departamento", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Empleado> empleados = new ArrayList<>();
}

@Entity
@Table(name = "proyectos")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Proyecto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 100)
    private String nombre;
    
    @Column(length = 1000)
    private String descripcion;
    
    @Column(name = "fecha_inicio")
    @Temporal(TemporalType.DATE)
    private LocalDate fechaInicio;
    
    @Column(name = "fecha_fin")
    @Temporal(TemporalType.DATE)
    private LocalDate fechaFin;
    
    @ManyToMany(mappedBy = "proyectos")
    private Set<Empleado> empleados = new HashSet<>();
}
```

### Respuesta Recibida:
~~~text
¡Claro! 🚀 Vamos a desmenuzar cómo funcionan las **relaciones entre las entidades** en tu proyecto, usando **JPA/Hibernate**.

---

# 🔹 Relaciones en JPA (con tus entidades como ejemplo)

## 1️⃣ **Empleado ↔ Departamento**

* **Regla de negocio**: un empleado pertenece a un solo departamento, y un departamento tiene muchos empleados.

### En código:

📌 Lado **Empleado**

```java
@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "departamento_id")
private Departamento departamento;
```

👉 Esto significa:

* En la tabla **empleados** se creará una columna `departamento_id`.
* Cada empleado apunta a un departamento específico.

📌 Lado **Departamento**

```java
@OneToMany(mappedBy = "departamento", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
private List<Empleado> empleados = new ArrayList<>();
```

👉 Esto significa:

* Un departamento tiene una **lista de empleados**.
* `mappedBy = "departamento"` dice: *"no crees otra columna extra, usa la que ya existe en Empleado (`departamento_id`)."*

➡️ **Ejemplo real**:

* `Empleado Juan` tiene `departamento_id = 1`.
* En `Departamento` con id=1, dentro de su lista `empleados` aparecerá Juan.

---

## 2️⃣ **Empleado ↔ Proyecto**

* **Regla de negocio**: un empleado puede trabajar en varios proyectos, y un proyecto puede tener varios empleados.

### En código:

📌 Lado **Empleado**

```java
@ManyToMany
@JoinTable(
    name = "empleado_proyecto",
    joinColumns = @JoinColumn(name = "empleado_id"),
    inverseJoinColumns = @JoinColumn(name = "proyecto_id")
)
private Set<Proyecto> proyectos = new HashSet<>();
```

👉 Esto significa:

* Se crea una tabla intermedia llamada **empleado\_proyecto**.
* Tendrá dos columnas: `empleado_id` y `proyecto_id`.

📌 Lado **Proyecto**

```java
@ManyToMany(mappedBy = "proyectos")
private Set<Empleado> empleados = new HashSet<>();
```

👉 Esto significa:

* El `Proyecto` sabe qué empleados están asignados, **pero no crea otra tabla**.
* Usa la tabla `empleado_proyecto` definida en `Empleado`.

➡️ **Ejemplo real**:

* Juan está en los proyectos **A** y **B**.
* En la tabla `empleado_proyecto` habrá:

  ```
  empleado_id | proyecto_id
  ------------|------------
        1     |     10    (Juan - Proyecto A)
        1     |     11    (Juan - Proyecto B)
  ```
* Al consultar el proyecto A, verás que **Juan** está en su lista de empleados.

---

## 3️⃣ Direccionalidad de las relaciones

En JPA, las relaciones pueden ser:

* **Unidireccionales** → solo un lado conoce la relación.
* **Bidireccionales** → ambos lados se conocen.

En tu código:

* **Empleado ↔ Departamento** es **bidireccional** (Empleado sabe su departamento, y Departamento sabe sus empleados).
* **Empleado ↔ Proyecto** es **bidireccional** (Empleado conoce sus proyectos, y Proyecto conoce sus empleados).

---

## 4️⃣ Carga de datos (`fetch`)

* `LAZY` → carga diferida (solo trae la relación cuando la consultas).
  Ejemplo: si traes un empleado, no se cargan sus proyectos hasta que hagas `empleado.getProyectos()`.
* `EAGER` → carga inmediata (trae la relación automáticamente junto con la entidad principal).
  🚨 Se recomienda **LAZY** en la mayoría de los casos para evitar consultas gigantes.

---

## 🔹 Resumen visual

```
Empleado  --(ManyToOne)-->  Departamento
  ↑                            ↓
  └-------(OneToMany)---------┘

Empleado  --(ManyToMany)-->  Proyecto
```

---

👉 En pocas palabras:

* **Departamento** agrupa empleados.
* **Empleado** pertenece a un departamento y puede estar en varios proyectos.
* **Proyecto** puede tener muchos empleados.

---

¿Quieres que te arme un **ejemplo con código de prueba** (creando objetos en Java y asignando relaciones) para que lo veas en acción antes de meter repositorios?

~~~

### Modificaciones Realizadas:
No se realizó ninguna modificación en el código

### Explicación del Prompt:
**Motivo**: Comprender el funcionamiento de las relaciones en java entre las distintas entidades.

### Aprendizajes Obtenidos:
Entendí cómo se representan las reglas del negocio en el modelo JPA, cómo se traduce eso a tablas en la base de datos, y qué hacen las anotaciones en el código.

---

## Prompt 2: Comprender estructura de interfaces e implementaciones

### Prompt Utilizado:
~~~text
Necesito que me ayudes a comprender el funcionamiento de algunos elementos en el codigo. Dentro de los ejemplos de implementación se me compartieron los siguiente tarea:

---

Crear interfaces de repositorio:
   - `EmpleadoRepository`
   - `DepartamentoRepository`
   - `ProyectoRepository`

Implementar servicios:
   
   - `EmpleadoService` (interface)
   - `EmpleadoServiceImpl` (implementación)
   - `DepartamentoService` (interface)
   - `DepartamentoServiceImpl` (implementación)
   - `ProyectoService` (interface)
   - `ProyectoServiceImpl` (implementación)

---

Pero antes de realizarla me gustaría comprender como es que funcionan los métodos de los ejemplos de implementación que se me han compartido. Son los siguientes: EmpleadoRepository, EmpleadoServiceImpl. Ayudame a comprender los siguientes metodos de cada ejemplo:
De `EmpleadoReposity` ayudame a comprender los decoradores de este metodo:
```java
@Query("SELECT e FROM Empleado e WHERE e.departamento.nombre = :nombreDepartamento")
    List<Empleado> findByNombreDepartamento(@Param("nombreDepartamento") String nombreDepartamento);
``
¿Que @Query y que es @Param?

De EmpleadoServiceImpl ayudame a comprender los decoradores: @Service, @Transactional.

```java
@Service
@Transactional
public class EmpleadoServiceImpl implements EmpleadoService {}
```
~~~

### Respuesta Recibida:
~~~text
Muy buena pregunta 👌. Te explico cada cosa paso a paso para que lo entiendas de manera clara:

---

## 📌 En `EmpleadoRepository`

### `@Query`

* Esta anotación pertenece a **Spring Data JPA**.
* Sirve para definir consultas **personalizadas en JPQL** (Java Persistence Query Language).
* En tu ejemplo:

  ```java
  @Query("SELECT e FROM Empleado e WHERE e.departamento.nombre = :nombreDepartamento")
  List<Empleado> findByNombreDepartamento(@Param("nombreDepartamento") String nombreDepartamento);
  ```

  * `"SELECT e FROM Empleado e ..."` → es la consulta escrita en **JPQL**, no en SQL directo. Aquí `Empleado` es la entidad, no la tabla.
  * `e.departamento.nombre` → accede a la relación de `Empleado` con `Departamento` y filtra por el campo `nombre`.
  * `:nombreDepartamento` → es un **parámetro nombrado**, que se reemplaza por el valor que recibe el método.

### `@Param`

* Permite vincular el parámetro del método con el parámetro nombrado de la consulta (`:nombreDepartamento`).
* Ejemplo:

  ```java
  findByNombreDepartamento("Ventas")
  ```

  Internamente Spring sustituirá `:nombreDepartamento` por `"Ventas"`.

📌 En resumen:

* `@Query` → defines una consulta personalizada.
* `@Param` → conectas el valor que pasas al método con el parámetro de la consulta.

---

## 📌 En `EmpleadoServiceImpl`

```java
@Service
@Transactional
public class EmpleadoServiceImpl implements EmpleadoService {}
```

### `@Service`

* Es una **estereotipa de Spring**.
* Indica que esta clase es un **servicio** dentro de la arquitectura.
* Spring la detecta automáticamente (con *component scan*) y la registra como un **bean** para poder inyectarla en otras partes del proyecto.
* Conceptualmente, la capa de servicio se encarga de la **lógica de negocio** (lo que está entre el controlador y el repositorio).

### `@Transactional`

* Es de **Jakarta Transactions** (antes javax).
* Indica que los métodos de la clase se ejecutarán dentro de una **transacción** de base de datos.
* Significa:

  * Si todo el método se ejecuta bien → **se confirma (commit)** la transacción.
  * Si ocurre un error (excepción) → **se revierte (rollback)** automáticamente.
* Esto protege la consistencia de los datos y evita estados intermedios corruptos.

📌 En resumen:

* `@Service` → Marca la clase como servicio de negocio.
* `@Transactional` → Controla las transacciones de BD, asegurando que los métodos sean atómicos (todo o nada).

---

👉 Entonces:

* En el **repositorio**, las anotaciones `@Query` y `@Param` definen cómo se hacen consultas personalizadas.
* En el **servicio**, `@Service` y `@Transactional` definen la capa de negocio y aseguran la integridad de las operaciones con BD.

---

¿Quieres que te prepare un **ejemplo paso a paso de ejecución real** de `findByNombreDepartamento("Ventas")` para que veas cómo interactúan repositorio y servicio?

~~~

### Modificaciones Realizadas:
No se realizó ninguna modificación

### Explicación del Prompt:
*Motivo*: Comprender la estructura del ejemplo de implementación presentado en la consigna

### Aprendizajes Obtenidos:
Aprendí cómo se conectan las capas de repositorio y servicio, cómo se hacen consultas personalizadas y cómo se manejan las transacciones y la inyección de dependencias en Spring Boot.

---

## Prompt 3: [Descripción breve]

### Prompt Utilizado:
~~~text
Necesito que me ayudes a comprender el funcionamiento de algunos elementos en el codigo. Dentro de los ejemplos de implementación se me compartieron las siguientes tareas:

---
1. Crear controladores REST:
   - `EmpleadoController`
   - `DepartamentoController`
   - `ProyectoController`
---

Pero antes de realizarla me gustaría comprender como es que funcionan los métodos de los ejemplos de implementación que se me han compartido. Son los siguientes: EmpleadoController. Ayudame a comprender los siguientes metodos de cada ejemplo:

De la clase:
```java
@RestController
@RequestMapping("/api/empleados")
@Validated
public class EmpleadoController {}
```
¿Que es RestController, @RequestMapping("/api/empleados" ) y @Validated?

De los metodos de EmpleadoController:

```java
@PutMapping("/{id}")
public Empleado actualizar(@PathVariable Long id, @Valid @RequestBody Empleado empleado) {
        return empleadoService.actualizar(id, empleado);
    }

@DeleteMapping("/{id}")
@ResponseStatus(HttpStatus.NO_CONTENT)
public void eliminar(@PathVariable Long id) {
        empleadoService.eliminar(id);
    }

@GetMapping("/salario")
public List<Empleado> obtenerPorRangoSalario(
            @RequestParam BigDecimal min, 
            @RequestParam BigDecimal max) {
        return empleadoService.buscarPorRangoSalario(min, max);```

¿Que es @PathVariable, @Valid, @RequestBody, @ResponseStatus(HttpStatus.NO_CONTENT), @RequestParam ? Explicame su funcionamiento general, no el funciomiento especifico por cada metodo.
~~~

### Respuesta Recibida:
~~~text
¡Perfecto! Vamos a desglosarlo paso a paso para que entiendas **el funcionamiento general** de cada anotación y componente que mencionaste en `EmpleadoController`. Te lo explico de manera informal y clara.

---

## **Clase:**

```java
@RestController
@RequestMapping("/api/empleados")
@Validated
public class EmpleadoController {}
```

### 1️⃣ `@RestController`

* Es una **combinación de dos cosas**: `@Controller` + `@ResponseBody`.
* Le dice a Spring que esta clase **maneja peticiones HTTP** y que **los métodos devuelven directamente datos JSON o XML** (no vistas HTML).
* Ejemplo: si un método devuelve un `Empleado`, el cliente recibe **un JSON con los datos del empleado**, no una página web.

### 2️⃣ `@RequestMapping("/api/empleados")`

* Define la **ruta base** para todos los endpoints de esta clase.
* En este caso, todos los métodos del controller van a empezar con `/api/empleados`.
* Ejemplo:

  * `GET /api/empleados` → obtiene todos los empleados
  * `GET /api/empleados/1` → obtiene el empleado con ID 1

### 3️⃣ `@Validated`

* Activa la **validación automática** de datos en los métodos que usan `@Valid`.
* Es necesario para que Spring revise **las reglas de validación** definidas en las entidades (`@NotNull`, `@Size`, etc.) antes de ejecutar la lógica del método.
* Si un dato no cumple con las reglas, Spring lanza automáticamente una excepción y devuelve un error 400 al cliente.

---

## **Métodos y sus anotaciones**

### 1️⃣ `@PutMapping("/{id}")`

* `@PutMapping` indica que el método responde a **peticiones HTTP PUT** (usadas para actualizar recursos).
* `("/{id}")` indica que espera un **valor dinámico en la URL**, que luego se puede usar dentro del método.

#### Anotaciones dentro del método:

* `@PathVariable Long id`

  * Toma el valor que viene en la URL (`/empleados/1`) y lo asigna a la variable `id`.
  * Ejemplo: si la URL es `/empleados/5`, entonces `id = 5`.

* `@Valid @RequestBody Empleado empleado`

  * `@RequestBody`: indica que los datos del empleado vienen **en el cuerpo de la petición HTTP** (generalmente en JSON).
  * `@Valid`: le dice a Spring que **valide esos datos** según las reglas definidas en la entidad antes de pasarlos al método.

---

### 2️⃣ `@DeleteMapping("/{id}")`

* `@DeleteMapping` indica que el método responde a **peticiones HTTP DELETE** (usadas para eliminar recursos).

* `@ResponseStatus(HttpStatus.NO_CONTENT)`

  * Define el **código HTTP de respuesta** que se enviará si el método se ejecuta correctamente.
  * `HttpStatus.NO_CONTENT` (204) significa “ok, se eliminó, pero no devuelvo contenido en la respuesta”.

* `@PathVariable Long id`

  * Igual que antes, toma el valor de la URL para identificar qué empleado eliminar.

---

### 3️⃣ `@GetMapping("/salario")`

* `@GetMapping` indica que el método responde a **peticiones HTTP GET** (usadas para obtener recursos).

* `("/salario")` indica la ruta relativa, quedando `/api/empleados/salario`.

* `@RequestParam BigDecimal min, @RequestParam BigDecimal max`

  * Toma **parámetros enviados en la URL** como query string:

    ```
    /api/empleados/salario?min=2000&max=5000
    ```
  * `min` y `max` se pasan al método como variables para filtrar datos.

---

### ✅ Resumen rápido de las anotaciones importantes:

| Anotación                                      | Función general                                                        |
| ---------------------------------------------- | ---------------------------------------------------------------------- |
| `@RestController`                              | Clase que maneja HTTP y devuelve JSON/XML directamente.                |
| `@RequestMapping`                              | Define la ruta base de todos los métodos del controller.               |
| `@Validated`                                   | Activa la validación automática de datos (`@Valid`).                   |
| `@PutMapping`, `@GetMapping`, `@DeleteMapping` | Indican el tipo de petición HTTP que maneja el método.                 |
| `@PathVariable`                                | Toma un valor de la URL y lo pasa al método.                           |
| `@RequestBody`                                 | Toma el cuerpo de la petición (JSON) y lo convierte en un objeto Java. |
| `@Valid`                                       | Valida automáticamente el objeto según reglas definidas en la entidad. |
| `@RequestParam`                                | Toma parámetros de la URL (query string) y los pasa al método.         |
| `@ResponseStatus`                              | Define el código HTTP que devolverá el método.                         |

---

Si querés, en el próximo paso puedo hacer un **diagrama simple mostrando cómo fluye una petición HTTP** desde el cliente hasta `EmpleadoController`, para que veas cómo interactúan todas estas anotaciones. Esto suele aclarar mucho la lógica.

¿Querés que haga eso?
~~~

### Modificaciones Realizadas:
No se realizaron modificaciones

### Explicación del Prompt:
**Motivo**: Comprender la estructura del código del ejemplo de implementación de la consigna compartida.

### Aprendizajes Obtenidos:
Entendí qué hace cada anotación en un controller REST y cómo Spring procesa automáticamente la información de las peticiónes.

---

## Prompt 4: Ubicación del Profile

### Prompt Utilizado:
```
Debo realizar la siguiente consgina:
Configurar profiles: - dev (H2 en memoria) - mysql (MySQL con Docker) - postgres (PostgreSQL con Docker)
¿En donde se ubican los archivos de configuracion de profiles?¿Es un archivo existente o debo crearlo?
```

### Respuesta Recibida:
~~~text
En un proyecto Spring Boot, el archivo `application.yml` que contiene profiles **no siempre existe por defecto**, así que puedes **crear uno** si aún no lo tienes.

### Ubicación correcta

Debes crearlo en:

```
src/main/resources/application.yml
```

* `src/main/resources` es la carpeta donde Spring Boot busca automáticamente los archivos de configuración (`application.properties` o `application.yml`).
* Spring cargará el profile activo definido dentro del mismo archivo o mediante variables externas.

### Pasos para crear el archivo

1. En tu IDE, ve a `src/main/resources`.
2. Crea un nuevo archivo llamado `application.yml`.
3. Pega la configuración de profiles que ya tienes.

Después de eso, Spring Boot reconocerá automáticamente los profiles `dev`, `mysql` y `postgres`.

Si quieres, puedo decirte cómo **activar un profile distinto al arrancar la aplicación** para probar MySQL o PostgreSQL. ¿Quieres que haga eso?

~~~

### Modificaciones Realizadas:
Se creó el archivo `application.yml` en la ruta indicada por la IA `src/main/resources/`

### Explicación del Prompt:
**Motivo**: Aprender las buenas practicas con respecto a la estructuración del proyecto.

### Aprendizajes Obtenidos:
Entendí en donde ubicar el archivo de configuración de los profiles.

---

## Prompt 5: Profiles

### Prompt Utilizado:
```
Es correcto ubicar application.properties y application.yml en la misma ubicacion?
No deberia ubicar la configuracion en un mismo archivo?
```

### Respuesta Recibida:
~~~text
Sí 🙂, puedes tener **ambos archivos** (`application.properties` y `application.yml`) en `src/main/resources`.

Spring Boot sigue un **orden de prioridad** para cargar configuraciones. En pocas palabras:

* Si tienes **los dos archivos**, **ambos se cargan** y Spring combina las propiedades.
* Si una misma propiedad está en **los dos archivos**, la de `application.properties` tiene prioridad sobre la de `application.yml`.

### Ejemplo práctico:

* `application.properties`

  ```properties
  server.port=8081
  ```
* `application.yml`

  ```yaml
  server:
    port: 8080
  ```

👉 En este caso, la aplicación arrancará en el puerto **8081**, porque `properties` pisa lo que dice `yml`.

---

🔹 **Recomendación**:
Lo más común es usar **solo uno de los dos formatos** para mantener consistencia.

* Si ya empezaste con `yml`, mejor seguir con `application.yml`.
* Si tenías configuraciones viejas en `properties`, puedes migrarlas al `yml` y borrar el `.properties`.

¿Querés que te muestre cómo migrar un `application.properties` que ya tengas a `application.yml` para que quede todo unificado?
~~~

### Modificaciones Realizadas:
No se realizaron cambios.

### Explicación del Prompt:
**Motivo**: Evitar errores durante la ejecución.

### Aprendizajes Obtenidos:
Entendí que puedo tener ambos archivos ya que Spring sigue un orden. Aun asi, decidí mantener `application.yml`.

---

## Prompt 6: Verificar el correcto funcionamiento

### Prompt Utilizado:
```
Ahora como puedo probar que todo funciona bien?
```

### Respuesta Recibida:
~~~text
Perfecto, ahora que ya tienes tus **controllers y profiles configurados**, podés probarlos de varias formas. Te paso los pasos más claros:

---

## 🔹 1. Levantar la aplicación

En la raíz de tu proyecto:

```bash
./mvnw spring-boot:run
```

o si usás Gradle:

```bash
./gradlew bootRun
```

Esto arranca la aplicación con el **profile activo** (`dev` por defecto, con H2 en memoria).
El backend debería estar corriendo en:

```
http://localhost:8080
```

---

## 🔹 2. Probar los endpoints

Podés hacerlo con herramientas como:

* **Postman** (recomendado)
* **cURL** en la terminal
* **IntelliJ HTTP Client** o VSCode REST Client

### Ejemplos:

* **Obtener todos los empleados**

  ```bash
  curl -X GET http://localhost:8080/api/empleados
  ```

* **Obtener empleado por id**

  ```bash
  curl -X GET http://localhost:8080/api/empleados/1
  ```

* **Crear un nuevo empleado**

  ```bash
  curl -X POST http://localhost:8080/api/empleados \
       -H "Content-Type: application/json" \
       -d '{"nombre": "Juan", "salario": 3000, "departamento": {"id": 1}}'
  ```

* **Actualizar empleado**

  ```bash
  curl -X PUT http://localhost:8080/api/empleados/1 \
       -H "Content-Type: application/json" \
       -d '{"nombre": "Juan Pérez", "salario": 3500, "departamento": {"id": 1}}'
  ```

* **Eliminar empleado**

  ```bash
  curl -X DELETE http://localhost:8080/api/empleados/1
  ```

* **Buscar por departamento**

  ```bash
  curl -X GET http://localhost:8080/api/empleados/departamento/Ventas
  ```

* **Buscar por rango de salario**

  ```bash
  curl -X GET "http://localhost:8080/api/empleados/salario?min=2000&max=4000"
  ```

---

## 🔹 3. Revisar la consola H2

Ya que estás usando el profile `dev`, podés abrir:

```
http://localhost:8080/h2-console
```

* **JDBC URL**: `jdbc:h2:mem:testdb`
* **User**: `sa`
* **Password**: (vacío)

Ahí podés ver las tablas y datos insertados.

---

## 🔹 4. Cambiar de profile (para MySQL/Postgres)

Si querés probar MySQL o PostgreSQL en lugar de H2:

```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=mysql
```

o

```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=postgres
```

---

👉 Con esto ya podés **crear, leer, actualizar, borrar y consultar empleados** desde tu API.

¿Querés que te arme un **script de prueba en Postman** con todos estos endpoints listos para importar y testear?
~~~

### Modificaciones Realizadas:
No se realizaron modificaciones

### Explicación del Prompt:
**Motivo**: Verificar el correcto funcionamiento. 

### Aprendizajes Obtenidos:
Aprendí a utilizar determinados comandos para ejecutar el proyecto.