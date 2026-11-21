package cl.duoc.huerto.actividades.repository;

import cl.duoc.huerto.actividades.model.Tarea;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TareaRepository extends JpaRepository<Tarea, Long> {

    List<Tarea> findAllByOrderByFechaProgramadaAscIdAsc();

    List<Tarea> findByResponsableIdOrderByFechaProgramadaAsc(Long responsableId);
}
