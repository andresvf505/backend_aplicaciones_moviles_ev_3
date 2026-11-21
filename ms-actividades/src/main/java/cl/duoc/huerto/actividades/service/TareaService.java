package cl.duoc.huerto.actividades.service;

import cl.duoc.huerto.actividades.client.UsuariosClient;
import cl.duoc.huerto.actividades.dto.AccionRequest;
import cl.duoc.huerto.actividades.dto.EntregaRequest;
import cl.duoc.huerto.actividades.dto.RevisionRequest;
import cl.duoc.huerto.actividades.dto.TareaRequest;
import cl.duoc.huerto.actividades.exception.ReglaNegocioException;
import cl.duoc.huerto.actividades.model.*;
import cl.duoc.huerto.actividades.repository.EvidenciaRepository;
import cl.duoc.huerto.actividades.repository.TareaRepository;
import cl.duoc.huerto.actividades.repository.ValidacionRepository;
import cl.duoc.huerto.actividades.util.Validar;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Tareas del huerto y su flujo de estados:
 * PENDIENTE -> EN_DESARROLLO -> PENDIENTE_VALIDACION -> VALIDADA / RECHAZADA.
 * Aqui se aplican las reglas de permisos y de estados en el servidor.
 */
@Service
public class TareaService {

    @Autowired
    private TareaRepository tareaRepository;

    @Autowired
    private EvidenciaRepository evidenciaRepository;

    @Autowired
    private ValidacionRepository validacionRepository;

    @Autowired
    private UsuariosClient usuariosClient;

    public List<Tarea> listar(Long responsableId) {
        return responsableId == null ? tareaRepository.findAllByOrderByFechaProgramadaAscIdAsc()
                : tareaRepository.findByResponsableIdOrderByFechaProgramadaAsc(responsableId);
    }

    public Tarea buscarPorId(Long id) {
        return tareaRepository.findById(id)
                .orElseThrow(() -> ReglaNegocioException.noEncontrado("La tarea " + id + " no existe"));
    }

    public List<Evidencia> evidencias() {
        return evidenciaRepository.findAllByOrderByFechaRegistroDesc();
    }

    public List<Validacion> validaciones() {
        return validacionRepository.findAllByOrderByFechaValidacionDesc();
    }

    /** Solo un administrador crea tareas, y el responsable debe ser estudiante. */
    public Tarea crear(TareaRequest solicitud) {
        validarPermisos(solicitud);
        long ahora = System.currentTimeMillis();
        Tarea tarea = new Tarea();
        copiar(solicitud, tarea);
        tarea.setCreadaPorId(solicitud.usuarioId());
        tarea.setFechaCreacion(ahora);
        tarea.setEstado(EstadoTarea.PENDIENTE);
        return tareaRepository.save(tarea);
    }

    public Tarea actualizar(Long id, TareaRequest solicitud) {
        Tarea tarea = buscarPorId(id);
        if (tarea.getEstado() == EstadoTarea.VALIDADA) {
            throw ReglaNegocioException.conflicto("Una tarea validada no se puede modificar");
        }
        validarPermisos(solicitud);
        copiar(solicitud, tarea);
        return tareaRepository.save(tarea);
    }

    /** No se elimina una tarea validada: forma parte del historial. */
    @Transactional
    public void borrar(Long id, Long usuarioId) {
        if (!usuariosClient.esAdministrador(usuarioId)) {
            throw ReglaNegocioException.prohibido("Solo un administrador puede eliminar tareas");
        }
        Tarea tarea = buscarPorId(id);
        if (tarea.getEstado() == EstadoTarea.VALIDADA) {
            throw ReglaNegocioException.conflicto("No se elimina una tarea validada: forma parte del historial");
        }
        evidenciaRepository.deleteByTareaId(id);
        validacionRepository.deleteByTareaId(id);
        tareaRepository.delete(tarea);
    }

    /** El estudiante responsable marca el inicio del trabajo. */
    public Tarea iniciar(Long id, AccionRequest solicitud) {
        Tarea tarea = buscarPorId(id);
        if (!tarea.getResponsableId().equals(solicitud.usuarioId())) {
            throw ReglaNegocioException.prohibido("Solo el responsable asignado puede iniciar esta tarea");
        }
        if (tarea.getEstado() != EstadoTarea.PENDIENTE && tarea.getEstado() != EstadoTarea.RECHAZADA) {
            throw ReglaNegocioException.conflicto("La tarea no está pendiente de ejecución");
        }
        tarea.setEstado(EstadoTarea.EN_DESARROLLO);
        return tareaRepository.save(tarea);
    }

    /** Entrega con comentario y foto. Solo desde EN_DESARROLLO o RECHAZADA. */
    @Transactional
    public Tarea entregar(Long id, EntregaRequest solicitud) {
        Tarea tarea = buscarPorId(id);
        if (!tarea.getResponsableId().equals(solicitud.usuarioId())) {
            throw ReglaNegocioException.prohibido("Solo el responsable asignado puede entregar esta tarea");
        }
        if (tarea.getEstado() != EstadoTarea.EN_DESARROLLO && tarea.getEstado() != EstadoTarea.RECHAZADA) {
            throw ReglaNegocioException.conflicto("Primero inicia la tarea; solo se entregan tareas en desarrollo o rechazadas");
        }
        String comentario = Validar.texto(solicitud.comentario(), "El comentario", 5, 300);
        String rutaFoto = Validar.texto(solicitud.rutaFoto(), "La fotografía de evidencia", 1, 300);
        long ahora = System.currentTimeMillis();

        Evidencia evidencia = new Evidencia();
        evidencia.setTareaId(id);
        evidencia.setUsuarioId(solicitud.usuarioId());
        evidencia.setRutaFoto(rutaFoto);
        evidencia.setComentario(comentario);
        evidencia.setFechaRegistro(ahora);
        evidenciaRepository.save(evidencia);

        tarea.setEstado(EstadoTarea.PENDIENTE_VALIDACION);
        tarea.setFechaRealizacion(ahora);
        tarea.setComentarioEstudiante(comentario);
        return tareaRepository.save(tarea);
    }

    /** Un administrador valida o rechaza; nadie revisa una tarea propia. */
    @Transactional
    public Tarea revisar(Long id, RevisionRequest solicitud) {
        Tarea tarea = buscarPorId(id);
        if (!usuariosClient.esAdministrador(solicitud.validadorId())) {
            throw ReglaNegocioException.prohibido("Solo un docente o tallerista puede validar actividades");
        }
        if (tarea.getResponsableId().equals(solicitud.validadorId())) {
            throw ReglaNegocioException.prohibido("No puedes validar una actividad de la que eres responsable");
        }
        if (tarea.getEstado() != EstadoTarea.PENDIENTE_VALIDACION) {
            throw ReglaNegocioException.conflicto("Solo se revisan actividades pendientes de validación");
        }
        ResultadoValidacion resultado =
                Validar.enumeracion(ResultadoValidacion.class, solicitud.resultado(), "El resultado");
        String motivo = solicitud.motivo() == null ? "" : solicitud.motivo().trim();
        if (resultado == ResultadoValidacion.RECHAZADA && motivo.length() < 5) {
            throw ReglaNegocioException.invalido("Debes indicar el motivo del rechazo");
        }

        Validacion validacion = new Validacion();
        validacion.setTareaId(id);
        validacion.setValidadorId(solicitud.validadorId());
        validacion.setResultado(resultado);
        validacion.setMotivo(motivo.isEmpty() ? "Actividad aprobada" : motivo);
        validacion.setFechaValidacion(System.currentTimeMillis());
        validacionRepository.save(validacion);

        tarea.setEstado(resultado == ResultadoValidacion.VALIDADA ? EstadoTarea.VALIDADA : EstadoTarea.RECHAZADA);
        return tareaRepository.save(tarea);
    }

    private void validarPermisos(TareaRequest solicitud) {
        if (!usuariosClient.esAdministrador(solicitud.usuarioId())) {
            throw ReglaNegocioException.prohibido("Solo un administrador puede crear o editar tareas");
        }
        if (!usuariosClient.esEstudiante(Validar.requerido(solicitud.responsableId(), "El responsable"))) {
            throw ReglaNegocioException.invalido("El responsable debe ser un estudiante activo del taller");
        }
    }

    private void copiar(TareaRequest solicitud, Tarea tarea) {
        tarea.setTipo(Validar.enumeracion(TipoTarea.class, solicitud.tipo(), "El tipo de tarea"));
        tarea.setDescripcion(Validar.texto(solicitud.descripcion(), "La descripción", 5, 120));
        tarea.setInstrucciones(Validar.texto(solicitud.instrucciones(), "Las instrucciones", 5, 300));
        tarea.setSectorId(Validar.requerido(solicitud.sectorId(), "El sector"));
        tarea.setCultivoId(solicitud.cultivoId());
        tarea.setResponsableId(solicitud.responsableId());
        tarea.setFechaProgramada(Validar.requerido(solicitud.fechaProgramada(), "La fecha programada"));
    }
}
