package cl.duoc.huerto.actividades.repository;

import cl.duoc.huerto.actividades.model.Validacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ValidacionRepository extends JpaRepository<Validacion, Long> {

    List<Validacion> findAllByOrderByFechaValidacionDesc();

    void deleteByTareaId(Long tareaId);
}
