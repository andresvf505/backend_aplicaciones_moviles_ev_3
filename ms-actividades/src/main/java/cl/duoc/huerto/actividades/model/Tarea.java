package cl.duoc.huerto.actividades.model;

import jakarta.persistence.*;


@Entity
@Table(name = "tareas")
public class Tarea {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_tarea")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 25)
    private TipoTarea tipo;

    @Column(nullable = false, length = 120)
    private String descripcion;

    @Column(nullable = false, length = 300)
    private String instrucciones;

    /** Sector y cultivo viven en ms-huerto; aqui solo se guarda su id. */
    @Column(name = "sector_id", nullable = false)
    private Long sectorId;

    @Column(name = "cultivo_id")
    private Long cultivoId;

    /** Estudiante responsable (usuario de ms-usuarios). */
    @Column(name = "responsable_id", nullable = false)
    private Long responsableId;

    @Column(name = "creada_por_id", nullable = false)
    private Long creadaPorId;

    @Column(nullable = false)
    private Long fechaCreacion;

    @Column(nullable = false)
    private Long fechaProgramada;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 25)
    private EstadoTarea estado;

    private Long fechaRealizacion;

    @Column(length = 300)
    private String comentarioEstudiante;

    public Tarea() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TipoTarea getTipo() {
        return tipo;
    }

    public void setTipo(TipoTarea tipo) {
        this.tipo = tipo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getInstrucciones() {
        return instrucciones;
    }

    public void setInstrucciones(String instrucciones) {
        this.instrucciones = instrucciones;
    }

    public Long getSectorId() {
        return sectorId;
    }

    public void setSectorId(Long sectorId) {
        this.sectorId = sectorId;
    }

    public Long getCultivoId() {
        return cultivoId;
    }

    public void setCultivoId(Long cultivoId) {
        this.cultivoId = cultivoId;
    }

    public Long getResponsableId() {
        return responsableId;
    }

    public void setResponsableId(Long responsableId) {
        this.responsableId = responsableId;
    }

    public Long getCreadaPorId() {
        return creadaPorId;
    }

    public void setCreadaPorId(Long creadaPorId) {
        this.creadaPorId = creadaPorId;
    }

    public Long getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(Long fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public Long getFechaProgramada() {
        return fechaProgramada;
    }

    public void setFechaProgramada(Long fechaProgramada) {
        this.fechaProgramada = fechaProgramada;
    }

    public EstadoTarea getEstado() {
        return estado;
    }

    public void setEstado(EstadoTarea estado) {
        this.estado = estado;
    }

    public Long getFechaRealizacion() {
        return fechaRealizacion;
    }

    public void setFechaRealizacion(Long fechaRealizacion) {
        this.fechaRealizacion = fechaRealizacion;
    }

    public String getComentarioEstudiante() {
        return comentarioEstudiante;
    }

    public void setComentarioEstudiante(String comentarioEstudiante) {
        this.comentarioEstudiante = comentarioEstudiante;
    }

}
