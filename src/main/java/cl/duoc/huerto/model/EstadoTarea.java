package cl.duoc.huerto.model;

/** Flujo: PENDIENTE -> EN_DESARROLLO -> PENDIENTE_VALIDACION -> VALIDADA / RECHAZADA. */
public enum EstadoTarea {
    PENDIENTE,
    EN_DESARROLLO,
    PENDIENTE_VALIDACION,
    VALIDADA,
    RECHAZADA
}
