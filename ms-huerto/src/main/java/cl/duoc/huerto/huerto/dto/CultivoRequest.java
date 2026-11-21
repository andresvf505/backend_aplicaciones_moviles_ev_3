package cl.duoc.huerto.huerto.dto;

public record CultivoRequest(
        String nombre,
        String descripcion,
        Long sectorId,
        Long fechaSiembra,
        Long fechaTrasplante,
        Long fechaCosechaEstimada,
        Integer frecuenciaRiegoDias,
        String estado,
        String observaciones,
        String rutaFoto
) {
}
