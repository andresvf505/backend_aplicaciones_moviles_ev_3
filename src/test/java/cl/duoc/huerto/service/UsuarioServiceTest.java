package cl.duoc.huerto.service;

import cl.duoc.huerto.Datos;
import cl.duoc.huerto.dto.LoginRequest;
import cl.duoc.huerto.dto.PerfilRequest;
import cl.duoc.huerto.dto.RegistroRequest;
import cl.duoc.huerto.dto.UsuarioResponse;
import cl.duoc.huerto.exception.ReglaNegocioException;
import cl.duoc.huerto.model.Usuario;
import cl.duoc.huerto.repository.UsuarioRepository;
import cl.duoc.huerto.util.Claves;
import org.junit.jupiter.api.BeforeEach;
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
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository repositorio;

    @InjectMocks
    private UsuarioService servicio;

    @BeforeEach
    void guardarDevuelveLoMismo() {
        when(repositorio.save(any(Usuario.class))).thenAnswer(inv -> inv.getArgument(0));
    }

    private RegistroRequest registro(String cargo, String codigo) {
        return new RegistroRequest("Camila Soto", "Camila@Escuela.cl", "Est.Camila", "clave1234",
                cargo, "Taller A", codigo);
    }

    @Test
    void listarConvierteAUsuarioResponseSinClave() {
        when(repositorio.findAll()).thenReturn(List.of(Datos.docente(), Datos.estudiante()));

        List<UsuarioResponse> lista = servicio.listar();

        assertEquals(2, lista.size());
        assertEquals("profe.ana", lista.get(0).nombreUsuario());
        assertEquals("ADMINISTRADOR", lista.get(0).rol());
        assertEquals("ESTUDIANTE", lista.get(1).cargo());
    }

    @Test
    void buscarPorIdInexistenteResponde404() {
        when(repositorio.findById(9L)).thenReturn(Optional.empty());

        ReglaNegocioException e = assertThrows(ReglaNegocioException.class, () -> servicio.buscarPorId(9L));

        assertEquals(HttpStatus.NOT_FOUND, e.getEstado());
        assertEquals("El usuario 9 no existe", e.getMessage());
    }

    @Test
    void buscarPorIdExistente() {
        when(repositorio.findById(1L)).thenReturn(Optional.of(Datos.docente()));

        assertEquals("Nombre Apellido", servicio.buscarPorId(1L).nombre());
    }

    @Test
    void registrarEstudianteNormalizaYGuardaHash() {
        when(repositorio.existsByNombreUsuario(anyString())).thenReturn(false);
        when(repositorio.existsByCorreoIgnoreCase(anyString())).thenReturn(false);

        UsuarioResponse respuesta = servicio.registrar(registro("estudiante", null));

        assertEquals("est.camila", respuesta.nombreUsuario());
        assertEquals("camila@escuela.cl", respuesta.correo());
        assertEquals("ESTUDIANTE", respuesta.rol());
        assertTrue(respuesta.activo());
        verify(repositorio).save(argThat(u -> u.getClaveHash().equals(Claves.hash("clave1234"))));
    }

    @Test
    void registrarDocenteSinCodigoEsProhibido() {
        ReglaNegocioException e = assertThrows(ReglaNegocioException.class,
                () -> servicio.registrar(registro("DOCENTE", null)));

        assertEquals(HttpStatus.FORBIDDEN, e.getEstado());
        verify(repositorio, never()).save(any());
    }

    @Test
    void registrarDocenteConCodigoCorrecto() {
        UsuarioResponse respuesta = servicio.registrar(registro("DOCENTE", " HUERTO2026 "));

        assertEquals("ADMINISTRADOR", respuesta.rol());
        assertEquals("DOCENTE", respuesta.cargo());
    }

    @Test
    void registrarConUsuarioRepetidoEsConflicto() {
        when(repositorio.existsByNombreUsuario("est.camila")).thenReturn(true);

        ReglaNegocioException e = assertThrows(ReglaNegocioException.class,
                () -> servicio.registrar(registro("ESTUDIANTE", null)));

        assertEquals(HttpStatus.CONFLICT, e.getEstado());
        assertEquals("Ese nombre de usuario ya está en uso", e.getMessage());
    }

    @Test
    void registrarConCorreoRepetidoEsConflicto() {
        when(repositorio.existsByCorreoIgnoreCase("camila@escuela.cl")).thenReturn(true);

        ReglaNegocioException e = assertThrows(ReglaNegocioException.class,
                () -> servicio.registrar(registro("ESTUDIANTE", null)));

        assertEquals("Ese correo ya está registrado", e.getMessage());
    }

    @Test
    void registrarConCargoInvalido() {
        ReglaNegocioException e = assertThrows(ReglaNegocioException.class,
                () -> servicio.registrar(registro("DIRECTOR", null)));

        assertEquals(HttpStatus.BAD_REQUEST, e.getEstado());
    }

    @Test
    void loginConUsuarioYClaveCorrecta() {
        when(repositorio.findByNombreUsuario("profe.ana")).thenReturn(Optional.of(Datos.docente()));

        UsuarioResponse respuesta = servicio.login(new LoginRequest("  Profe.Ana ", Datos.CLAVE));

        assertEquals(1L, respuesta.id());
    }

    @Test
    void loginConCorreo() {
        when(repositorio.findByNombreUsuario(anyString())).thenReturn(Optional.empty());
        when(repositorio.findByCorreoIgnoreCase("profe.ana@escuela.cl")).thenReturn(Optional.of(Datos.docente()));

        UsuarioResponse respuesta = servicio.login(new LoginRequest("profe.ana@escuela.cl", Datos.CLAVE));

        assertEquals("profe.ana", respuesta.nombreUsuario());
    }

    @Test
    void loginConClaveIncorrectaResponde401() {
        when(repositorio.findByNombreUsuario("profe.ana")).thenReturn(Optional.of(Datos.docente()));

        ReglaNegocioException e = assertThrows(ReglaNegocioException.class,
                () -> servicio.login(new LoginRequest("profe.ana", "otra1234")));

        assertEquals(HttpStatus.UNAUTHORIZED, e.getEstado());
    }

    @Test
    void loginDeCuentaInactivaFalla() {
        Usuario inactivo = Datos.docente();
        inactivo.setActivo(false);
        when(repositorio.findByNombreUsuario("profe.ana")).thenReturn(Optional.of(inactivo));

        assertThrows(ReglaNegocioException.class, () -> servicio.login(new LoginRequest("profe.ana", Datos.CLAVE)));
    }

    @Test
    void loginSinIdentificador() {
        when(repositorio.findByNombreUsuario("")).thenReturn(Optional.empty());
        when(repositorio.findByCorreoIgnoreCase("")).thenReturn(Optional.empty());

        assertThrows(ReglaNegocioException.class, () -> servicio.login(new LoginRequest(null, null)));
    }

    @Test
    void actualizarPerfilSinCambiarClave() {
        Usuario ana = Datos.docente();
        String hashAntes = ana.getClaveHash();
        when(repositorio.findById(1L)).thenReturn(Optional.of(ana));

        UsuarioResponse respuesta = servicio.actualizarPerfil(1L,
                new PerfilRequest("Ana  Rojas", "ana.nueva@escuela.cl", "Taller B", null, null));

        assertEquals("Ana Rojas", respuesta.nombre());
        assertEquals("ana.nueva@escuela.cl", respuesta.correo());
        assertEquals("Taller B", respuesta.taller());
        assertEquals(hashAntes, ana.getClaveHash());
    }

    @Test
    void actualizarPerfilCambiandoClave() {
        Usuario ana = Datos.docente();
        when(repositorio.findById(1L)).thenReturn(Optional.of(ana));

        servicio.actualizarPerfil(1L,
                new PerfilRequest("Ana Rojas", "ana@escuela.cl", "Taller", Datos.CLAVE, "nueva1234"));

        assertEquals(Claves.hash("nueva1234"), ana.getClaveHash());
    }

    @Test
    void actualizarPerfilConClaveActualIncorrecta() {
        when(repositorio.findById(1L)).thenReturn(Optional.of(Datos.docente()));

        ReglaNegocioException e = assertThrows(ReglaNegocioException.class, () -> servicio.actualizarPerfil(1L,
                new PerfilRequest("Ana Rojas", "ana@escuela.cl", "Taller", "mala1234", "nueva1234")));

        assertEquals("La contraseña actual no es correcta", e.getMessage());
    }

    @Test
    void actualizarPerfilConCorreoDeOtraCuenta() {
        when(repositorio.findById(1L)).thenReturn(Optional.of(Datos.docente()));
        when(repositorio.existsByCorreoIgnoreCaseAndIdNot("otro@escuela.cl", 1L)).thenReturn(true);

        ReglaNegocioException e = assertThrows(ReglaNegocioException.class, () -> servicio.actualizarPerfil(1L,
                new PerfilRequest("Ana Rojas", "otro@escuela.cl", "Taller", null, "")));

        assertEquals(HttpStatus.CONFLICT, e.getEstado());
    }

    @Test
    void borrarEliminaElUsuario() {
        Usuario ana = Datos.docente();
        when(repositorio.findById(1L)).thenReturn(Optional.of(ana));

        servicio.borrar(1L);

        verify(repositorio).delete(ana);
    }
}
