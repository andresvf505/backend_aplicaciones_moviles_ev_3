package cl.duoc.huerto.dto;

/** Entrega del estudiante: comentario y ruta de la fotografia de evidencia. */
public record EntregaRequest(Long usuarioId, String comentario, String rutaFoto) {
}
