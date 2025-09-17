# Prompts de Docker

## Prompt 1: Levantar contenedores.

### Prompt Utilizado:
```
Necesito tu ayuda para levantar contenedores docker.
Nombre del archivo de configuracion: docker-compose.yml
Nombre de los contenedores: empleados_mysql, empleados_postgres
A continuacion te compartire el archivo de configuracion para obtener mayor precision en tu respuesta:

---

# docker-compose.yml
services:
  mysql:
    image: mysql:8.0
    container_name: empleados_mysql
    environment:
      MYSQL_ROOT_PASSWORD: root_password
      MYSQL_DATABASE: empleados_db
      MYSQL_USER: empleados_user
      MYSQL_PASSWORD: empleados_pass
    ports:
      - "3306:3306"
    volumes:
      - mysql_data:/var/lib/mysql
    networks:
      - empleados_network
    healthcheck:
      test: ["CMD", "mysqladmin", "ping", "-h", "localhost"]
      timeout: 20s
      retries: 10

  postgres:
    image: postgres:16
    container_name: empleados_postgres
    environment:
      POSTGRES_DB: empleados_db
      POSTGRES_USER: empleados_user
      POSTGRES_PASSWORD: empleados_pass
    ports:
      - "5432:5432"
    volumes:
      - postgres_data:/var/lib/postgresql/data
    networks:
      - empleados_network
    healthcheck:
      test: ["CMD-SHELL", "pg_isready -U empleados_user -d empleados_db"]
      timeout: 20s
      retries: 10

volumes:
  mysql_data:
  postgres_data:

networks:
  empleados_network:
    driver: bridge

---
```

### Respuesta Recibida:

~~~text
Perfecto 🚀 Comencemos.

👉 Para ejecutar ese `docker-compose.yml` necesitas seguir estos pasos:

---

### 📂 Ubicación del archivo

* El archivo `docker-compose.yml` debe estar en la **raíz del proyecto** (es decir, en el mismo nivel donde está el `pom.xml` o `build.gradle`).

  ```
  proyecto-empleados/
  ├── src/
  ├── pom.xml
  ├── docker-compose.yml   👈 aquí
  ```
  
---

### ▶️ Cómo levantar los contenedores

En la raíz del proyecto, ejecuta:

```bash
docker-compose up -d
```

Esto hará que se levanten los contenedores de **MySQL 8.0** y **PostgreSQL 16** con las credenciales y configuraciones que definiste.

---

### 🔎 Verificación

Puedes comprobar si están corriendo con:

```bash
docker ps
```

Y para probar conexión rápida:

* **MySQL**:

  ```bash
  docker exec -it empleados_mysql mysql -uempleados_user -p
  ```

  (te pedirá `empleados_pass`)

* **Postgres**:

  ```bash
  docker exec -it empleados_postgres psql -U empleados_user -d empleados_db
  ```

---

¿Querés que te prepare también un **archivo `.env`** para centralizar usuarios/contraseñas (y no dejarlos hardcodeados en el `docker-compose.yml`) o preferís dejarlo así de momento?

---

### Modificaciones Realizadas

No se realizo ninguna modificación.

### Explicación del Prompt:
**Motivo**: No recordar los comandos para levantar un contenedor en docker.

### Aprendizajes Obtenidos:
Me permitió recordar los comandos.

---

## Prompt 2: Instalar docker.

### Prompt Utilizado:
```
Guiame en la instalacion de Docker en ubuntu para poder utilizar docker-compose.yml.
```
### Respuesta Recibida:

---
Para poder usar ese `docker-compose.yml` en tu Ubuntu **necesitás tener instalado Docker y Docker Compose** en tu sistema.

---

### 🔧 Pasos de instalación en Ubuntu (recomendado por Docker)

#### 1. Instalar Docker Engine

Ejecutá en tu terminal:

```bash
sudo apt-get update
sudo apt-get install ca-certificates curl gnupg lsb-release
```

Agregar la clave GPG oficial de Docker:

```bash
sudo mkdir -p /etc/apt/keyrings
curl -fsSL https://download.docker.com/linux/ubuntu/gpg | sudo gpg --dearmor -o /etc/apt/keyrings/docker.gpg
```

Configurar el repositorio de Docker:

```bash
echo \
  "deb [arch=$(dpkg --print-architecture) signed-by=/etc/apt/keyrings/docker.gpg] https://download.docker.com/linux/ubuntu \
  $(lsb_release -cs) stable" | sudo tee /etc/apt/sources.list.d/docker.list > /dev/null
```

Instalar Docker:

```bash
sudo apt-get update
sudo apt-get install docker-ce docker-ce-cli containerd.io docker-buildx-plugin docker-compose-plugin
```

---

#### 2. Verificar instalación

```bash
docker --version
```

Debería mostrar algo como:

```
Docker version 27.x.x, build ...
```

Y para Docker Compose:

```bash
docker compose version
```

Debería mostrar:

```
Docker Compose version v2.x.x
```

---

#### 3. Permitir correr Docker sin `sudo` (opcional, pero recomendado)

```bash
sudo groupadd docker
sudo usermod -aG docker $USER
```

⚠️ Necesitás cerrar sesión y volver a entrar para que los cambios tengan efecto.

---

Una vez instalado, ya podés usar:

```bash
docker compose up -d
```

y levantar las bases de datos 🚀

---

¿Querés que te arme un **script `.sh`** que te instale todo esto de una sola vez en tu Ubuntu, así no copiás comando por comando?

---

### Modificaciones Realizadas:
No se realizó ningún cambio en el código

### Explicación del Prompt:
**Motivo**: Recientemente instalé ubuntu como sistema principal en mi computadora y debía instalar docker ya que no lo tenia

### Aprendizajes Obtenidos:
Me permitió aprender a instalar docker.

~~~

## Prompt 3: Verificar el funcionamiento correcto del código

### Prompt Utilizado:
```
Genial! ya lo instale y levante la base de datos. Para probar que todo funcione bien deberia ejecutar el proyecto?
```

### Respuesta Recibida:

~~~text

¡Exacto! 🎯 Ahora que los contenedores de **MySQL** y **PostgreSQL** están corriendo, lo siguiente es **probar que tu proyecto Spring Boot se conecta correctamente a las bases de datos**.

---

### 1️⃣ Configurar los `application-*.yml` (Profiles)

Si ya tenés los perfiles para **MySQL** y **Postgres**, asegurate de que las credenciales coincidan con las que pusiste en `docker-compose.yml`:

**`application-mysql.yml`**

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/empleados_db
    username: empleados_user
    password: empleados_pass
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true
```

**`application-postgres.yml`**

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/empleados_db
    username: empleados_user
    password: empleados_pass
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true
```

---

### 2️⃣ Ejecutar el proyecto con un profile

Para probar **MySQL**:

```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=mysql
```

Para probar **PostgreSQL**:

```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=postgres
```

Si usás **Gradle**:

```bash
./gradlew bootRun --args='--spring.profiles.active=mysql'
```

---

### 3️⃣ Qué revisar

* En la consola deberías ver algo como:

  ```
  HHH000400: Using dialect: org.hibernate.dialect.MySQL8Dialect
  ```

  o para Postgres:

  ```
  HHH000400: Using dialect: org.hibernate.dialect.PostgreSQLDialect
  ```
* Que no aparezcan errores de conexión (`Connection refused`, `Access denied`, etc.)
* Podés probar un endpoint simple de tu API para ver que funciona.

---

💡 **Tip:** Antes de correr Spring Boot, asegurate de que los contenedores estén saludables:

```bash
docker ps
docker inspect -f '{{.State.Health.Status}}' empleados_mysql
docker inspect -f '{{.State.Health.Status}}' empleados_postgres
```

Debería mostrar `healthy`.

---

Si querés, puedo prepararte un **mini script para testear ambos perfiles automáticamente** y ver si la conexión a las bases de datos funciona sin problemas. Esto te ahorra tener que cambiar el profile manualmente.

¿Querés que haga eso?

~~~

### Modificaciones Realizadas:
No se realizó ningún cambió en el código.

### Explicación del Prompt:
*Motivo*: Quería verificar el correcto funcionamiento en el proceso de configuracion e instalación de docker.

### Aprendizajes Obtenidos:
Me permitió aprender a ejecutar el proyecto con distintas bases de datos.

---

## Prompt 4: Identificar error

### Prompt Utilizado:
```
Obtuve el siguiente error en la ejecucion del proyecto. Me ayudas a resolverlo?
Mensaje de Error: (Acá pegué el error)
*No copio y pego el error porque dejaría un mensaje muy largo*
```

### Respuesta Recibida:

~~~text
Ah, perfecto, esto explica exactamente qué está pasando. ✅

El error principal es este:

```
Parameter 1 of method dataSource ... required a single bean, but 2 were found:
- jdbcConnectionDetailsForEmpleados_mysql
- jdbcConnectionDetailsForEmpleados_postgres
```

Es decir, **Spring Boot detectó ambas configuraciones de base de datos y no sabe cuál usar** aunque vos ejecutaste con el perfil `mysql`. Esto suele pasar cuando tenés **ambos `DataSource` configurados como beans** y Spring no sabe cuál priorizar.

---

### 🔹 Solución 1: Marcar un `DataSource` como `@Primary`

Si tenés algo como:

```java
@Bean
@ConfigurationProperties("spring.datasource")
public DataSource dataSource() {
    return DataSourceBuilder.create().build();
}
```

Podés marcar el que querés usar por default con:

```java
@Primary
@Bean
@ConfigurationProperties("spring.datasource")
public DataSource mysqlDataSource() {
    return DataSourceBuilder.create().build();
}
```

Esto le indica a Spring cuál usar si encuentra varios beans de `DataSource`.

---

### 🔹 Solución 2: Revisar profiles y configuración

Si usás **application-mysql.yml** y **application-postgres.yml** para perfiles, asegurate de que:

1. **No estés creando beans de DataSource “manualmente”** en código para ambas bases.
2. Que el profile activo realmente sea `mysql` (`-Dspring-boot.run.profiles=mysql`) y que **el otro profile no se esté cargando simultáneamente**.

Con Spring Boot 3 + JPA, lo ideal es **dejar que Spring cree el DataSource automáticamente** usando los archivos `application-*.yml` y no declarar beans duplicados en tu código.

---

### 🔹 Solución 3: Usar `@Qualifier` si necesitás ambos DataSources

Si el proyecto eventualmente va a usar **ambos** (MySQL y Postgres al mismo tiempo), podés usar `@Qualifier` en los lugares donde inyectás el DataSource.

Ejemplo:

```java
@Autowired
@Qualifier("mysqlDataSource")
private DataSource dataSource;
```

---

💡 En tu caso **lo más simple ahora** es:

* Revisar que no tengas un bean de DataSource duplicado en tu proyecto.
* Confiar en los `application-mysql.yml` y `application-postgres.yml`.
* Ejecutar de nuevo con:

```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=mysql
```

---

Si querés, puedo revisarte cómo deberían quedar tus archivos `application.yml` y `application-*.yml` para **que Spring levante solo la base que corresponde al profile activo**, evitando este conflicto.

¿Querés que haga eso?
~~~

### Modificaciones Realizadas:
No se realizó ningún cambio

### Explicación del Prompt:
*Motivo*: Obtuve un error en la ejecución del código.

### Aprendizajes Obtenidos:
Esta respuesta no me permitió tener una solución directa, pero si pensar sobre el posible error.
El siguiente prompt realizado me permitió confirmar lo que pensaba.

---

## Prompt 5: Arhivo de configuración

### Prompt Utilizado:
```
Confirmame lo siguiente:
El motivo del error se debe a que la configuración de los distintos profiles los puse en un mismo archivo?
```

### Respuesta Recibida:

~~~text
¡Ah, perfecto! Ahora tiene sentido por qué Spring Boot detecta **dos DataSources** al mismo tiempo. 😅

El problema viene de cómo Spring interpreta los **múltiples documentos YAML en un solo archivo**. En tu archivo actual:

```yaml
spring:
  profiles:
    active: dev

---
spring:
  config:
    activate:
      on-profile: dev
  ...
---
spring:
  config:
    activate:
      on-profile: mysql
  ...
---
spring:
  config:
    activate:
      on-profile: postgres
  ...
```

Spring **primero lee el profile activo** (`dev`) y luego evalúa los bloques que tengan `on-profile`. Sin embargo, el bloque `spring.profiles.active: dev` **no desactiva los demás bloques**, y si tenés configuraciones que generan `DataSource` automáticamente (Spring Boot 3.5 + JPA detecta las dependencias), puede terminar registrando ambos `DataSources` de MySQL y Postgres si hay algo que los “une” en el contexto.

---

### 🔹 Forma correcta de organizarlo

1. **Archivo principal `application.yml`**:

```yaml
spring:
  profiles:
    active: dev
```

2. **Archivo por profile**:

* `application-dev.yml` → H2
* `application-mysql.yml` → MySQL
* `application-postgres.yml` → Postgres

Cada uno con su configuración de datasource y JPA. Ejemplo:

**`application-mysql.yml`**

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/empleados_db?useSSL=false&serverTimezone=UTC
    driver-class-name: com.mysql.cj.jdbc.Driver
    username: empleados_user
    password: empleados_pass
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true
    properties:
      hibernate:
        dialect: org.hibernate.dialect.MySQL8Dialect
        format_sql: true
```

**`application-postgres.yml`** es igual, cambiando `url`, `driver-class-name` y `dialect`.

3. **Profile activo al correr**:

```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=mysql
```

Esto asegura que **solo se levante un `DataSource`** y se eliminen los conflictos.

---

💡 Alternativa rápida si querés mantener un solo archivo `application.yml`:

Agregar **`spring.config.activate.on-profile`** en cada bloque y **eliminar `spring.profiles.active` del primer bloque**, y asegurarte de **no dejar H2 activo al mismo tiempo que MySQL o Postgres**. Pero separar en archivos distintos es mucho más limpio y evita estos problemas.

---

Si querés, puedo reescribirte tu `application.yml` actual en **tres archivos separados y listos para usar con Docker**, así probás MySQL y Postgres sin conflictos.

¿Querés que haga eso?

~~~

### Modificaciones Realizadas:
Se tuvo que modificar el archivo de application.yml para luego separarlo en distintos profiles.

### Explicación del Prompt:
Obtenía un error en ejecución que provenía de la realización de una mala configuración de los profiles.

### Aprendizajes Obtenidos:
Me permitió aprender que el DataSource es la conexion hacia la base de datos y que al utilizar distintas configuraciones en un mismo archivo hacia que Spring intentara conectarse a distintas bases de datos al mismo tiempo