package cl.duoc.huerto.dto;

public record SectorRequest(
        String codigo,
        String nombre,
        String descripcion,
        String ubicacion,
        String estado,
        Long responsableId
) {
}
