package cl.duoc.huerto.dto;

/** Iniciar una tarea: solo se indica quien lo hace. */
public record AccionRequest(Long usuarioId) {
}
