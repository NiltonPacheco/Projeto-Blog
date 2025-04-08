package nilton.acelera.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import nilton.acelera.demo.model.Postagem;

public interface PostagemRepository extends JpaRepository<Postagem, Long> {
    List<Postagem> findAllByTituloContainingIgnoreCase(String titulo);

    List<Postagem> findByTemaId(Long temaId);

    List<Postagem> findByUsuarioId(Long usuarioId);

    List<Postagem> findByUsuarioIdAndTemaId(Long usuarioId, Long temaId);
}