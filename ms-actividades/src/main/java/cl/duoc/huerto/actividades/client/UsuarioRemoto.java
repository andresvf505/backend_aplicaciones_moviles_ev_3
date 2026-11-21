package cl.duoc.huerto.actividades.client;

/** Lo que ms-actividades necesita saber de un usuario de ms-usuarios. */
public record UsuarioRemoto(Long id, String nombre, String rol, boolean activo) {
}
