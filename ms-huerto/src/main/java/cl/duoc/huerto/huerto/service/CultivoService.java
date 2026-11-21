package cl.duoc.huerto.huerto.service;

import cl.duoc.huerto.huerto.dto.CultivoRequest;
import cl.duoc.huerto.huerto.dto.EstadoRequest;
import cl.duoc.huerto.huerto.exception.ReglaNegocioException;
import cl.duoc.huerto.huerto.model.Cultivo;
import cl.duoc.huerto.huerto.model.EstadoCultivo;
import cl.duoc.huerto.huerto.repository.CultivoRepository;
import cl.duoc.huerto.huerto.repository.SectorRepository;
import cl.duoc.huerto.huerto.util.Validar;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CultivoService {

    @Autowired
    private CultivoRepository cultivoRepository;

    @Autowired
    private SectorRepository sectorRepository;

    // 1. Listar todos (opcionalmente solo los de un sector)
    public List<Cultivo> listar(Long sectorId) {
        return sectorId == null ? cultivoRepository.findAllByOrderByNombreAsc()
                : cultivoRepository.findBySectorId(sectorId);
    }

    // 2. Buscar por ID
    public Cultivo buscarPorId(Long id) {
        return cultivoRepository.findById(id)
                .orElseThrow(() -> ReglaNegocioException.noEncontrado("El cultivo " + id + " no existe"));
    }

    // 3. Crear
    public Cultivo crear(CultivoRequest solicitud) {
        Cultivo cultivo = new Cultivo();
        copiar(solicitud, cultivo);
        return cultivoRepository.save(cultivo);
    }

    // 4. Actualizar
    public Cultivo actualizar(Long id, CultivoRequest solicitud) {
        Cultivo cultivo = buscarPorId(id);
        copiar(solicitud, cultivo);
        return cultivoRepository.save(cultivo);
    }

    // 5. Cambiar solo el estado de desarrollo
    public Cultivo cambiarEstado(Long id, EstadoRequest solicitud) {
        Cultivo cultivo = buscarPorId(id);
        cultivo.setEstado(Validar.enumeracion(EstadoCultivo.class, solicitud.estado(), "El estado"));
        return cultivoRepository.save(cultivo);
    }

    // 6. Borrar
    public void borrar(Long id) {
        cultivoRepository.delete(buscarPorId(id));
    }

    private void copiar(CultivoRequest solicitud, Cultivo cultivo) {
        Long sectorId = Validar.requerido(solicitud.sectorId(), "El sector");
        if (!sectorRepository.existsById(sectorId)) {
            throw ReglaNegocioException.invalido("El sector seleccionado no existe");
        }
        Long siembra = Validar.requerido(solicitud.fechaSiembra(), "La fecha de siembra");
        Long cosecha = Validar.requerido(solicitud.fechaCosechaEstimada(), "La cosecha estimada");
        if (cosecha < siembra) {
            throw ReglaNegocioException.invalido("La cosecha estimada no puede ser anterior a la siembra");
        }
        Long trasplante = solicitud.fechaTrasplante();
        if (trasplante != null && (trasplante < siembra || trasplante > cosecha)) {
            throw ReglaNegocioException.invalido("El trasplante debe quedar entre la siembra y la cosecha");
        }
        Integer riego = Validar.requerido(solicitud.frecuenciaRiegoDias(), "La frecuencia de riego");
        if (riego <= 0 || riego > 60) {
            throw ReglaNegocioException.invalido("La frecuencia de riego debe estar entre 1 y 60 días");
        }
        cultivo.setNombre(Validar.texto(solicitud.nombre(), "El nombre del cultivo", 3, 60));
        cultivo.setDescripcion(Validar.texto(solicitud.descripcion(), "La descripción", 5, 200));
        cultivo.setSectorId(sectorId);
        cultivo.setFechaSiembra(siembra);
        cultivo.setFechaTrasplante(trasplante);
        cultivo.setFechaCosechaEstimada(cosecha);
        cultivo.setFrecuenciaRiegoDias(riego);
        cultivo.setEstado(Validar.enumeracion(EstadoCultivo.class, solicitud.estado(), "El estado"));
        cultivo.setObservaciones(Validar.opcional(solicitud.observaciones(), 300));
        cultivo.setRutaFoto(solicitud.rutaFoto());
    }
}
