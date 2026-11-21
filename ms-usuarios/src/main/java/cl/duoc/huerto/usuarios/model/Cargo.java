package cl.duoc.huerto.usuarios.model;

/** Cargo de la persona. Docente y tallerista comparten el rol administrador. */
public enum Cargo {
    DOCENTE(Rol.ADMINISTRADOR),
    TALLERISTA(Rol.ADMINISTRADOR),
    ESTUDIANTE(Rol.ESTUDIANTE);

    private final Rol rol;

    Cargo(Rol rol) {
        this.rol = rol;
    }

    public Rol getRol() {
        return rol;
    }
}
