package cl.duoc.huerto.repository;

import cl.duoc.huerto.model.Tarea;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TareaRepository extends JpaRepository<Tarea, Long> {

    List<Tarea> findAllByOrderByFechaProgramadaAscIdAsc();

    List<Tarea> findByResponsableIdOrderByFechaProgramadaAsc(Long responsableId);

    boolean existsBySectorId(Long sectorId);

    boolean existsByCultivoId(Long cultivoId);
}
