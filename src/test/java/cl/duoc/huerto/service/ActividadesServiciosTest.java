package cl.duoc.huerto.service;

import cl.duoc.huerto.Datos;
import cl.duoc.huerto.dto.*;
import cl.duoc.huerto.exception.ReglaNegocioException;
import cl.duoc.huerto.model.*;
import cl.duoc.huerto.repository.EvidenciaRepository;
import cl.duoc.huerto.repository.ReporteRepository;
import cl.duoc.huerto.repository.TareaRepository;
import cl.duoc.huerto.repository.ValidacionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/** Tareas (flujo de estados y permisos) y reportes de problemas. */
class ActividadesServiciosTest {

    @Nested
    @ExtendWith(MockitoExtension.class)
    @MockitoSettings(strictness = Strictness.LENIENT)
    class Tareas {

        @Mock
        private TareaRepository tareas;
        @Mock
        private EvidenciaRepository evidencias;
        @Mock
        private ValidacionRepository validaciones;
        @Mock
        private PermisoService permisos;

        @InjectMocks
        private TareaService servicio;

        private final TareaRequest solicitud =
                new TareaRequest("riego", "Regar el sector", "Regar temprano", 1L, 2L, 3L, 500L, 1L);

        @BeforeEach
        void preparar() {
            when(tareas.save(any(Tarea.class))).thenAnswer(inv -> inv.getArgument(0));
            when(permisos.esAdministrador(1L)).thenReturn(true);
            when(permisos.esEstudiante(3L)).thenReturn(true);
        }

        private void existe(Tarea tarea) {
            when(tareas.findById(tarea.getId())).thenReturn(Optional.of(tarea));
        }

        @Test
        void listarTodasOPorResponsableYHistorial() {
            when(tareas.findAllByOrderByFechaProgramadaAscIdAsc()).thenReturn(List.of(Datos.tarea(1L, EstadoTarea.PENDIENTE)));
            when(tareas.findByResponsableIdOrderByFechaProgramadaAsc(3L)).thenReturn(List.of());
            when(evidencias.findAllByOrderByFechaRegistroDesc()).thenReturn(List.of(new Evidencia()));
            when(validaciones.findAllByOrderByFechaValidacionDesc()).thenReturn(List.of(new Validacion(), new Validacion()));

            assertEquals(1, servicio.listar(null).size());
            assertTrue(servicio.listar(3L).isEmpty());
            assertEquals(1, servicio.evidencias().size());
            assertEquals(2, servicio.validaciones().size());
        }

        @Test
        void buscarInexistente() {
            when(tareas.findById(9L)).thenReturn(Optional.empty());

            assertEquals(HttpStatus.NOT_FOUND,
                    assertThrows(ReglaNegocioException.class, () -> servicio.buscarPorId(9L)).getEstado());
        }

        @Test
        void crearQuedaPendiente() {
            Tarea t = servicio.crear(solicitud);

            assertEquals(EstadoTarea.PENDIENTE, t.getEstado());
            assertEquals(TipoTarea.RIEGO, t.getTipo());
            assertEquals(1L, t.getCreadaPorId());
            assertEquals(3L, t.getResponsableId());
            assertEquals(2L, t.getCultivoId());
            assertEquals(500L, t.getFechaProgramada());
            assertNotNull(t.getFechaCreacion());
        }

        @Test
        void soloAdministradorCreaYResponsableDebeSerEstudiante() {
            when(permisos.esAdministrador(1L)).thenReturn(false);
            assertEquals(HttpStatus.FORBIDDEN,
                    assertThrows(ReglaNegocioException.class, () -> servicio.crear(solicitud)).getEstado());

            when(permisos.esAdministrador(1L)).thenReturn(true);
            when(permisos.esEstudiante(3L)).thenReturn(false);
            assertEquals("El responsable debe ser un estudiante activo del taller",
                    assertThrows(ReglaNegocioException.class, () -> servicio.crear(solicitud)).getMessage());
        }

        @Test
        void actualizarNoPermiteTareasValidadas() {
            existe(Datos.tarea(1L, EstadoTarea.PENDIENTE));
            existe(Datos.tarea(2L, EstadoTarea.VALIDADA));

            assertEquals("Regar temprano", servicio.actualizar(1L, solicitud).getInstrucciones());
            assertEquals(HttpStatus.CONFLICT,
                    assertThrows(ReglaNegocioException.class, () -> servicio.actualizar(2L, solicitud)).getEstado());
        }

        @Test
        void borrarEliminaEvidenciasYValidaciones() {
            Tarea tarea = Datos.tarea(1L, EstadoTarea.RECHAZADA);
            existe(tarea);

            servicio.borrar(1L, 1L);

            verify(evidencias).deleteByTareaId(1L);
            verify(validaciones).deleteByTareaId(1L);
            verify(tareas).delete(tarea);
        }

        @Test
        void borrarReglas() {
            existe(Datos.tarea(2L, EstadoTarea.VALIDADA));

            assertEquals(HttpStatus.FORBIDDEN,
                    assertThrows(ReglaNegocioException.class, () -> servicio.borrar(2L, 3L)).getEstado());
            assertEquals(HttpStatus.CONFLICT,
                    assertThrows(ReglaNegocioException.class, () -> servicio.borrar(2L, 1L)).getEstado());
        }

        @Test
        void iniciarSoloElResponsableYDesdePendienteORechazada() {
            existe(Datos.tarea(1L, EstadoTarea.PENDIENTE));
            existe(Datos.tarea(2L, EstadoTarea.RECHAZADA));
            existe(Datos.tarea(3L, EstadoTarea.VALIDADA));

            assertEquals(EstadoTarea.EN_DESARROLLO, servicio.iniciar(1L, new AccionRequest(3L)).getEstado());
            assertEquals(EstadoTarea.EN_DESARROLLO, servicio.iniciar(2L, new AccionRequest(3L)).getEstado());
            assertEquals(HttpStatus.FORBIDDEN,
                    assertThrows(ReglaNegocioException.class, () -> servicio.iniciar(1L, new AccionRequest(4L))).getEstado());
            assertEquals(HttpStatus.CONFLICT,
                    assertThrows(ReglaNegocioException.class, () -> servicio.iniciar(3L, new AccionRequest(3L))).getEstado());
        }

        @Test
        void entregarCreaEvidenciaYPasaAPendienteValidacion() {
            existe(Datos.tarea(1L, EstadoTarea.EN_DESARROLLO));

            Tarea t = servicio.entregar(1L, new EntregaRequest(3L, "Sector regado completo", "foto.jpg"));

            assertEquals(EstadoTarea.PENDIENTE_VALIDACION, t.getEstado());
            assertEquals("Sector regado completo", t.getComentarioEstudiante());
            assertNotNull(t.getFechaRealizacion());
            ArgumentCaptor<Evidencia> captor = ArgumentCaptor.forClass(Evidencia.class);
            verify(evidencias).save(captor.capture());
            Evidencia e = captor.getValue();
            assertEquals(1L, e.getTareaId());
            assertEquals(3L, e.getUsuarioId());
            assertEquals("foto.jpg", e.getRutaFoto());
            assertEquals("Sector regado completo", e.getComentario());
            assertEquals(t.getFechaRealizacion(), e.getFechaRegistro());
            assertNull(e.getId());
        }

        @Test
        void entregarReglas() {
            existe(Datos.tarea(1L, EstadoTarea.PENDIENTE));
            existe(Datos.tarea(2L, EstadoTarea.EN_DESARROLLO));

            assertEquals(HttpStatus.FORBIDDEN, assertThrows(ReglaNegocioException.class,
                    () -> servicio.entregar(2L, new EntregaRequest(4L, "Comentario", "f.jpg"))).getEstado());
            assertEquals(HttpStatus.CONFLICT, assertThrows(ReglaNegocioException.class,
                    () -> servicio.entregar(1L, new EntregaRequest(3L, "Comentario", "f.jpg"))).getEstado());
            assertEquals("La fotografía de evidencia es obligatorio", assertThrows(ReglaNegocioException.class,
                    () -> servicio.entregar(2L, new EntregaRequest(3L, "Comentario", ""))).getMessage());
        }

        @Test
        void validarTarea() {
            existe(Datos.tarea(1L, EstadoTarea.PENDIENTE_VALIDACION));

            Tarea t = servicio.revisar(1L, new RevisionRequest(1L, "VALIDADA", null));

            assertEquals(EstadoTarea.VALIDADA, t.getEstado());
            ArgumentCaptor<Validacion> captor = ArgumentCaptor.forClass(Validacion.class);
            verify(validaciones).save(captor.capture());
            Validacion v = captor.getValue();
            assertEquals("Actividad aprobada", v.getMotivo());
            assertEquals(ResultadoValidacion.VALIDADA, v.getResultado());
            assertEquals(1L, v.getTareaId());
            assertEquals(1L, v.getValidadorId());
            assertNotNull(v.getFechaValidacion());
            assertNull(v.getId());
        }

        @Test
        void rechazarExigeMotivo() {
            existe(Datos.tarea(1L, EstadoTarea.PENDIENTE_VALIDACION));

            assertEquals("Debes indicar el motivo del rechazo", assertThrows(ReglaNegocioException.class,
                    () -> servicio.revisar(1L, new RevisionRequest(1L, "RECHAZADA", " no "))).getMessage());
            assertEquals(EstadoTarea.RECHAZADA,
                    servicio.revisar(1L, new RevisionRequest(1L, "rechazada", "Falta la foto del sector")).getEstado());
        }

        @Test
        void revisarReglas() {
            Tarea propia = Datos.tarea(1L, EstadoTarea.PENDIENTE_VALIDACION);
            propia.setResponsableId(1L);
            existe(propia);
            existe(Datos.tarea(2L, EstadoTarea.EN_DESARROLLO));

            assertEquals("Solo un docente o tallerista puede validar actividades", assertThrows(ReglaNegocioException.class,
                    () -> servicio.revisar(2L, new RevisionRequest(3L, "VALIDADA", null))).getMessage());
            assertEquals("No puedes validar una actividad de la que eres responsable", assertThrows(ReglaNegocioException.class,
                    () -> servicio.revisar(1L, new RevisionRequest(1L, "VALIDADA", null))).getMessage());
            assertEquals(HttpStatus.CONFLICT, assertThrows(ReglaNegocioException.class,
                    () -> servicio.revisar(2L, new RevisionRequest(1L, "VALIDADA", null))).getEstado());
        }
    }

    @Nested
    @ExtendWith(MockitoExtension.class)
    @MockitoSettings(strictness = Strictness.LENIENT)
    class Reportes {

        @Mock
        private ReporteRepository reportes;
        @Mock
        private PermisoService permisos;

        @InjectMocks
        private ReporteService servicio;

        @BeforeEach
        void preparar() {
            when(reportes.save(any(ReporteProblema.class))).thenAnswer(inv -> inv.getArgument(0));
            when(permisos.esEstudiante(3L)).thenReturn(true);
            when(permisos.esAdministrador(1L)).thenReturn(true);
        }

        @Test
        void listarYBuscar() {
            when(reportes.findAllByOrderByFechaReporteDesc()).thenReturn(List.of(Datos.reporte(1L)));
            when(reportes.findById(1L)).thenReturn(Optional.of(Datos.reporte(1L)));
            when(reportes.findById(2L)).thenReturn(Optional.empty());

            assertEquals(1, servicio.listar().size());
            assertEquals(TipoProblema.FALTA_DE_AGUA, servicio.buscarPorId(1L).getTipo());
            assertEquals("El reporte 2 no existe",
                    assertThrows(ReglaNegocioException.class, () -> servicio.buscarPorId(2L)).getMessage());
        }

        @Test
        void estudianteRegistraReporteInformado() {
            ReporteProblema r = servicio.registrar(
                    new ReporteRequest(1L, 4L, "plaga_simulada", "Hay pulgones en las hojas", 3L, "foto.jpg"));

            assertEquals(EstadoReporte.INFORMADO, r.getEstado());
            assertEquals(TipoProblema.PLAGA_SIMULADA, r.getTipo());
            assertEquals(1L, r.getSectorId());
            assertEquals(4L, r.getCultivoId());
            assertEquals(3L, r.getReportadoPorId());
            assertEquals("foto.jpg", r.getRutaFoto());
            assertEquals("", r.getComentarioDocente());
            assertNull(r.getFechaCierre());
            assertNotNull(r.getFechaReporte());
        }

        @Test
        void docenteNoReportaProblemas() {
            assertEquals(HttpStatus.FORBIDDEN, assertThrows(ReglaNegocioException.class, () -> servicio.registrar(
                    new ReporteRequest(1L, null, "OTRO", "Descripcion larga", 1L, null))).getEstado());
        }

        @Test
        void docenteAtiendeYCierraReporte() {
            when(reportes.findById(1L)).thenReturn(Optional.of(Datos.reporte(1L)));

            ReporteProblema enRevision = servicio.actualizarEstado(1L, new EstadoReporteRequest(1L, "EN_REVISION", null));
            assertEquals(EstadoReporte.EN_REVISION, enRevision.getEstado());
            assertNull(enRevision.getFechaCierre());

            ReporteProblema atendido = servicio.actualizarEstado(1L, new EstadoReporteRequest(1L, "ATENDIDO", "Listo"));
            assertEquals("Listo", atendido.getComentarioDocente());
            assertNotNull(atendido.getFechaCierre());

            assertNotNull(servicio.actualizarEstado(1L, new EstadoReporteRequest(1L, "CERRADO", "")).getFechaCierre());
        }

        @Test
        void estudianteNoCambiaEstado() {
            assertEquals(HttpStatus.FORBIDDEN, assertThrows(ReglaNegocioException.class,
                    () -> servicio.actualizarEstado(1L, new EstadoReporteRequest(3L, "CERRADO", ""))).getEstado());
        }

        @Test
        void borrar() {
            ReporteProblema r = Datos.reporte(1L);
            when(reportes.findById(1L)).thenReturn(Optional.of(r));

            servicio.borrar(1L);

            verify(reportes).delete(r);
        }
    }
}
