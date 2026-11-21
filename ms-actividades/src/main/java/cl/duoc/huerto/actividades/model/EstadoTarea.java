package cl.duoc.huerto.actividades.model;

/** Flujo: PENDIENTE -> EN_DESARROLLO -> PENDIENTE_VALIDACION -> VALIDADA / RECHAZADA. */
public enum EstadoTarea {
    PENDIENTE,
    EN_DESARROLLO,
    PENDIENTE_VALIDACION,
    VALIDADA,
    RECHAZADA
}
