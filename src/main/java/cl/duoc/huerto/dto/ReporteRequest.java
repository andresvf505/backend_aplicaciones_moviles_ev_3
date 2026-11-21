package cl.duoc.huerto.dto;

public record ReporteRequest(
        Long sectorId,
        Long cultivoId,
        String tipo,
        String descripcion,
        Long usuarioId,
        String rutaFoto
) {
}
