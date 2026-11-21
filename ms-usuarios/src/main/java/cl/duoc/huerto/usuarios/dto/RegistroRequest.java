package cl.duoc.huerto.usuarios.dto;

/** Datos para crear una cuenta. codigoAutorizacion solo se exige a docentes y talleristas. */
public record RegistroRequest(
        String nombre,
        String correo,
        String nombreUsuario,
        String clave,
        String cargo,
        String taller,
        String codigoAutorizacion
) {
}
