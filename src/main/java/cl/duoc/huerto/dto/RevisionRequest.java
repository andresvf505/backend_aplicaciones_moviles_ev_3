package cl.duoc.huerto.dto;

/** Revision docente. resultado: VALIDADA o RECHAZADA (con motivo). */
public record RevisionRequest(Long validadorId, String resultado, String motivo) {
}
