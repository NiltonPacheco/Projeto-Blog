package nilton.acelera.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import nilton.acelera.demo.model.Postagem;

public interface PostagemRepository extends JpaRepository<Postagem, Long> {

    // Busca por título (case insensitive)
    List<Postagem> findAllByTituloContainingIgnoreCase(String titulo);

    // Busca por descrição do tema (case insensitive)
    List<Postagem> findAllByTemaDescricaoContainingIgnoreCase(String descricao);

    // Busca por nome do usuário (case insensitive)
    List<Postagem> findAllByUsuarioNomeContainingIgnoreCase(String nome);

    // Busca por ID do tema
    List<Postagem> findByTemaId(Long temaId);

    // Busca por ID do usuário
    List<Postagem> findByUsuarioId(Long usuarioId);

    // Busca por ID do usuário e do tema
    List<Postagem> findByUsuarioIdAndTemaId(Long usuarioId, Long temaId);
}