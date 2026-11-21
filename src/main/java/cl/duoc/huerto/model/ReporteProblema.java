package cl.duoc.huerto.model;

import jakarta.persistence.*;


@Entity
@Table(name = "reportes_problema")
public class ReporteProblema {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_reporte")
    private Long id;

    @Column(name = "sector_id", nullable = false)
    private Long sectorId;

    @Column(name = "cultivo_id")
    private Long cultivoId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 25)
    private TipoProblema tipo;

    @Column(nullable = false, length = 300)
    private String descripcion;

    @Column(nullable = false)
    private Long fechaReporte;

    @Column(name = "reportado_por_id", nullable = false)
    private Long reportadoPorId;

    @Column(length = 300)
    private String rutaFoto;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 15)
    private EstadoReporte estado;

    @Column(nullable = false, length = 300)
    private String comentarioDocente;

    private Long fechaCierre;

    public ReporteProblema() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public TipoProblema getTipo() {
        return tipo;
    }

    public void setTipo(TipoProblema tipo) {
        this.tipo = tipo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Long getFechaReporte() {
        return fechaReporte;
    }

    public void setFechaReporte(Long fechaReporte) {
        this.fechaReporte = fechaReporte;
    }

    public Long getReportadoPorId() {
        return reportadoPorId;
    }

    public void setReportadoPorId(Long reportadoPorId) {
        this.reportadoPorId = reportadoPorId;
    }

    public String getRutaFoto() {
        return rutaFoto;
    }

    public void setRutaFoto(String rutaFoto) {
        this.rutaFoto = rutaFoto;
    }

    public EstadoReporte getEstado() {
        return estado;
    }

    public void setEstado(EstadoReporte estado) {
        this.estado = estado;
    }

    public String getComentarioDocente() {
        return comentarioDocente;
    }

    public void setComentarioDocente(String comentarioDocente) {
        this.comentarioDocente = comentarioDocente;
    }

    public Long getFechaCierre() {
        return fechaCierre;
    }

    public void setFechaCierre(Long fechaCierre) {
        this.fechaCierre = fechaCierre;
    }

}
