package cl.duoc.huerto.actividades.dto;

/** Revision docente. resultado: VALIDADA o RECHAZADA (con motivo). */
public record RevisionRequest(Long validadorId, String resultado, String motivo) {
}
