package cl.duoc.huerto.actividades.dto;

public record ReporteRequest(
        Long sectorId,
        Long cultivoId,
        String tipo,
        String descripcion,
        Long usuarioId,
        String rutaFoto
) {
}
