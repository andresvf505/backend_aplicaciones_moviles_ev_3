package cl.duoc.huerto.huerto.model;

import jakarta.persistence.*;


@Entity
@Table(name = "cultivos")
public class Cultivo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_cultivo")
    private Long id;

    @Column(nullable = false, length = 60)
    private String nombre;

    @Column(nullable = false, length = 200)
    private String descripcion;

    @Column(name = "sector_id", nullable = false)
    private Long sectorId;

    @Column(nullable = false)
    private Long fechaSiembra;

    private Long fechaTrasplante;

    @Column(nullable = false)
    private Long fechaCosechaEstimada;

    @Column(nullable = false)
    private Integer frecuenciaRiegoDias;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 25)
    private EstadoCultivo estado;

    @Column(nullable = false, length = 300)
    private String observaciones;

    @Column(length = 300)
    private String rutaFoto;

    public Cultivo() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Long getSectorId() {
        return sectorId;
    }

    public void setSectorId(Long sectorId) {
        this.sectorId = sectorId;
    }

    public Long getFechaSiembra() {
        return fechaSiembra;
    }

    public void setFechaSiembra(Long fechaSiembra) {
        this.fechaSiembra = fechaSiembra;
    }

    public Long getFechaTrasplante() {
        return fechaTrasplante;
    }

    public void setFechaTrasplante(Long fechaTrasplante) {
        this.fechaTrasplante = fechaTrasplante;
    }

    public Long getFechaCosechaEstimada() {
        return fechaCosechaEstimada;
    }

    public void setFechaCosechaEstimada(Long fechaCosechaEstimada) {
        this.fechaCosechaEstimada = fechaCosechaEstimada;
    }

    public Integer getFrecuenciaRiegoDias() {
        return frecuenciaRiegoDias;
    }

    public void setFrecuenciaRiegoDias(Integer frecuenciaRiegoDias) {
        this.frecuenciaRiegoDias = frecuenciaRiegoDias;
    }

    public EstadoCultivo getEstado() {
        return estado;
    }

    public void setEstado(EstadoCultivo estado) {
        this.estado = estado;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public String getRutaFoto() {
        return rutaFoto;
    }

    public void setRutaFoto(String rutaFoto) {
        this.rutaFoto = rutaFoto;
    }

}
