package cl.duoc.huerto.actividades.dto;

/** Crear o editar una tarea. usuarioId: el docente o tallerista que la crea o edita. */
public record TareaRequest(
        String tipo,
        String descripcion,
        String instrucciones,
        Long sectorId,
        Long cultivoId,
        Long responsableId,
        Long fechaProgramada,
        Long usuarioId
) {
}
