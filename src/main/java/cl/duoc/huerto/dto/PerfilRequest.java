package cl.duoc.huerto.dto;

/** Edicion del perfil. Si claveNueva viene vacia, la contrasena no cambia. */
public record PerfilRequest(
        String nombre,
        String correo,
        String taller,
        String claveActual,
        String claveNueva
) {
}
