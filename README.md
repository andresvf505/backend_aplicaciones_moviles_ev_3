# Microservicios del Huerto Escolar

Tres proyectos Spring Boot independientes (Java 21, Spring Boot 4.1.2, Spring Data JPA,
MySQL), con la misma estructura del ejemplo `miapi` visto en clase.

| Microservicio | Puerto | Base de datos | Responsabilidad |
|---|---|---|---|
| `ms-usuarios` | 8081 | `huerto_usuarios` | Cuentas, login, registro y perfil |
| `ms-huerto` | 8082 | `huerto_cultivos` | Sectores, cultivos y lecturas simuladas de sensores |
| `ms-actividades` | 8083 | `huerto_actividades` | Tareas, evidencias, validaciones y reportes |

`ms-actividades` consulta a `ms-usuarios` (con `RestClient`) para revisar el rol de quien
crea, entrega o valida una tarea. Por eso conviene levantar primero `ms-usuarios`.

## Estructura de cada microservicio

```
src/main/java/cl/duoc/huerto/<servicio>/
├── Ms...Application.java   Punto de entrada
├── controller/             Endpoints REST (@RestController)
├── service/                Reglas de negocio
├── repository/             Acceso a datos (JpaRepository)
├── model/                  Entidades JPA y enums
├── dto/                    Datos que llegan en las solicitudes (records)
├── config/                 DataInitializer: datos de prueba
├── exception/              Errores con su codigo HTTP y respuesta {"mensaje": "..."}
└── util/                   Validaciones comunes
```

## Ejecutar

El paso a paso completo (XAMPP, IntelliJ IDEA y Android Studio) está en el README
principal, sección **5. Pasos para ejecutar**. Resumen:

1. XAMPP → **Start** en MySQL.
2. IntelliJ IDEA → *File > Open* → carpeta `backend` → clic derecho en cada `pom.xml` →
   *Add as Maven Project* → JDK 21.
3. Ejecutar con ▶ `MsUsuariosApplication`, luego `MsHuertoApplication` y luego
   `MsActividadesApplication`, y dejar los tres corriendo.

Pruebas rápidas desde el navegador o Postman:
- http://localhost:8081/usuarios
- http://localhost:8082/sectores
- http://localhost:8083/tareas

Ejemplo de login con Postman: `POST http://localhost:8081/usuarios/login` con el cuerpo
`{"identificador": "profe.ana", "clave": "huerto123"}`.
