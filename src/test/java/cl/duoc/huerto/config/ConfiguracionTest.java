package cl.duoc.huerto.config;

import cl.duoc.huerto.Datos;
import cl.duoc.huerto.exception.ManejadorErrores;
import cl.duoc.huerto.exception.ReglaNegocioException;
import cl.duoc.huerto.model.*;
import cl.duoc.huerto.repository.CultivoRepository;
import cl.duoc.huerto.repository.LecturaSensorRepository;
import cl.duoc.huerto.repository.SectorRepository;
import cl.duoc.huerto.repository.UsuarioRepository;
import cl.duoc.huerto.service.LecturaService;
import cl.duoc.huerto.util.Claves;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/** Datos de prueba que se cargan al arrancar y respuestas de error en JSON. */
class ConfiguracionTest {

    private final UsuarioRepository usuarios = mock(UsuarioRepository.class);
    private final SectorRepository sectores = mock(SectorRepository.class);
    private final CultivoRepository cultivos = mock(CultivoRepository.class);
    private final LecturaSensorRepository lecturas = mock(LecturaSensorRepository.class);

    @Test
    @SuppressWarnings("unchecked")
    void cargaUsuariosYLuegoElHuertoConSusResponsables() throws Exception {
        AtomicLong ids = new AtomicLong(10);
        when(usuarios.count()).thenReturn(0L);
        when(sectores.count()).thenReturn(0L);
        when(usuarios.findByNombreUsuario("profe.ana")).thenReturn(Optional.of(Datos.usuario(1L, "profe.ana", Cargo.DOCENTE)));
        when(usuarios.findByNombreUsuario("taller.luis")).thenReturn(Optional.of(Datos.usuario(2L, "taller.luis", Cargo.TALLERISTA)));
        when(sectores.save(any(Sector.class))).thenAnswer(inv -> {
            Sector s = inv.getArgument(0);
            s.setId(ids.incrementAndGet());
            return s;
        });

        new DataInitializer().cargarDatosDemo(usuarios, sectores, cultivos, lecturas).run();

        ArgumentCaptor<List<Usuario>> cuentas = ArgumentCaptor.forClass(List.class);
        verify(usuarios).saveAll(cuentas.capture());
        assertEquals(5, cuentas.getValue().size());
        Usuario ana = cuentas.getValue().get(0);
        assertEquals("profe.ana", ana.getNombreUsuario());
        assertEquals(Rol.ADMINISTRADOR, ana.getRol());
        assertTrue(Claves.coincide(DataInitializer.CLAVE_DEMO, ana.getClaveHash()));
        assertEquals(Rol.ESTUDIANTE, cuentas.getValue().get(4).getRol());

        ArgumentCaptor<Sector> sectoresGuardados = ArgumentCaptor.forClass(Sector.class);
        verify(sectores, times(4)).save(sectoresGuardados.capture());
        assertEquals(1L, sectoresGuardados.getAllValues().get(0).getResponsableId());
        assertEquals(2L, sectoresGuardados.getAllValues().get(1).getResponsableId());
        assertNull(sectoresGuardados.getAllValues().get(3).getResponsableId());

        ArgumentCaptor<Cultivo> cultivosGuardados = ArgumentCaptor.forClass(Cultivo.class);
        verify(cultivos, times(6)).save(cultivosGuardados.capture());
        Cultivo lechuga = cultivosGuardados.getAllValues().get(0);
        assertEquals(11L, lechuga.getSectorId());
        assertTrue(lechuga.getFechaSiembra() < lechuga.getFechaCosechaEstimada());
        assertNull(cultivosGuardados.getAllValues().get(2).getFechaTrasplante());

        ArgumentCaptor<LecturaSensor> lecturasGuardadas = ArgumentCaptor.forClass(LecturaSensor.class);
        verify(lecturas, times(6)).save(lecturasGuardadas.capture());
        assertTrue(lecturasGuardadas.getAllValues().stream()
                .allMatch(l -> LecturaService.ORIGEN.equals(l.getOrigen())));
    }

    @Test
    void noRepiteDatosSiYaExisten() throws Exception {
        when(usuarios.count()).thenReturn(5L);
        when(sectores.count()).thenReturn(4L);

        new DataInitializer().cargarDatosDemo(usuarios, sectores, cultivos, lecturas).run();

        verify(usuarios, never()).saveAll(any());
        verify(sectores, never()).save(any());
        verifyNoInteractions(cultivos, lecturas);
    }

    @Test
    void diaDevuelveLaMedianocheDeHoyEnSantiago() {
        ZoneId zona = ZoneId.of("America/Santiago");
        long hoy = LocalDate.now(zona).atStartOfDay(zona).toInstant().toEpochMilli();

        assertEquals(hoy, DataInitializer.dia(0));
        assertTrue(DataInitializer.dia(1) > hoy);
    }

    @Test
    void manejadorDevuelveMensajeConSuCodigo() {
        ManejadorErrores manejador = new ManejadorErrores();

        ResponseEntity<Map<String, String>> negocio =
                manejador.reglaNegocio(ReglaNegocioException.conflicto("Repetido"));
        assertEquals(HttpStatus.CONFLICT, negocio.getStatusCode());
        assertEquals("Repetido", negocio.getBody().get("mensaje"));

        ResponseEntity<Map<String, String>> inesperado = manejador.inesperado(new IllegalStateException("falla"));
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, inesperado.getStatusCode());
        assertEquals("Error interno del servidor: falla", inesperado.getBody().get("mensaje"));
    }
}
