package nilton.acelera.demo.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import nilton.acelera.demo.model.Tema;

public interface TemaRepository extends JpaRepository<Tema, Long> {
    List<Tema> findAllByDescricaoContainingIgnoreCase(String descricao);
}
