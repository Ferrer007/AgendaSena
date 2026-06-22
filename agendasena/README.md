# AgendaSENA — API de Reserva de Ambientes de Formación

API REST construida con **Spring Boot 3.5.14 + Spring Data JPA + MySQL** que centraliza la gestión de reservas de ambientes (salas, laboratorios, auditorios) y hace cumplir las reglas de negocio del centro de formación automáticamente.

## Tecnologías

- Java 21
- Spring Boot 3.5.14
- Spring Web, Spring Data JPA, Validation
- MySQL 8
- Lombok
- Maven

## Arquitectura

El proyecto sigue una arquitectura en capas estricta:

```
Controller → Service → Repository
```

La lógica de negocio vive **exclusivamente en la capa de servicio** (`ReservaService`, `AmbienteService`, `ReporteService`). Los controllers solo reciben la petición, la delegan al servicio y devuelven la respuesta.

```
src/main/java/com/sena/agendasena/
├── controllers/      # Entrada HTTP (sin lógica de negocio)
├── services/          # Las 7 reglas de negocio viven aquí
├── repositories/      # Spring Data JPA + consultas @Query personalizadas
├── models/            # Entidades JPA (Ambiente, Reserva) y enums
├── dtos/               # Objetos de entrada/salida (no se exponen entidades directamente)
└── exceptions/        # Excepciones de negocio + manejador centralizado (@RestControllerAdvice)
```

## Cómo ejecutar el proyecto

### 1. Requisitos previos

- Java 21 instalado
- MySQL corriendo localmente (puerto 3306 por defecto)
- Maven (o usa el wrapper `./mvnw` incluido en el proyecto)

### 2. Configurar la conexión a MySQL

Abre `src/main/resources/application.properties` y reemplaza `TU_PASSWORD_AQUI` por la contraseña real de tu usuario `root` de MySQL:

```properties
spring.datasource.password=tu_password_real
```

> No es necesario crear la base de datos a mano: la URL de conexión incluye `createDatabaseIfNotExist=true`, así que MySQL la crea automáticamente la primera vez que la aplicación se conecta.

### 3. Ejecutar la aplicación

Desde la raíz del proyecto (en Windows, PowerShell):

```powershell
.\mvnw.cmd spring-boot:run
```

En Linux/Mac:

```bash
./mvnw spring-boot:run
```

También puedes ejecutar la clase `AgendasenaApplication.java` directamente desde VS Code (botón "Run").

Al iniciar, Hibernate creará automáticamente las tablas `ambientes` y `reservas` (gracias a `spring.jpa.hibernate.ddl-auto=update`). Justo después, un `CommandLineRunner` (clase `DataLoader.java`) carga automáticamente 5 ambientes y 4 reservas de prueba si la tabla de ambientes está vacía.

La API queda disponible en `http://localhost:8080`.

### 4. Probar los endpoints

El proyecto incluye una colección de pruebas en `http/agendasena.http` con un caso exitoso y un caso de error para cada una de las 7 reglas de negocio. Para usarla:

1. Instala la extensión **REST Client** en VS Code.
2. Abre `http/agendasena.http`.
3. Haz clic en "Send Request" sobre cada bloque (separado por `###`).

## Modelo de datos

**Ambiente**: `id`, `nombre`, `tipo` (SALA, LABORATORIO, AUDITORIO), `capacidad`, `activo`.

**Reserva**: `id`, `ambiente` (relación `@ManyToOne`), `nombreInstructor`, `fechaHoraInicio`, `fechaHoraFin` (ambos `LocalDateTime`, fecha y hora juntas), `numeroAprendices`, `estado` (ACTIVA, CANCELADA, FINALIZADA).

## Reglas de negocio implementadas

Todas se validan en `ReservaService`, nunca se confía en el cliente:

1. **Sin cruces de horario** — un ambiente no puede tener dos reservas ACTIVAS que se solapen, ni siquiera parcialmente. Se valida con una consulta JPQL personalizada (`findSolapamientos`) que compara `inicioExistente < finNuevo AND finExistente > inicioNuevo`.
2. **Capacidad** — el número de aprendices no puede superar la capacidad del ambiente.
3. **Horario institucional** — las reservas deben estar entre las 6:00 y las 22:00, y durar entre 1 y 4 horas.
4. **Ambientes inactivos** — no se puede reservar un ambiente con `activo = false`.
5. **Límite por instructor** — un instructor no puede tener más de 3 reservas ACTIVAS el mismo día.
6. **Cancelación con anticipación** — solo se puede cancelar si faltan al menos 2 horas para el inicio. Cancelar cambia el estado a CANCELADA, nunca borra el registro.
7. **No se reserva en el pasado** — la fecha de inicio debe ser posterior al momento actual.

## Manejo de errores

Todas las excepciones se capturan en `GlobalExceptionHandler` (`@RestControllerAdvice`) y se traducen a un cuerpo JSON consistente:

```json
{
  "timestamp": "2026-06-21T14:32:10",
  "status": 409,
  "error": "Conflicto",
  "mensaje": "El ambiente ya tiene una reserva activa que se solapa con el horario solicitado."
}
```

- `ReglaNegocioException` → HTTP 400
- `ConflictException` → HTTP 409 (solapamiento, límite de instructor superado)
- Errores de validación de campos (`@NotNull`, `@Min`, etc.) → HTTP 400
- Cualquier otro error inesperado → HTTP 500, siempre con cuerpo JSON, nunca una página de error genérica

## Endpoints

| Método | Ruta | Descripción |
|---|---|---|
| POST | `/api/ambientes` | Registrar un ambiente |
| GET | `/api/ambientes` | Listar ambientes |
| POST | `/api/reservas` | Crear una reserva (aplica todas las reglas) |
| PATCH | `/api/reservas/{id}/cancelar` | Cancelar una reserva |
| GET | `/api/ambientes/{id}/reservas?fecha=2026-07-01` | Reservas activas de un ambiente en una fecha |
| GET | `/api/ambientes/disponibles?inicio=...&fin=...` | Ambientes libres en un rango de tiempo |
| GET | `/api/reportes/ocupacion?fecha=2026-07-01` | Horas reservadas y % de ocupación por ambiente |

## Extra implementado

- **Manejo centralizado de excepciones con `@RestControllerAdvice`**: en lugar de repetir try-catch en cada controller, todas las excepciones de la API pasan por `GlobalExceptionHandler`, que las traduce al código HTTP y formato JSON correctos.