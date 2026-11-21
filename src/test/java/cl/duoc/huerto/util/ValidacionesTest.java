package cl.duoc.huerto.util;

import cl.duoc.huerto.exception.ReglaNegocioException;
import cl.duoc.huerto.model.EstadoSector;
import cl.duoc.huerto.service.ValidadorUsuario;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static org.junit.jupiter.api.Assertions.*;

/** Validaciones comunes, hash de claves y reglas de las cuentas. */
class ValidacionesTest {

    @Test
    void textoLimpiaEspacios() {
        assertEquals("Hola mundo", Validar.texto("  Hola mundo ", "El campo", 3, 20));
    }

    @Test
    void textoVacioEsObligatorio() {
        ReglaNegocioException e = assertThrows(ReglaNegocioException.class,
                () -> Validar.texto(null, "El nombre", 3, 20));
        assertEquals("El nombre es obligatorio", e.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, e.getEstado());
    }

    @Test
    void textoFueraDeRango() {
        assertThrows(ReglaNegocioException.class, () -> Validar.texto("ab", "El nombre", 3, 20));
        assertThrows(ReglaNegocioException.class, () -> Validar.texto("abcdef", "El nombre", 3, 5));
    }

    @Test
    void opcionalDevuelveVacioOTextoLimpio() {
        assertEquals("", Validar.opcional(null, 10));
        assertEquals("nota", Validar.opcional(" nota ", 10));
        assertThrows(ReglaNegocioException.class, () -> Validar.opcional("demasiado largo", 5));
    }

    @Test
    void requerido() {
        assertEquals(5L, Validar.requerido(5L, "El id"));
        assertThrows(ReglaNegocioException.class, () -> Validar.requerido(null, "El id"));
    }

    @Test
    void enumeracionAceptaMinusculas() {
        assertEquals(EstadoSector.EN_USO, Validar.enumeracion(EstadoSector.class, " en_uso ", "El estado"));
    }

    @Test
    void enumeracionVaciaOInvalida() {
        assertThrows(ReglaNegocioException.class, () -> Validar.enumeracion(EstadoSector.class, null, "El estado"));
        assertThrows(ReglaNegocioException.class, () -> Validar.enumeracion(EstadoSector.class, "  ", "El estado"));
        ReglaNegocioException e = assertThrows(ReglaNegocioException.class,
                () -> Validar.enumeracion(EstadoSector.class, "ROTO", "El estado"));
        assertEquals("El estado no es valido: ROTO", e.getMessage());
    }

    @Test
    void hashDeClaveEsSha256EnHexadecimal() {
        String hash = Claves.hash("huerto123");
        assertEquals(64, hash.length());
        assertTrue(Claves.coincide("huerto123", hash));
        assertFalse(Claves.coincide("otra", hash));
        assertFalse(Claves.coincide(null, hash));
    }

    @Test
    void fabricasDeErroresUsanSuCodigoHttp() {
        assertEquals(HttpStatus.NOT_FOUND, ReglaNegocioException.noEncontrado("x").getEstado());
        assertEquals(HttpStatus.BAD_REQUEST, ReglaNegocioException.invalido("x").getEstado());
        assertEquals(HttpStatus.CONFLICT, ReglaNegocioException.conflicto("x").getEstado());
        assertEquals(HttpStatus.FORBIDDEN, ReglaNegocioException.prohibido("x").getEstado());
    }

    @Test
    void nombreDeUsuarioValido() {
        assertEquals("Ana Rojas", ValidadorUsuario.nombre("  Ana   Rojas "));
        assertThrows(ReglaNegocioException.class, () -> ValidadorUsuario.nombre("Ana"));
        assertThrows(ReglaNegocioException.class, () -> ValidadorUsuario.nombre("Ana Rojas 2"));
        assertThrows(ReglaNegocioException.class, () -> ValidadorUsuario.nombre(null));
    }

    @Test
    void correo() {
        assertEquals("ana@escuela.cl", ValidadorUsuario.correo(" Ana@Escuela.CL "));
        assertThrows(ReglaNegocioException.class, () -> ValidadorUsuario.correo("ana@escuela"));
        assertThrows(ReglaNegocioException.class, () -> ValidadorUsuario.correo(null));
    }

    @Test
    void nombreUsuario() {
        assertEquals("est.camila", ValidadorUsuario.nombreUsuario("Est.Camila"));
        assertThrows(ReglaNegocioException.class, () -> ValidadorUsuario.nombreUsuario("ab"));
        assertThrows(ReglaNegocioException.class, () -> ValidadorUsuario.nombreUsuario("con espacio"));
        assertThrows(ReglaNegocioException.class, () -> ValidadorUsuario.nombreUsuario(null));
    }

    @Test
    void clave() {
        assertEquals("clave1234", ValidadorUsuario.clave("clave1234"));
        assertThrows(ReglaNegocioException.class, () -> ValidadorUsuario.clave(null));
        assertThrows(ReglaNegocioException.class, () -> ValidadorUsuario.clave("corta1"));
        assertThrows(ReglaNegocioException.class, () -> ValidadorUsuario.clave("sololetras"));
        assertThrows(ReglaNegocioException.class, () -> ValidadorUsuario.clave("12345678"));
        assertThrows(ReglaNegocioException.class, () -> ValidadorUsuario.clave("con espacio 1"));
    }

    @Test
    void taller() {
        assertEquals("Taller A", ValidadorUsuario.taller(" Taller A "));
        assertThrows(ReglaNegocioException.class, () -> ValidadorUsuario.taller("A"));
        assertThrows(ReglaNegocioException.class, () -> ValidadorUsuario.taller(null));
    }
}
