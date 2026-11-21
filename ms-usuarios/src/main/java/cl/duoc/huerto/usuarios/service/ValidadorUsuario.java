package cl.duoc.huerto.usuarios.service;

import cl.duoc.huerto.usuarios.exception.ReglaNegocioException;

import java.util.regex.Pattern;

/**
 * Mismas reglas que valida la app movil (domain/validacion). Se repiten aqui
 * porque el servidor no puede confiar en que los datos lleguen ya validados.
 */
public final class ValidadorUsuario {

    public static final String CODIGO_AUTORIZACION_ADMIN = "HUERTO2026";

    private static final Pattern CORREO =
            Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*\\.[A-Za-z]{2,}$");
    private static final Pattern NOMBRE = Pattern.compile("^[A-Za-z\u00C0-\u00FF' -]+$");
    private static final Pattern USUARIO = Pattern.compile("^[a-z0-9._]{4,20}$");

    private ValidadorUsuario() {
    }

    public static String nombre(String nombre) {
        String limpio = nombre == null ? "" : nombre.trim().replaceAll("\\s+", " ");
        if (limpio.length() < 5 || limpio.length() > 60 || !NOMBRE.matcher(limpio).matches()
                || limpio.split(" ").length < 2) {
            throw ReglaNegocioException.invalido("Ingresa nombre y apellido (solo letras, 5 a 60 caracteres)");
        }
        return limpio;
    }

    public static String correo(String correo) {
        String limpio = correo == null ? "" : correo.trim().toLowerCase();
        if (limpio.length() > 80 || !CORREO.matcher(limpio).matches()) {
            throw ReglaNegocioException.invalido("Correo inválido. Ejemplo: nombre@escuela.cl");
        }
        return limpio;
    }

    public static String nombreUsuario(String usuario) {
        String limpio = usuario == null ? "" : usuario.trim().toLowerCase();
        if (!USUARIO.matcher(limpio).matches()) {
            throw ReglaNegocioException.invalido(
                    "El usuario debe tener de 4 a 20 caracteres: minúsculas, números, punto o guion bajo");
        }
        return limpio;
    }

    public static String clave(String clave) {
        if (clave == null || clave.length() < 8 || clave.length() > 30 || clave.chars().anyMatch(Character::isWhitespace)
                || clave.chars().noneMatch(Character::isLetter) || clave.chars().noneMatch(Character::isDigit)) {
            throw ReglaNegocioException.invalido(
                    "La contraseña debe tener de 8 a 30 caracteres, con letras y números y sin espacios");
        }
        return clave;
    }

    public static String taller(String taller) {
        String limpio = taller == null ? "" : taller.trim();
        if (limpio.length() < 2 || limpio.length() > 40) {
            throw ReglaNegocioException.invalido("El taller debe tener entre 2 y 40 caracteres");
        }
        return limpio;
    }
}
