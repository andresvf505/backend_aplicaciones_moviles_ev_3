package cl.duoc.huerto.controller;

import cl.duoc.huerto.Datos;
import cl.duoc.huerto.dto.*;
import cl.duoc.huerto.model.*;
import cl.duoc.huerto.service.*;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Los controladores solo reciben la solicitud, llaman al servicio y
 * responden con el codigo HTTP correcto (200, 201 o 204).
 */
class ControladoresTest {

    @Nested
    @ExtendWith(MockitoExtension.class)
    @MockitoSettings(strictness = Strictness.LENIENT)
    class Usuarios {
        @Mock
        private UsuarioService servicio;
        @InjectMocks
        private UsuarioController controlador;

        private final UsuarioResponse ana = UsuarioResponse.desde(Datos.docente());

        @Test
        void endpoints() {
            RegistroRequest registro = new RegistroRequest("a", "b", "c", "d", "e", "f", null);
            LoginRequest login = new LoginRequest("profe.ana", "huerto123");
            PerfilRequest perfil = new PerfilRequest("a", "b", "c", null, null);
            when(servicio.listar()).thenReturn(List.of(ana));
            when(servicio.buscarPorId(1L)).thenReturn(ana);
            when(servicio.registrar(registro)).thenReturn(ana);
            when(servicio.login(login)).thenReturn(ana);
            when(servicio.actualizarPerfil(1L, perfil)).thenReturn(ana);

            assertEquals(List.of(ana), controlador.listar().getBody());
            assertEquals(ana, controlador.buscarPorId(1L).getBody());
            ResponseEntity<UsuarioResponse> creado = controlador.registrar(registro);
            assertEquals(HttpStatus.CREATED, creado.getStatusCode());
            assertEquals(HttpStatus.OK, controlador.login(login).getStatusCode());
            assertEquals(ana, controlador.actualizar(1L, perfil).getBody());
            assertEquals(HttpStatus.NO_CONTENT, controlador.borrar(1L).getStatusCode());
            verify(servicio).borrar(1L);
        }
    }

    @Nested
    @ExtendWith(MockitoExtension.class)
    @MockitoSettings(strictness = Strictness.LENIENT)
    class Sectores {
        @Mock
        private SectorService servicio;
        @InjectMocks
        private SectorController controlador;

        @Test
        void endpoints() {
            Sector s = Datos.sector(1L);
            SectorRequest req = new SectorRequest("S-01", "n", "d", "u", "EN_USO", null);
            when(servicio.listar()).thenReturn(List.of(s));
            when(servicio.buscarPorId(1L)).thenReturn(s);
            when(servicio.crear(req)).thenReturn(s);
            when(servicio.actualizar(1L, req)).thenReturn(s);

            assertEquals(1, controlador.listar().getBody().size());
            assertEquals(s, controlador.buscarPorId(1L).getBody());
            assertEquals(HttpStatus.CREATED, controlador.crear(req).getStatusCode());
            assertEquals(s, controlador.actualizar(1L, req).getBody());
            assertEquals(HttpStatus.NO_CONTENT, controlador.borrar(1L).getStatusCode());
            verify(servicio).borrar(1L);
        }
    }

    @Nested
    @ExtendWith(MockitoExtension.class)
    @MockitoSettings(strictness = Strictness.LENIENT)
    class Cultivos {
        @Mock
        private CultivoService servicio;
        @InjectMocks
        private CultivoController controlador;

        @Test
        void endpoints() {
            Cultivo c = Datos.cultivo(1L, 1L);
            CultivoRequest req = new CultivoRequest("n", "d", 1L, 1L, null, 2L, 1, "SEMBRADO", "", null);
            EstadoRequest estado = new EstadoRequest("COSECHADO");
            when(servicio.listar(1L)).thenReturn(List.of(c));
            when(servicio.buscarPorId(1L)).thenReturn(c);
            when(servicio.crear(req)).thenReturn(c);
            when(servicio.actualizar(1L, req)).thenReturn(c);
            when(servicio.cambiarEstado(1L, estado)).thenReturn(c);

            assertEquals(List.of(c), controlador.listar(1L).getBody());
            assertEquals(c, controlador.buscarPorId(1L).getBody());
            assertEquals(HttpStatus.CREATED, controlador.crear(req).getStatusCode());
            assertEquals(c, controlador.actualizar(1L, req).getBody());
            assertEquals(c, controlador.cambiarEstado(1L, estado).getBody());
            assertEquals(HttpStatus.NO_CONTENT, controlador.borrar(1L).getStatusCode());
            verify(servicio).borrar(1L);
        }
    }

    @Nested
    @ExtendWith(MockitoExtension.class)
    @MockitoSettings(strictness = Strictness.LENIENT)
    class Lecturas {
        @Mock
        private LecturaService servicio;
        @InjectMocks
        private LecturaController controlador;

        @Test
        void endpoints() {
            LecturaSensor l = new LecturaSensor();
            when(servicio.listar()).thenReturn(List.of(l));
            when(servicio.simularFaltantes()).thenReturn(List.of(l, l));

            assertEquals(1, controlador.listar().getBody().size());
            assertEquals(2, controlador.simular().getBody().size());
        }
    }

    @Nested
    @ExtendWith(MockitoExtension.class)
    @MockitoSettings(strictness = Strictness.LENIENT)
    class Tareas {
        @Mock
        private TareaService servicio;
        @InjectMocks
        private TareaController controlador;

        @Test
        void endpoints() {
            Tarea t = Datos.tarea(1L, EstadoTarea.PENDIENTE);
            TareaRequest req = new TareaRequest("RIEGO", "d", "i", 1L, null, 3L, 1L, 1L);
            AccionRequest accion = new AccionRequest(3L);
            EntregaRequest entrega = new EntregaRequest(3L, "c", "f");
            RevisionRequest revision = new RevisionRequest(1L, "VALIDADA", null);
            when(servicio.listar(3L)).thenReturn(List.of(t));
            when(servicio.buscarPorId(1L)).thenReturn(t);
            when(servicio.crear(req)).thenReturn(t);
            when(servicio.actualizar(1L, req)).thenReturn(t);
            when(servicio.iniciar(1L, accion)).thenReturn(t);
            when(servicio.entregar(1L, entrega)).thenReturn(t);
            when(servicio.revisar(1L, revision)).thenReturn(t);

            assertEquals(List.of(t), controlador.listar(3L).getBody());
            assertEquals(t, controlador.buscarPorId(1L).getBody());
            assertEquals(HttpStatus.CREATED, controlador.crear(req).getStatusCode());
            assertEquals(t, controlador.actualizar(1L, req).getBody());
            assertEquals(HttpStatus.NO_CONTENT, controlador.borrar(1L, 1L).getStatusCode());
            verify(servicio).borrar(1L, 1L);
            assertEquals(t, controlador.iniciar(1L, accion).getBody());
            assertEquals(t, controlador.entregar(1L, entrega).getBody());
            assertEquals(t, controlador.revisar(1L, revision).getBody());
        }
    }

    @Nested
    @ExtendWith(MockitoExtension.class)
    @MockitoSettings(strictness = Strictness.LENIENT)
    class Historial {
        @Mock
        private TareaService servicio;
        @InjectMocks
        private HistorialController controlador;

        @Test
        void endpoints() {
            when(servicio.evidencias()).thenReturn(List.of(new Evidencia()));
            when(servicio.validaciones()).thenReturn(List.of());

            assertEquals(1, controlador.evidencias().getBody().size());
            assertTrue(controlador.validaciones().getBody().isEmpty());
        }
    }

    @Nested
    @ExtendWith(MockitoExtension.class)
    @MockitoSettings(strictness = Strictness.LENIENT)
    class Reportes {
        @Mock
        private ReporteService servicio;
        @InjectMocks
        private ReporteController controlador;

        @Test
        void endpoints() {
            ReporteProblema r = Datos.reporte(1L);
            ReporteRequest req = new ReporteRequest(1L, null, "OTRO", "d", 3L, null);
            EstadoReporteRequest estado = new EstadoReporteRequest(1L, "CERRADO", "");
            when(servicio.listar()).thenReturn(List.of(r));
            when(servicio.buscarPorId(1L)).thenReturn(r);
            when(servicio.registrar(req)).thenReturn(r);
            when(servicio.actualizarEstado(1L, estado)).thenReturn(r);

            assertEquals(List.of(r), controlador.listar().getBody());
            assertEquals(r, controlador.buscarPorId(1L).getBody());
            assertEquals(HttpStatus.CREATED, controlador.registrar(req).getStatusCode());
            assertEquals(r, controlador.actualizarEstado(1L, estado).getBody());
            assertEquals(HttpStatus.NO_CONTENT, controlador.borrar(1L).getStatusCode());
            verify(servicio).borrar(1L);
        }
    }
}
