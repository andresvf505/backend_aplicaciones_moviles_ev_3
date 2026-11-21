package cl.duoc.huerto.huerto.service;

import cl.duoc.huerto.huerto.model.EstadoLectura;
import cl.duoc.huerto.huerto.model.LecturaSensor;
import cl.duoc.huerto.huerto.model.Sector;
import cl.duoc.huerto.huerto.repository.LecturaSensorRepository;
import cl.duoc.huerto.huerto.repository.SectorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Lecturas SIMULADAS de sensores. No hay hardware conectado: los valores se
 * generan al azar dentro de rangos realistas y quedan marcados como simulacion.
 */
@Service
public class LecturaService {

    public static final String ORIGEN = "Simulación académica";

    @Autowired
    private LecturaSensorRepository lecturaRepository;

    @Autowired
    private SectorRepository sectorRepository;

    public List<LecturaSensor> listar() {
        return lecturaRepository.findAllByOrderByFechaLecturaDesc();
    }

    /** Genera una lectura para cada sector que todavia no tiene ninguna. */
    public List<LecturaSensor> simularFaltantes() {
        List<LecturaSensor> nuevas = new ArrayList<>();
        for (Sector sector : sectorRepository.findAll()) {
            if (!lecturaRepository.existsBySectorId(sector.getId())) {
                nuevas.add(lecturaRepository.save(lecturaSimulada(sector.getId())));
            }
        }
        return nuevas;
    }

    public LecturaSensor simularPara(Long sectorId) {
        return lecturaRepository.save(lecturaSimulada(sectorId));
    }

    private LecturaSensor lecturaSimulada(Long sectorId) {
        ThreadLocalRandom azar = ThreadLocalRandom.current();
        int humedadSuelo = azar.nextInt(20, 80);
        LecturaSensor lectura = new LecturaSensor();
        lectura.setSectorId(sectorId);
        lectura.setFechaLectura(System.currentTimeMillis());
        lectura.setHumedadSuelo(humedadSuelo);
        lectura.setTemperatura(azar.nextInt(150, 290) / 10.0);
        lectura.setHumedadAmbiente(azar.nextInt(40, 85));
        lectura.setOrigen(ORIGEN);
        lectura.setEstado(humedadSuelo < 30 ? EstadoLectura.BAJO
                : humedadSuelo > 65 ? EstadoLectura.ALTO : EstadoLectura.NORMAL);
        return lectura;
    }
}
