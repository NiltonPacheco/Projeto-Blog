package nilton.acelera.demo.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import nilton.acelera.demo.model.Postagem;
import nilton.acelera.demo.model.Tema;
import nilton.acelera.demo.model.Usuario;
import nilton.acelera.demo.repository.PostagemRepository;
import nilton.acelera.demo.repository.TemaRepository;
import nilton.acelera.demo.repository.UsuarioRepository;

@Service
public class PostagemService {

    @Autowired
    private PostagemRepository postagemRepository;

    @Autowired
    private TemaRepository temaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

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
    public Optional<Postagem> criar(Postagem postagem) {
        // Valida se o tema existe
        Optional<Tema> temaOpt = temaRepository.findById(postagem.getTema().getId());
        if (temaOpt.isEmpty()) return Optional.empty();

        // Valida se o usuário existe
        Optional<Usuario> usuarioOpt = usuarioRepository.findById(postagem.getUsuario().getId());
        if (usuarioOpt.isEmpty()) return Optional.empty();

        postagem.setTema(temaOpt.get());
        postagem.setUsuario(usuarioOpt.get());
        return Optional.of(postagemRepository.save(postagem));
    }

    @Transactional
    public Optional<Postagem> atualizar(Long id, Postagem postagemAtualizada, Usuario usuarioRequisitante) {
        return postagemRepository.findById(id)
            .filter(postagem -> 
                postagem.getUsuario().getId().equals(usuarioRequisitante.getId()) ||
                usuarioRequisitante.getTipo().name().equals("ROLE_ADMIN")
            )
            .flatMap(postagem -> {
                // Valida se o tema existe
                Optional<Tema> temaOpt = temaRepository.findById(postagemAtualizada.getTema().getId());
                if (temaOpt.isEmpty()) return Optional.empty();

                postagem.setTitulo(postagemAtualizada.getTitulo());
                postagem.setTexto(postagemAtualizada.getTexto());
                postagem.setTema(temaOpt.get());
                return Optional.of(postagemRepository.save(postagem));
            });
    }

    @Transactional
    public boolean deletar(Long id, Usuario usuarioRequisitante) {
        return postagemRepository.findById(id)
            .filter(postagem -> 
                postagem.getUsuario().getId().equals(usuarioRequisitante.getId()) ||
                usuarioRequisitante.getTipo().name().equals("ROLE_ADMIN")
            )
            .map(postagem -> {
                postagemRepository.deleteById(id);
                return true;
            })
            .orElse(false);
    }
}