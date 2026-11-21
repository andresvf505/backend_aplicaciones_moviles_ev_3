package cl.duoc.huerto.usuarios.util;

import cl.duoc.huerto.usuarios.exception.ReglaNegocioException;

/** Validaciones simples de los datos que llegan en las solicitudes. */
public final class Validar {

    private Validar() {
    }

    /** Texto obligatorio, sin espacios sobrantes, entre minimo y maximo caracteres. */
    public static String texto(String valor, String campo, int minimo, int maximo) {
        String limpio = valor == null ? "" : valor.trim();
        if (limpio.isEmpty()) {
            throw ReglaNegocioException.invalido(campo + " es obligatorio");
        }
        if (limpio.length() < minimo || limpio.length() > maximo) {
            throw ReglaNegocioException.invalido(
                    campo + " debe tener entre " + minimo + " y " + maximo + " caracteres");
        }
        return limpio;
    }

    /** Texto opcional: devuelve "" si viene vacio. */
    public static String opcional(String valor, int maximo) {
        String limpio = valor == null ? "" : valor.trim();
        if (limpio.length() > maximo) {
            throw ReglaNegocioException.invalido("El texto no puede superar " + maximo + " caracteres");
        }
        return limpio;
    }

    public static <T> T requerido(T valor, String campo) {
        if (valor == null) {
            throw ReglaNegocioException.invalido(campo + " es obligatorio");
        }
        return valor;
    }

    /** Convierte el texto de un enum (por ejemplo "EN_USO") o informa el error. */
    public static <E extends Enum<E>> E enumeracion(Class<E> tipo, String valor, String campo) {
        if (valor == null || valor.isBlank()) {
            throw ReglaNegocioException.invalido(campo + " es obligatorio");
        }
        try {
            return Enum.valueOf(tipo, valor.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw ReglaNegocioException.invalido(campo + " no es valido: " + valor);
        }
    }
}
