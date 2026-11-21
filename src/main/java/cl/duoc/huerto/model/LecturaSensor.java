package cl.duoc.huerto.model;

import jakarta.persistence.*;


@Entity
@Table(name = "lecturas_sensor")
public class LecturaSensor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_lectura")
    private Long id;

    @Column(name = "sector_id", nullable = false)
    private Long sectorId;

    @Column(nullable = false)
    private Long fechaLectura;

    @Column(nullable = false)
    private Integer humedadSuelo;

    @Column(nullable = false)
    private Double temperatura;

    @Column(nullable = false)
    private Integer humedadAmbiente;

    /** Siempre "Simulación académica": no hay sensores reales conectados. */
    @Column(nullable = false, length = 40)
    private String origen;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoLectura estado;

    public LecturaSensor() {
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

    public Long getFechaLectura() {
        return fechaLectura;
    }

    public void setFechaLectura(Long fechaLectura) {
        this.fechaLectura = fechaLectura;
    }

    public Integer getHumedadSuelo() {
        return humedadSuelo;
    }

    public void setHumedadSuelo(Integer humedadSuelo) {
        this.humedadSuelo = humedadSuelo;
    }

    public Double getTemperatura() {
        return temperatura;
    }

    public void setTemperatura(Double temperatura) {
        this.temperatura = temperatura;
    }

    public Integer getHumedadAmbiente() {
        return humedadAmbiente;
    }

    public void setHumedadAmbiente(Integer humedadAmbiente) {
        this.humedadAmbiente = humedadAmbiente;
    }

    public String getOrigen() {
        return origen;
    }

    public void setOrigen(String origen) {
        this.origen = origen;
    }

    public EstadoLectura getEstado() {
        return estado;
    }

    public void setEstado(EstadoLectura estado) {
        this.estado = estado;
    }

}
