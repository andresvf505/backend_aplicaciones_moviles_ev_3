package cl.duoc.huerto.actividades.model;

import jakarta.persistence.*;


@Entity
@Table(name = "validaciones")
public class Validacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_validacion")
    private Long id;

    @Column(name = "tarea_id", nullable = false)
    private Long tareaId;

    @Column(name = "validador_id", nullable = false)
    private Long validadorId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 15)
    private ResultadoValidacion resultado;

    @Column(nullable = false, length = 300)
    private String motivo;

    @Column(nullable = false)
    private Long fechaValidacion;

    public Validacion() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTareaId() {
        return tareaId;
    }

    public void setTareaId(Long tareaId) {
        this.tareaId = tareaId;
    }

    public Long getValidadorId() {
        return validadorId;
    }

    public void setValidadorId(Long validadorId) {
        this.validadorId = validadorId;
    }

    public ResultadoValidacion getResultado() {
        return resultado;
    }

    public void setResultado(ResultadoValidacion resultado) {
        this.resultado = resultado;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public Long getFechaValidacion() {
        return fechaValidacion;
    }

    public void setFechaValidacion(Long fechaValidacion) {
        this.fechaValidacion = fechaValidacion;
    }

}
