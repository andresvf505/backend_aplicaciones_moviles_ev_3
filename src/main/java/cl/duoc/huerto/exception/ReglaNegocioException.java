package cl.duoc.huerto.exception;

import org.springframework.http.HttpStatus;

/**
 * Error de negocio con su codigo HTTP (400, 404, 409...). El manejador
 * lo convierte en una respuesta JSON: {"mensaje": "..."}, que la app
 * movil muestra tal cual al usuario.
 */
public class ReglaNegocioException extends RuntimeException {

    private final HttpStatus estado;

    public ReglaNegocioException(HttpStatus estado, String mensaje) {
        super(mensaje);
        this.estado = estado;
    }

    public HttpStatus getEstado() {
        return estado;
    }

    public static ReglaNegocioException noEncontrado(String mensaje) {
        return new ReglaNegocioException(HttpStatus.NOT_FOUND, mensaje);
    }

    public static ReglaNegocioException invalido(String mensaje) {
        return new ReglaNegocioException(HttpStatus.BAD_REQUEST, mensaje);
    }

    public static ReglaNegocioException conflicto(String mensaje) {
        return new ReglaNegocioException(HttpStatus.CONFLICT, mensaje);
    }

    public static ReglaNegocioException prohibido(String mensaje) {
        return new ReglaNegocioException(HttpStatus.FORBIDDEN, mensaje);
    }
}
