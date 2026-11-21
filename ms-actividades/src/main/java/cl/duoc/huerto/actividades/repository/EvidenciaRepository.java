package cl.duoc.huerto.actividades.repository;

import cl.duoc.huerto.actividades.model.Evidencia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EvidenciaRepository extends JpaRepository<Evidencia, Long> {

    List<Evidencia> findAllByOrderByFechaRegistroDesc();

    void deleteByTareaId(Long tareaId);
}
