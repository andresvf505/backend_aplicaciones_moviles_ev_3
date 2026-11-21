package cl.duoc.huerto.huerto.service;

import cl.duoc.huerto.huerto.dto.SectorRequest;
import cl.duoc.huerto.huerto.exception.ReglaNegocioException;
import cl.duoc.huerto.huerto.model.EstadoSector;
import cl.duoc.huerto.huerto.model.Sector;
import cl.duoc.huerto.huerto.repository.CultivoRepository;
import cl.duoc.huerto.huerto.repository.LecturaSensorRepository;
import cl.duoc.huerto.huerto.repository.SectorRepository;
import cl.duoc.huerto.huerto.util.Validar;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.regex.Pattern;

@Service
public class SectorService {

    private static final Pattern CODIGO = Pattern.compile("^[A-Z0-9-]{3,10}$");

    @Autowired
    private SectorRepository sectorRepository;

    @Autowired
    private CultivoRepository cultivoRepository;

    @Autowired
    private LecturaSensorRepository lecturaRepository;

    @Autowired
    private LecturaService lecturaService;

    // 1. Listar todos
    public List<Sector> listar() {
        return sectorRepository.findAllByOrderByNombreAsc();
    }

    // 2. Buscar por ID
    public Sector buscarPorId(Long id) {
        return sectorRepository.findById(id)
                .orElseThrow(() -> ReglaNegocioException.noEncontrado("El sector " + id + " no existe"));
    }

    // 3. Crear: ademas se genera su primera lectura simulada de sensor
    @Transactional
    public Sector crear(SectorRequest solicitud) {
        String codigo = codigo(solicitud.codigo());
        if (sectorRepository.existsByCodigoIgnoreCase(codigo)) {
            throw ReglaNegocioException.conflicto("Ya existe otro sector con el código " + codigo);
        }
        long ahora = System.currentTimeMillis();
        Sector sector = new Sector();
        sector.setFechaCreacion(ahora);
        copiar(solicitud, codigo, sector, ahora);
        Sector guardado = sectorRepository.save(sector);
        lecturaService.simularPara(guardado.getId());
        return guardado;
    }

    // 4. Actualizar
    public Sector actualizar(Long id, SectorRequest solicitud) {
        Sector sector = buscarPorId(id);
        String codigo = codigo(solicitud.codigo());
        if (sectorRepository.existsByCodigoIgnoreCaseAndIdNot(codigo, id)) {
            throw ReglaNegocioException.conflicto("Ya existe otro sector con el código " + codigo);
        }
        copiar(solicitud, codigo, sector, System.currentTimeMillis());
        return sectorRepository.save(sector);
    }

    // 5. Borrar: tambien se borran sus cultivos y lecturas
    @Transactional
    public void borrar(Long id) {
        Sector sector = buscarPorId(id);
        cultivoRepository.deleteBySectorId(id);
        lecturaRepository.deleteBySectorId(id);
        sectorRepository.delete(sector);
    }

    private void copiar(SectorRequest solicitud, String codigo, Sector sector, long ahora) {
        sector.setCodigo(codigo);
        sector.setNombre(Validar.texto(solicitud.nombre(), "El nombre", 3, 60));
        sector.setDescripcion(Validar.texto(solicitud.descripcion(), "La descripción", 5, 200));
        sector.setUbicacion(Validar.texto(solicitud.ubicacion(), "La ubicación", 3, 80));
        sector.setEstado(Validar.enumeracion(EstadoSector.class, solicitud.estado(), "El estado"));
        sector.setResponsableId(solicitud.responsableId());
        sector.setFechaActualizacion(ahora);
    }

    private String codigo(String codigo) {
        String limpio = codigo == null ? "" : codigo.trim().toUpperCase();
        if (!CODIGO.matcher(limpio).matches()) {
            throw ReglaNegocioException.invalido("El código debe tener entre 3 y 10 caracteres: letras, números o guion");
        }
        return limpio;
    }
}
