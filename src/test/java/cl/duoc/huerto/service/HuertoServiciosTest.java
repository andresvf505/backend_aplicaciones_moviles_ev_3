package cl.duoc.huerto.service;

import cl.duoc.huerto.Datos;
import cl.duoc.huerto.dto.CultivoRequest;
import cl.duoc.huerto.dto.EstadoRequest;
import cl.duoc.huerto.dto.SectorRequest;
import cl.duoc.huerto.exception.ReglaNegocioException;
import cl.duoc.huerto.model.*;
import cl.duoc.huerto.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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

/** Sectores, cultivos, lecturas simuladas y permisos. */
class HuertoServiciosTest {

    @Nested
    @ExtendWith(MockitoExtension.class)
    @MockitoSettings(strictness = Strictness.LENIENT)
    class Permisos {

        @Mock
        private UsuarioRepository usuarios;

        @InjectMocks
        private PermisoService servicio;

        @Test
        void docenteEsAdministrador() {
            when(usuarios.findById(1L)).thenReturn(Optional.of(Datos.docente()));

            assertTrue(servicio.esAdministrador(1L));
            assertFalse(servicio.esEstudiante(1L));
        }

        @Test
        void estudianteEsEstudiante() {
            when(usuarios.findById(3L)).thenReturn(Optional.of(Datos.estudiante()));

            assertTrue(servicio.esEstudiante(3L));
            assertFalse(servicio.esAdministrador(3L));
        }

        @Test
        void sinIdNoExisteOInactivo() {
            Usuario inactivo = Datos.estudiante();
            inactivo.setActivo(false);
            when(usuarios.findById(3L)).thenReturn(Optional.of(inactivo));
            when(usuarios.findById(9L)).thenReturn(Optional.empty());

            assertEquals("Falta indicar el usuario",
                    assertThrows(ReglaNegocioException.class, () -> servicio.obtenerActivo(null)).getMessage());
            assertEquals("El usuario 9 no existe",
                    assertThrows(ReglaNegocioException.class, () -> servicio.obtenerActivo(9L)).getMessage());
            assertEquals("El usuario 3 no está activo",
                    assertThrows(ReglaNegocioException.class, () -> servicio.obtenerActivo(3L)).getMessage());
        }
    }

    @Nested
    @ExtendWith(MockitoExtension.class)
    @MockitoSettings(strictness = Strictness.LENIENT)
    class Sectores {

        @Mock
        private SectorRepository sectores;
        @Mock
        private CultivoRepository cultivos;
        @Mock
        private LecturaSensorRepository lecturas;
        @Mock
        private TareaRepository tareas;
        @Mock
        private ReporteRepository reportes;
        @Mock
        private LecturaService lecturaService;

        @InjectMocks
        private SectorService servicio;

        private final SectorRequest valido =
                new SectorRequest(" s-05 ", "Huerto Este", "Cama nueva del patio", "Patio este", "disponible", 1L);

        @BeforeEach
        void guardarAsignaId() {
            when(sectores.save(any(Sector.class))).thenAnswer(inv -> {
                Sector s = inv.getArgument(0);
                if (s.getId() == null) {
                    s.setId(5L);
                }
                return s;
            });
        }

        @Test
        void listarYBuscar() {
            when(sectores.findAllByOrderByNombreAsc()).thenReturn(List.of(Datos.sector(1L)));
            when(sectores.findById(1L)).thenReturn(Optional.of(Datos.sector(1L)));
            when(sectores.findById(7L)).thenReturn(Optional.empty());

            assertEquals(1, servicio.listar().size());
            assertEquals("S-01", servicio.buscarPorId(1L).getCodigo());
            assertEquals(HttpStatus.NOT_FOUND,
                    assertThrows(ReglaNegocioException.class, () -> servicio.buscarPorId(7L)).getEstado());
        }

        @Test
        void crearNormalizaCodigoYSimulaPrimeraLectura() {
            Sector creado = servicio.crear(valido);

            assertEquals("S-05", creado.getCodigo());
            assertEquals(EstadoSector.DISPONIBLE, creado.getEstado());
            assertNotNull(creado.getFechaCreacion());
            assertEquals(creado.getFechaCreacion(), creado.getFechaActualizacion());
            verify(lecturaService).simularPara(5L);
        }

        @Test
        void crearConCodigoRepetidoOInvalido() {
            when(sectores.existsByCodigoIgnoreCase("S-05")).thenReturn(true);

            assertEquals(HttpStatus.CONFLICT,
                    assertThrows(ReglaNegocioException.class, () -> servicio.crear(valido)).getEstado());
            SectorRequest malo = new SectorRequest("x", "Huerto", "Descripcion", "Patio", "EN_USO", null);
            assertEquals(HttpStatus.BAD_REQUEST,
                    assertThrows(ReglaNegocioException.class, () -> servicio.crear(malo)).getEstado());
            SectorRequest sinCodigo = new SectorRequest(null, "Huerto", "Descripcion", "Patio", "EN_USO", null);
            assertThrows(ReglaNegocioException.class, () -> servicio.crear(sinCodigo));
        }

        @Test
        void actualizar() {
            when(sectores.findById(1L)).thenReturn(Optional.of(Datos.sector(1L)));

            Sector actualizado = servicio.actualizar(1L, valido);

            assertEquals("Huerto Este", actualizado.getNombre());
            assertEquals("Patio este", actualizado.getUbicacion());
        }

        @Test
        void actualizarConCodigoDeOtroSector() {
            when(sectores.findById(1L)).thenReturn(Optional.of(Datos.sector(1L)));
            when(sectores.existsByCodigoIgnoreCaseAndIdNot("S-05", 1L)).thenReturn(true);

            assertThrows(ReglaNegocioException.class, () -> servicio.actualizar(1L, valido));
        }

        @Test
        void borrarEliminaCultivosLecturasYReportes() {
            Sector sector = Datos.sector(1L);
            when(sectores.findById(1L)).thenReturn(Optional.of(sector));

            servicio.borrar(1L);

            verify(cultivos).deleteBySectorId(1L);
            verify(lecturas).deleteBySectorId(1L);
            verify(reportes).deleteBySectorId(1L);
            verify(sectores).delete(sector);
        }

        @Test
        void noSeBorraUnSectorConTareas() {
            when(sectores.findById(1L)).thenReturn(Optional.of(Datos.sector(1L)));
            when(tareas.existsBySectorId(1L)).thenReturn(true);

            ReglaNegocioException e = assertThrows(ReglaNegocioException.class, () -> servicio.borrar(1L));

            assertEquals("No se puede eliminar: el sector tiene tareas registradas", e.getMessage());
            verify(sectores, never()).delete(any());
        }
    }

    @Nested
    @ExtendWith(MockitoExtension.class)
    @MockitoSettings(strictness = Strictness.LENIENT)
    class Cultivos {

        @Mock
        private CultivoRepository cultivos;
        @Mock
        private SectorRepository sectores;
        @Mock
        private TareaRepository tareas;

        @InjectMocks
        private CultivoService servicio;

        private CultivoRequest solicitud(Long trasplante, Long cosecha, Integer riego) {
            return new CultivoRequest("Lechuga", "Almacigo de lechuga", 1L, 100L, trasplante, cosecha, riego,
                    "SEMBRADO", " sin novedad ", "foto.jpg");
        }

        @BeforeEach
        void preparar() {
            when(cultivos.save(any(Cultivo.class))).thenAnswer(inv -> inv.getArgument(0));
            when(sectores.existsById(1L)).thenReturn(true);
        }

        @Test
        void listarTodosOPorSector() {
            when(cultivos.findAllByOrderByNombreAsc()).thenReturn(List.of(Datos.cultivo(1L, 1L), Datos.cultivo(2L, 2L)));
            when(cultivos.findBySectorId(2L)).thenReturn(List.of(Datos.cultivo(2L, 2L)));

            assertEquals(2, servicio.listar(null).size());
            assertEquals(1, servicio.listar(2L).size());
        }

        @Test
        void crearCopiaTodosLosCampos() {
            Cultivo c = servicio.crear(solicitud(150L, 200L, 3));

            assertEquals("Lechuga", c.getNombre());
            assertEquals(1L, c.getSectorId());
            assertEquals(150L, c.getFechaTrasplante());
            assertEquals(200L, c.getFechaCosechaEstimada());
            assertEquals(3, c.getFrecuenciaRiegoDias());
            assertEquals(EstadoCultivo.SEMBRADO, c.getEstado());
            assertEquals("sin novedad", c.getObservaciones());
            assertEquals("foto.jpg", c.getRutaFoto());
        }

        @Test
        void reglasDeFechasYRiego() {
            assertEquals("La cosecha estimada no puede ser anterior a la siembra",
                    assertThrows(ReglaNegocioException.class, () -> servicio.crear(solicitud(null, 50L, 2))).getMessage());
            assertEquals("El trasplante debe quedar entre la siembra y la cosecha",
                    assertThrows(ReglaNegocioException.class, () -> servicio.crear(solicitud(50L, 200L, 2))).getMessage());
            assertThrows(ReglaNegocioException.class, () -> servicio.crear(solicitud(250L, 200L, 2)));
            assertThrows(ReglaNegocioException.class, () -> servicio.crear(solicitud(null, 200L, 0)));
            assertThrows(ReglaNegocioException.class, () -> servicio.crear(solicitud(null, 200L, 61)));
            assertThrows(ReglaNegocioException.class, () -> servicio.crear(solicitud(null, 200L, null)));
        }

        @Test
        void sectorInexistente() {
            when(sectores.existsById(1L)).thenReturn(false);

            assertEquals("El sector seleccionado no existe",
                    assertThrows(ReglaNegocioException.class, () -> servicio.crear(solicitud(null, 200L, 2))).getMessage());
        }

        @Test
        void actualizarYCambiarEstado() {
            when(cultivos.findById(1L)).thenReturn(Optional.of(Datos.cultivo(1L, 1L)));

            assertEquals(3, servicio.actualizar(1L, solicitud(null, 200L, 3)).getFrecuenciaRiegoDias());
            assertEquals(EstadoCultivo.COSECHADO,
                    servicio.cambiarEstado(1L, new EstadoRequest("cosechado")).getEstado());
        }

        @Test
        void buscarInexistente() {
            when(cultivos.findById(8L)).thenReturn(Optional.empty());

            assertEquals("El cultivo 8 no existe",
                    assertThrows(ReglaNegocioException.class, () -> servicio.buscarPorId(8L)).getMessage());
        }

        @Test
        void borrarSoloSinTareas() {
            Cultivo cultivo = Datos.cultivo(1L, 1L);
            when(cultivos.findById(1L)).thenReturn(Optional.of(cultivo));

            servicio.borrar(1L);
            verify(cultivos).delete(cultivo);

            when(tareas.existsByCultivoId(1L)).thenReturn(true);
            assertEquals(HttpStatus.CONFLICT,
                    assertThrows(ReglaNegocioException.class, () -> servicio.borrar(1L)).getEstado());
        }
    }

    @Nested
    @ExtendWith(MockitoExtension.class)
    @MockitoSettings(strictness = Strictness.LENIENT)
    class Lecturas {

        @Mock
        private LecturaSensorRepository lecturas;
        @Mock
        private SectorRepository sectores;

        @InjectMocks
        private LecturaService servicio;

        @BeforeEach
        void guardarDevuelveLoMismo() {
            when(lecturas.save(any(LecturaSensor.class))).thenAnswer(inv -> inv.getArgument(0));
        }

        @Test
        void listarOrdenado() {
            when(lecturas.findAllByOrderByFechaLecturaDesc()).thenReturn(List.of(new LecturaSensor()));

            assertEquals(1, servicio.listar().size());
        }

        @Test
        void lecturaSimuladaQuedaEnRangoYMarcada() {
            for (int i = 0; i < 50; i++) {
                LecturaSensor l = servicio.simularPara(2L);
                assertEquals(2L, l.getSectorId());
                assertEquals(LecturaService.ORIGEN, l.getOrigen());
                assertTrue(l.getHumedadSuelo() >= 20 && l.getHumedadSuelo() < 80);
                assertTrue(l.getTemperatura() >= 15.0 && l.getTemperatura() < 29.0);
                assertTrue(l.getHumedadAmbiente() >= 40 && l.getHumedadAmbiente() < 85);
                assertNotNull(l.getFechaLectura());
                EstadoLectura esperado = l.getHumedadSuelo() < 30 ? EstadoLectura.BAJO
                        : l.getHumedadSuelo() > 65 ? EstadoLectura.ALTO : EstadoLectura.NORMAL;
                assertEquals(esperado, l.getEstado());
            }
        }

        @Test
        void simularFaltantesSoloParaSectoresSinLecturas() {
            when(sectores.findAll()).thenReturn(List.of(Datos.sector(1L), Datos.sector(2L)));
            when(lecturas.existsBySectorId(1L)).thenReturn(true);
            when(lecturas.existsBySectorId(2L)).thenReturn(false);

            List<LecturaSensor> nuevas = servicio.simularFaltantes();

            assertEquals(1, nuevas.size());
            assertEquals(2L, nuevas.get(0).getSectorId());
        }
    }
}
