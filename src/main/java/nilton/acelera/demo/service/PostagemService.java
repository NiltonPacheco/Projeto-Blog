package nilton.acelera.demo.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import nilton.acelera.demo.model.Postagem;
import nilton.acelera.demo.repository.PostagemRepository;


@Service
public class PostagemService {

    @Autowired
    private PostagemRepository postagemRepository;

    public List<Postagem> listarTodas() {
        return postagemRepository.findAll();
    }

    public Optional<Postagem> buscarPorId(Long id) {
        return postagemRepository.findById(id);
    }

    public List<Postagem> buscarPorTitulo(String titulo) {
        return postagemRepository.findAllByTituloContainingIgnoreCase(titulo);
    }

    public List<Postagem> buscarPorTemaId(Long temaId) {
        return postagemRepository.findByTemaId(temaId);
    }

    public List<Postagem> buscarPorUsuarioId(Long usuarioId) {
        return postagemRepository.findByUsuarioId(usuarioId);
    }

    public List<Postagem> buscarPorUsuarioIdETemaId(Long usuarioId, Long temaId) {
        return postagemRepository.findByUsuarioIdAndTemaId(usuarioId, temaId);
    }

    @Transactional
    public Postagem criar(Postagem postagem) {
        // Aqui você pode adicionar validações de regras de negócio antes de salvar
        return postagemRepository.save(postagem);
    }

    @Transactional
    public Optional<Postagem> atualizar(Long id, Postagem postagemAtualizada) {
        return postagemRepository.findById(id)
                .map(postagem -> {
                    postagemAtualizada.setId(id);
                    return postagemRepository.save(postagemAtualizada);
                });
    }

    @Transactional
    public boolean deletar(Long id) {
        return postagemRepository.findById(id)
                .map(postagem -> {
                    postagemRepository.deleteById(id);
                    return true;
                })
                .orElse(false);
    }
}