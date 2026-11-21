package cl.duoc.huerto.huerto.repository;

import cl.duoc.huerto.huerto.model.LecturaSensor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LecturaSensorRepository extends JpaRepository<LecturaSensor, Long> {

    List<LecturaSensor> findAllByOrderByFechaLecturaDesc();

    boolean existsBySectorId(Long sectorId);

    void deleteBySectorId(Long sectorId);
}
