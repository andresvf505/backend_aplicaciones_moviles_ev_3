package cl.duoc.huerto.usuarios.dto;

/** identificador: nombre de usuario o correo. */
public record LoginRequest(String identificador, String clave) {
}
