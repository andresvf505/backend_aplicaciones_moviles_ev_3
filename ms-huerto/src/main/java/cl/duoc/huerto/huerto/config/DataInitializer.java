package cl.duoc.huerto.huerto.config;

import cl.duoc.huerto.huerto.model.Cultivo;
import cl.duoc.huerto.huerto.model.EstadoCultivo;
import cl.duoc.huerto.huerto.model.EstadoLectura;
import cl.duoc.huerto.huerto.model.EstadoSector;
import cl.duoc.huerto.huerto.model.LecturaSensor;
import cl.duoc.huerto.huerto.model.Sector;
import cl.duoc.huerto.huerto.repository.CultivoRepository;
import cl.duoc.huerto.huerto.repository.LecturaSensorRepository;
import cl.duoc.huerto.huerto.repository.SectorRepository;
import cl.duoc.huerto.huerto.service.LecturaService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;
import java.time.ZoneId;

/**
 * Sectores, cultivos y lecturas ficticias (los mismos que antes cargaba la
 * app en Room). Las fechas se calculan desde hoy. Responsables: usuario 1
 * (profe.ana) y usuario 2 (taller.luis) de ms-usuarios.
 */
@Configuration
public class DataInitializer {

    private static final ZoneId ZONA = ZoneId.of("America/Santiago");

    @Bean
    CommandLineRunner cargarHuertoDemo(SectorRepository sectores, CultivoRepository cultivos,
                                       LecturaSensorRepository lecturas) {
        return args -> {
            if (sectores.count() > 0) {
                return;
            }
            Long norte = sectores.save(sector("S-01", "Huerto Norte", "Cuatro camas de cultivo junto al patio techado",
                    "Patio norte, costado del comedor", EstadoSector.EN_USO, 1L, -60, -5)).getId();
            Long invernadero = sectores.save(sector("S-02", "Invernadero", "Estructura cubierta para almácigos y plantas delicadas",
                    "Costado del gimnasio", EstadoSector.EN_USO, 2L, -55, -2)).getId();
            Long compostera = sectores.save(sector("S-03", "Compostera", "Zona de compostaje y preparación de tierra",
                    "Fondo del patio trasero", EstadoSector.EN_MANTENIMIENTO, 2L, -40, -10)).getId();
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
        };
    }

    /** Milisegundos de hoy a medianoche, mas (o menos) la cantidad de dias indicada. */
    private static long dia(int desdeHoy) {
        return LocalDate.now(ZONA).plusDays(desdeHoy).atStartOfDay(ZONA).toInstant().toEpochMilli();
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
