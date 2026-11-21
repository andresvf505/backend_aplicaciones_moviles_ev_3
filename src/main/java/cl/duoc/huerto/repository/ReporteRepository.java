package cl.duoc.huerto.repository;

import cl.duoc.huerto.model.ReporteProblema;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReporteRepository extends JpaRepository<ReporteProblema, Long> {

    List<ReporteProblema> findAllByOrderByFechaReporteDesc();

    void deleteBySectorId(Long sectorId);
}
