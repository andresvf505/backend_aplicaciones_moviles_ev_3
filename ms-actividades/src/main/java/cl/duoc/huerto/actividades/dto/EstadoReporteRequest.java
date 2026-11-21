package cl.duoc.huerto.actividades.dto;

/** Cambio del estado de atencion de un reporte, hecho por un docente o tallerista. */
public record EstadoReporteRequest(Long usuarioId, String estado, String comentario) {
}
