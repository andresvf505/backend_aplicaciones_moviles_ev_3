package cl.duoc.huerto.huerto.repository;

import cl.duoc.huerto.huerto.model.Cultivo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CultivoRepository extends JpaRepository<Cultivo, Long> {

    List<Cultivo> findAllByOrderByNombreAsc();

    List<Cultivo> findBySectorId(Long sectorId);

    void deleteBySectorId(Long sectorId);
}
