package cl.duoc.huerto.config;

import cl.duoc.huerto.model.Cargo;
import cl.duoc.huerto.model.Cultivo;
import cl.duoc.huerto.model.EstadoCultivo;
import cl.duoc.huerto.model.EstadoLectura;
import cl.duoc.huerto.model.EstadoSector;
import cl.duoc.huerto.model.LecturaSensor;
import cl.duoc.huerto.model.Sector;
import cl.duoc.huerto.model.Usuario;
import cl.duoc.huerto.repository.CultivoRepository;
import cl.duoc.huerto.repository.LecturaSensorRepository;
import cl.duoc.huerto.repository.SectorRepository;
import cl.duoc.huerto.repository.UsuarioRepository;
import cl.duoc.huerto.service.LecturaService;
import cl.duoc.huerto.util.Claves;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

/**
 * Datos ficticios de prueba. Se cargan solo si las tablas estan vacias:
 * 1. Cuentas (contrasena huerto123).
 * 2. Sectores, cultivos y lecturas simuladas. Las fechas se calculan desde hoy.
 * Los usuarios se crean primero porque profe.ana y taller.luis son los
 * responsables de los sectores.
 */
@Configuration
public class DataInitializer {

    public static final String CLAVE_DEMO = "huerto123";

    private static final ZoneId ZONA = ZoneId.of("America/Santiago");

    @Bean
    CommandLineRunner cargarDatosDemo(UsuarioRepository usuarios, SectorRepository sectores,
                                      CultivoRepository cultivos, LecturaSensorRepository lecturas) {
        return args -> {
            cargarUsuarios(usuarios);
            cargarHuerto(usuarios, sectores, cultivos, lecturas);
        };
    }

    void cargarUsuarios(UsuarioRepository usuarios) {
        if (usuarios.count() > 0) {
            return;
        }
        usuarios.saveAll(List.of(
                usuario("profe.ana", "ana.rojas@escuela.cl", "Ana Rojas", Cargo.DOCENTE, "Taller de Huerto"),
                usuario("taller.luis", "luis.pinto@escuela.cl", "Luis Pinto", Cargo.TALLERISTA, "Taller de Huerto"),
                usuario("est.camila", "camila.soto@escuela.cl", "Camila Soto", Cargo.ESTUDIANTE, "Taller A"),
                usuario("est.benjamin", "benjamin.diaz@escuela.cl", "Benjamín Díaz", Cargo.ESTUDIANTE, "Taller A"),
                usuario("est.martina", "martina.rivas@escuela.cl", "Martina Rivas", Cargo.ESTUDIANTE, "Taller B")
        ));
    }

    void cargarHuerto(UsuarioRepository usuarios, SectorRepository sectores,
                      CultivoRepository cultivos, LecturaSensorRepository lecturas) {
        if (sectores.count() > 0) {
            return;
        }
        Long ana = idDe(usuarios, "profe.ana");
        Long luis = idDe(usuarios, "taller.luis");

        Long norte = sectores.save(sector("S-01", "Huerto Norte", "Cuatro camas de cultivo junto al patio techado",
                "Patio norte, costado del comedor", EstadoSector.EN_USO, ana, -60, -5)).getId();
        Long invernadero = sectores.save(sector("S-02", "Invernadero", "Estructura cubierta para almácigos y plantas delicadas",
                "Costado del gimnasio", EstadoSector.EN_USO, luis, -55, -2)).getId();
        Long compostera = sectores.save(sector("S-03", "Compostera", "Zona de compostaje y preparación de tierra",
                "Fondo del patio trasero", EstadoSector.EN_MANTENIMIENTO, luis, -40, -10)).getId();
        Long sur = sectores.save(sector("S-04", "Huerto Sur", "Cama nueva disponible para la siembra de primavera",
                "Patio sur, junto a la reja", EstadoSector.DISPONIBLE, null, -15, -15)).getId();

        cultivos.save(cultivo("Lechuga costina", "Almácigo trasplantado en la cama 1 del sector norte", norte,
                -35, -20, 10, 2, EstadoCultivo.EN_CRECIMIENTO, "Crecimiento parejo, hojas sanas"));
        cultivos.save(cultivo("Tomate cherry", "Plantas en maceteros altos dentro del invernadero", invernadero,
                -50, -30, 7, 3, EstadoCultivo.LISTO_PARA_COSECHAR, "Primeros frutos maduros"));
        cultivos.save(cultivo("Acelga", "Siembra directa en la cama 2 del sector norte", norte,
                -18, null, 25, 2, EstadoCultivo.SEMBRADO, "Germinación despareja en el borde"));
        cultivos.save(cultivo("Cilantro", "Almácigo en bandeja dentro del invernadero", invernadero,
                -12, null, 30, 1, EstadoCultivo.EN_CRECIMIENTO, "Requiere riego diario"));
        cultivos.save(cultivo("Zanahoria", "Siembra planificada para la cama del sector sur", sur,
                5, null, 95, 3, EstadoCultivo.PLANIFICADO, "Pendiente preparar la tierra"));
        cultivos.save(cultivo("Albahaca", "Macetero de aromáticas junto a los tomates", invernadero,
                -70, -45, -5, 2, EstadoCultivo.COSECHADO, "Primera cosecha entregada al taller de cocina"));

        lecturas.save(lectura(norte, -1, 46, 18.5, 55, EstadoLectura.NORMAL));
        lecturas.save(lectura(norte, 0, 28, 21.0, 48, EstadoLectura.BAJO));
        lecturas.save(lectura(invernadero, 0, 62, 26.4, 70, EstadoLectura.ALTO));
        lecturas.save(lectura(invernadero, -2, 54, 24.1, 63, EstadoLectura.NORMAL));
        lecturas.save(lectura(compostera, -1, 71, 19.8, 66, EstadoLectura.PENDIENTE_REVISION));
        lecturas.save(lectura(sur, 0, 35, 20.2, 52, EstadoLectura.NORMAL));
    }

    private static Long idDe(UsuarioRepository usuarios, String nombreUsuario) {
        return usuarios.findByNombreUsuario(nombreUsuario).map(Usuario::getId).orElse(null);
    }

    /** Milisegundos de hoy a medianoche, mas (o menos) la cantidad de dias indicada. */
    static long dia(int desdeHoy) {
        return LocalDate.now(ZONA).plusDays(desdeHoy).atStartOfDay(ZONA).toInstant().toEpochMilli();
    }

    private static Usuario usuario(String nombreUsuario, String correo, String nombre, Cargo cargo, String taller) {
        Usuario u = new Usuario();
        u.setNombreUsuario(nombreUsuario);
        u.setCorreo(correo);
        u.setClaveHash(Claves.hash(CLAVE_DEMO));
        u.setNombre(nombre);
        u.setCargo(cargo);
        u.setRol(cargo.getRol());
        u.setTaller(taller);
        u.setActivo(true);
        return u;
    }

    private static Sector sector(String codigo, String nombre, String descripcion, String ubicacion,
                                 EstadoSector estado, Long responsableId, int creado, int actualizado) {
        Sector s = new Sector();
        s.setCodigo(codigo);
        s.setNombre(nombre);
        s.setDescripcion(descripcion);
        s.setUbicacion(ubicacion);
        s.setEstado(estado);
        s.setResponsableId(responsableId);
        s.setFechaCreacion(dia(creado));
        s.setFechaActualizacion(dia(actualizado));
        return s;
    }

    private static Cultivo cultivo(String nombre, String descripcion, Long sectorId, int siembra, Integer trasplante,
                                   int cosecha, int riego, EstadoCultivo estado, String observaciones) {
        Cultivo c = new Cultivo();
        c.setNombre(nombre);
        c.setDescripcion(descripcion);
        c.setSectorId(sectorId);
        c.setFechaSiembra(dia(siembra));
        c.setFechaTrasplante(trasplante == null ? null : dia(trasplante));
        c.setFechaCosechaEstimada(dia(cosecha));
        c.setFrecuenciaRiegoDias(riego);
        c.setEstado(estado);
        c.setObservaciones(observaciones);
        c.setRutaFoto(null);
        return c;
    }

    private static LecturaSensor lectura(Long sectorId, int dia, int humedadSuelo, double temperatura,
                                         int humedadAmbiente, EstadoLectura estado) {
        LecturaSensor l = new LecturaSensor();
        l.setSectorId(sectorId);
        l.setFechaLectura(dia(dia));
        l.setHumedadSuelo(humedadSuelo);
        l.setTemperatura(temperatura);
        l.setHumedadAmbiente(humedadAmbiente);
        l.setOrigen(LecturaService.ORIGEN);
        l.setEstado(estado);
        return l;
    }
}
