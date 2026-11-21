package cl.duoc.huerto.dto;

import cl.duoc.huerto.model.Usuario;

/** Datos publicos de un usuario. Nunca incluye la contrasena. */
public record UsuarioResponse(
        Long id,
        String nombreUsuario,
        String correo,
        String nombre,
        String rol,
        String cargo,
        String taller,
        boolean activo
) {
    public static UsuarioResponse desde(Usuario u) {
        return new UsuarioResponse(u.getId(), u.getNombreUsuario(), u.getCorreo(), u.getNombre(),
                u.getRol().name(), u.getCargo().name(), u.getTaller(), u.isActivo());
    }
}
