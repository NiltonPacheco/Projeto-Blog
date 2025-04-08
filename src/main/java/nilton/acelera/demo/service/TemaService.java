package nilton.acelera.demo.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import nilton.acelera.demo.model.Tema;
import nilton.acelera.demo.repository.PostagemRepository;
import nilton.acelera.demo.repository.TemaRepository;

@Service
public class TemaService {

    @Autowired
    private TemaRepository temaRepository;

    @Autowired
    private PostagemRepository postagemRepository;

    public List<Tema> listarTodos() {
        return temaRepository.findAll();
    }

    public Optional<Tema> buscarPorId(Long id) {
        return temaRepository.findById(id);
    }

    public List<Tema> buscarPorDescricao(String descricao) {
        return temaRepository.findAllByDescricaoContainingIgnoreCase(descricao);
    }

    @Transactional
    public Tema criar(Tema tema) {
        return temaRepository.save(tema);
    }

    @Transactional
    public Optional<Tema> atualizar(Long id, Tema temaAtualizado) {
        return temaRepository.findById(id)
                .map(tema -> {
                    temaAtualizado.setId(id);
                    return temaRepository.save(temaAtualizado);
                });
    }

    @Transactional
    public ResponseEntity<Void> deletar(Long id) {
        if (!temaRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        if (!postagemRepository.findByTemaId(id).isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
        temaRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}