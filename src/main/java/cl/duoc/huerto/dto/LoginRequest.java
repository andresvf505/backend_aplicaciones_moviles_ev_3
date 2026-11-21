package cl.duoc.huerto.dto;

/** identificador: nombre de usuario o correo. */
public record LoginRequest(String identificador, String clave) {
}
