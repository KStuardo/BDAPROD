package rac.devs.backend.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import rac.devs.backend.model.Boleta;

@Repository
public interface BoletaRepository extends JpaRepository<Boleta, Long> {
}
