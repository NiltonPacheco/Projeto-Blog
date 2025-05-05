package nilton.acelera.demo.controller;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import nilton.acelera.demo.model.Postagem;
import nilton.acelera.demo.model.Usuario;
import nilton.acelera.demo.repository.PostagemRepository;
import nilton.acelera.demo.repository.UsuarioRepository;

@RestController
@RequestMapping("/postagens")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class PostagemController {

    @Autowired
    private PostagemRepository postagemRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    // Listar todas as postagens.
    @GetMapping
    public ResponseEntity<List<Postagem>> listarPostagens() {
        return ResponseEntity.ok(postagemRepository.findAll());
    }

    // Buscar por título
    @GetMapping("/titulo/{titulo}")
    public ResponseEntity<List<Postagem>> buscarPorTitulo(@PathVariable String titulo) {
        return ResponseEntity.ok(postagemRepository.findAllByTituloContainingIgnoreCase(titulo));
    }

    // Criar postagem (qualquer usuário autenticado pode criar)
    @PostMapping
    public ResponseEntity<Postagem> criarPostagem(@RequestBody Postagem postagem, Principal principal) {
        // Garante que o usuário logado é o dono da postagem
        Usuario usuario = usuarioRepository.findByUsuario(principal.getName()).orElse(null);
        if (usuario == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        postagem.setUsuario(usuario);
        return ResponseEntity.status(HttpStatus.CREATED).body(postagemRepository.save(postagem));
    }

    // Atualizar postagem
    @PutMapping
public ResponseEntity<Postagem> atualizarPostagem(@RequestBody Postagem postagem, Principal principal) {
    Optional<Postagem> postagemExistente = postagemRepository.findById(postagem.getId());
    if (!postagemExistente.isPresent()) {
        return ResponseEntity.notFound().build();
    }

    Usuario usuarioLogado = usuarioRepository.findByUsuario(principal.getName()).orElse(null);
    if (usuarioLogado == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

    Postagem postagemAtual = postagemExistente.get();

   
    boolean isAdmin = usuarioLogado.getTipo().name().equals("ADMIN") || usuarioLogado.getTipo().name().equals("ROLE_ADMIN");
    boolean isDono = postagemAtual.getUsuario().getId().equals(usuarioLogado.getId());

    if (isAdmin) {
        if (isDono) {
            // Admin editando a própria postagem: pode editar tudo
            postagem.setUsuario(usuarioLogado); // Garante que não muda o dono
            return ResponseEntity.ok(postagemRepository.save(postagem));
        } else {
            // Admin editando postagem de outro: só pode mudar o tema
            postagemAtual.setTema(postagem.getTema());
            return ResponseEntity.ok(postagemRepository.save(postagemAtual));
        }
    } else {
        // User comum: só pode editar a própria postagem
        if (isDono) {
            postagem.setUsuario(usuarioLogado); // Garante que não muda o dono
            return ResponseEntity.ok(postagemRepository.save(postagem));
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }
}

    // Excluir postagem
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarPostagem(@PathVariable Long id, Principal principal) {
        Optional<Postagem> postagemExistente = postagemRepository.findById(id);
        if (!postagemExistente.isPresent()) {
            return ResponseEntity.notFound().build();
        }
    
        Usuario usuarioLogado = usuarioRepository.findByUsuario(principal.getName()).orElse(null);
        if (usuarioLogado == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    
        Postagem postagemAtual = postagemExistente.get();
    
        // Corrigido aqui:
        boolean isAdmin = usuarioLogado.getTipo().name().equals("ADMIN") || usuarioLogado.getTipo().name().equals("ROLE_ADMIN");
        boolean isDono = postagemAtual.getUsuario().getId().equals(usuarioLogado.getId());
    
        if (isAdmin || isDono) {
            postagemRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }
}