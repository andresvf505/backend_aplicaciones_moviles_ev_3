package cl.duoc.huerto.service;

import cl.duoc.huerto.dto.EstadoReporteRequest;
import cl.duoc.huerto.dto.ReporteRequest;
import cl.duoc.huerto.exception.ReglaNegocioException;
import cl.duoc.huerto.model.EstadoReporte;
import cl.duoc.huerto.model.ReporteProblema;
import cl.duoc.huerto.model.TipoProblema;
import cl.duoc.huerto.repository.ReporteRepository;
import cl.duoc.huerto.util.Validar;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/** El estudiante reporta problemas; el docente o tallerista los atiende. */
@Service
public class ReporteService {

    @Autowired
    private ReporteRepository reporteRepository;

    @Autowired
    private PermisoService permisoService;

    public List<ReporteProblema> listar() {
        return reporteRepository.findAllByOrderByFechaReporteDesc();
    }

    public ReporteProblema buscarPorId(Long id) {
        return reporteRepository.findById(id)
                .orElseThrow(() -> ReglaNegocioException.noEncontrado("El reporte " + id + " no existe"));
    }

    public ReporteProblema registrar(ReporteRequest solicitud) {
        if (!permisoService.esEstudiante(solicitud.usuarioId())) {
            throw ReglaNegocioException.prohibido(
                    "Los problemas los reporta el estudiante; el docente o tallerista los soluciona");
        }
        ReporteProblema reporte = new ReporteProblema();
        reporte.setSectorId(Validar.requerido(solicitud.sectorId(), "El sector"));
        reporte.setCultivoId(solicitud.cultivoId());
        reporte.setTipo(Validar.enumeracion(TipoProblema.class, solicitud.tipo(), "El tipo de problema"));
        reporte.setDescripcion(Validar.texto(solicitud.descripcion(), "La descripción del problema", 10, 300));
        reporte.setFechaReporte(System.currentTimeMillis());
        reporte.setReportadoPorId(solicitud.usuarioId());
        reporte.setRutaFoto(solicitud.rutaFoto());
        reporte.setEstado(EstadoReporte.INFORMADO);
        reporte.setComentarioDocente("");
        reporte.setFechaCierre(null);
        return reporteRepository.save(reporte);
    }

    public ReporteProblema actualizarEstado(Long id, EstadoReporteRequest solicitud) {
        if (!permisoService.esAdministrador(solicitud.usuarioId())) {
            throw ReglaNegocioException.prohibido("Solo un docente o tallerista puede cambiar el estado del reporte");
        }
        ReporteProblema reporte = buscarPorId(id);
        EstadoReporte estado = Validar.enumeracion(EstadoReporte.class, solicitud.estado(), "El estado");
        reporte.setEstado(estado);
        reporte.setComentarioDocente(Validar.opcional(solicitud.comentario(), 300));
        reporte.setFechaCierre(estado == EstadoReporte.ATENDIDO || estado == EstadoReporte.CERRADO
                ? System.currentTimeMillis() : null);
        return reporteRepository.save(reporte);
    }

    public void borrar(Long id) {
        reporteRepository.delete(buscarPorId(id));
    }
}
