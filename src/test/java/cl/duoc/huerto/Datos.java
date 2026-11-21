package cl.duoc.huerto;

import cl.duoc.huerto.model.*;
import cl.duoc.huerto.util.Claves;

/** Objetos de prueba reutilizados por varios tests. */
public final class Datos {

    public static final String CLAVE = "huerto123";

    private Datos() {
    }

    public static Usuario usuario(Long id, String nombreUsuario, Cargo cargo) {
        Usuario u = new Usuario();
        u.setId(id);
        u.setNombreUsuario(nombreUsuario);
        u.setCorreo(nombreUsuario + "@escuela.cl");
        u.setClaveHash(Claves.hash(CLAVE));
        u.setNombre("Nombre Apellido");
        u.setCargo(cargo);
        u.setRol(cargo.getRol());
        u.setTaller("Taller A");
        u.setActivo(true);
        return u;
    }

    public static Usuario docente() {
        return usuario(1L, "profe.ana", Cargo.DOCENTE);
    }

    public static Usuario estudiante() {
        return usuario(3L, "est.camila", Cargo.ESTUDIANTE);
    }

    public static Sector sector(Long id) {
        Sector s = new Sector();
        s.setId(id);
        s.setCodigo("S-0" + id);
        s.setNombre("Sector " + id);
        s.setDescripcion("Descripcion del sector");
        s.setUbicacion("Patio norte");
        s.setEstado(EstadoSector.EN_USO);
        s.setResponsableId(1L);
        s.setFechaCreacion(1L);
        s.setFechaActualizacion(2L);
        return s;
    }

    public static Cultivo cultivo(Long id, Long sectorId) {
        Cultivo c = new Cultivo();
        c.setId(id);
        c.setNombre("Lechuga");
        c.setDescripcion("Almacigo de lechuga");
        c.setSectorId(sectorId);
        c.setFechaSiembra(100L);
        c.setFechaTrasplante(150L);
        c.setFechaCosechaEstimada(200L);
        c.setFrecuenciaRiegoDias(2);
        c.setEstado(EstadoCultivo.SEMBRADO);
        c.setObservaciones("");
        c.setRutaFoto(null);
        return c;
    }

    public static Tarea tarea(Long id, EstadoTarea estado) {
        Tarea t = new Tarea();
        t.setId(id);
        t.setTipo(TipoTarea.RIEGO);
        t.setDescripcion("Regar el sector");
        t.setInstrucciones("Regar temprano en la manana");
        t.setSectorId(1L);
        t.setCultivoId(null);
        t.setResponsableId(3L);
        t.setCreadaPorId(1L);
        t.setFechaCreacion(10L);
        t.setFechaProgramada(20L);
        t.setEstado(estado);
        return t;
    }

    public static ReporteProblema reporte(Long id) {
        ReporteProblema r = new ReporteProblema();
        r.setId(id);
        r.setSectorId(1L);
        r.setCultivoId(null);
        r.setTipo(TipoProblema.FALTA_DE_AGUA);
        r.setDescripcion("La cama esta muy seca");
        r.setFechaReporte(5L);
        r.setReportadoPorId(3L);
        r.setRutaFoto(null);
        r.setEstado(EstadoReporte.INFORMADO);
        r.setComentarioDocente("");
        r.setFechaCierre(null);
        return r;
    }
}
