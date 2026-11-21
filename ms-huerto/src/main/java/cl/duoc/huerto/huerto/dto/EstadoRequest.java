package cl.duoc.huerto.huerto.dto;

/** Cambio de estado de un cultivo, por ejemplo {"estado": "COSECHADO"}. */
public record EstadoRequest(String estado) {
}
