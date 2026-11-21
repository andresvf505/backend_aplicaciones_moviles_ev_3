# Huerto Escolar: backend (`ms-huerto-escolar`)

Microservicio que usa la app Android del proyecto **Huerto Escolar**. La app está en otra
carpeta, **`huerto_escolar_frontend`**, y se abre en Android Studio.

Repositorios en GitHub:
- Backend (este): https://github.com/andresvf505/backend_aplicaciones_moviles_ev_3.git
- App Android: https://github.com/antoniadelourdespalmavega/apps_moviles_segundaEntrega.git

## Cómo se conecta con la app

Los dos proyectos son independientes y se comunican por HTTP, no importa dónde estén
guardadas las carpetas:

- Este backend escucha en **http://localhost:8080**.
- La app apunta a **`http://10.0.2.2:8080/`** (`ApiConfig.URL_BACKEND` en el frontend):
  `10.0.2.2` es el computador visto desde el emulador.
- Solo hay que tener este backend corriendo (y MySQL encendido) en el mismo computador
  donde se ejecuta el emulador.

Un solo proyecto Spring Boot (Java 21, Spring Boot 4.1.2, Spring Data JPA, MySQL), con la
misma estructura del ejemplo `miapi` visto en clase.

| Microservicio | Puerto | Base de datos | Responsabilidad |
|---|---|---|---|
| `ms-huerto-escolar` | 8080 | `huerto_escolar` | Usuarios (login, registro, perfil), sectores, cultivos, lecturas simuladas, tareas, evidencias, validaciones y reportes |

> Antes el backend estaba dividido en tres microservicios (`ms-usuarios`, `ms-huerto` y
> `ms-actividades`), cada uno con su base de datos. Por recomendación del docente se
> unificaron en uno solo. Todos los endpoints conservan su ruta, por lo que la app funciona
> igual. La consulta de roles que antes se hacía por HTTP a `ms-usuarios` ahora la hace
> `PermisoService` leyendo directamente la tabla de usuarios.

## Estructura

```
src/main/java/cl/duoc/huerto/
├── MsHuertoEscolarApplication.java   Punto de entrada
├── controller/   Endpoints REST: usuarios, sectores, cultivos, lecturas, tareas, historial, reportes
├── service/      Reglas de negocio y permisos (PermisoService)
├── repository/   Acceso a datos (JpaRepository)
├── model/        Entidades JPA y enums
├── dto/          Datos que llegan en las solicitudes (records)
├── config/       DataInitializer: cuentas, sectores, cultivos y lecturas de prueba
├── exception/    Errores con su codigo HTTP y respuesta {"mensaje": "..."}
└── util/         Validaciones comunes y hash de contrasenas
```

## Ejecutar

1. XAMPP → **Start** en MySQL (puerto 3306). La base `huerto_escolar` se crea sola.
   Si su MySQL tiene contraseña, escríbanla en `src/main/resources/application.properties`.
2. IntelliJ IDEA → *File > Open* → esta carpeta (`huerto_escolar_backend`) → *Trust Project*.
3. *File > Project Structure > SDK* → **JDK 21**.
4. Abrir `src/main/java/cl/duoc/huerto/MsHuertoEscolarApplication.java` y presionar ▶.
   Esperar `Tomcat started on port 8080` y dejarlo corriendo.
5. Abrir la app en Android Studio (carpeta `huerto_escolar_frontend`) y ejecutarla en el emulador.

Cuentas de prueba (contraseña `huerto123`): `profe.ana`, `taller.luis`, `est.camila`,
`est.benjamin`, `est.martina`.

Pruebas rápidas desde el navegador o Postman:
- http://localhost:8080/usuarios
- http://localhost:8080/sectores
- http://localhost:8080/tareas

Ejemplo de login con Postman: `POST http://localhost:8080/usuarios/login` con el cuerpo
`{"identificador": "profe.ana", "clave": "huerto123"}`.

## Pruebas unitarias y cobertura

JUnit 5 + Mockito (sin base de datos: los repositorios se simulan) y **Kover** para la cobertura:

En IntelliJ: panel **Maven** (barra derecha) → **Lifecycle** → doble clic en **verify**.
O desde la terminal de IntelliJ:

```
.\mvnw.cmd verify   # pruebas + informe Kover + falla si la cobertura baja del 90 %
```

Informe de cobertura: `target/site/kover/html/index.html` (se genera al terminar **verify**).
