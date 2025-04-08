package nilton.acelera.demo.service;

import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import nilton.acelera.demo.dto.UsuarioLoginDTO;
import nilton.acelera.demo.dto.UsuarioTokenDTO;
import nilton.acelera.demo.model.TipoUsuario;
import nilton.acelera.demo.model.Usuario;
import nilton.acelera.demo.repository.UsuarioRepository;
import nilton.acelera.demo.security.jwt.JwtService;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional
    public Optional<Usuario> cadastrarUsuario(Usuario usuario) {
        return usuarioRepository.findByUsuario(usuario.getUsuario())
            .map(u -> Optional.<Usuario>empty())
            .orElseGet(() -> {
                usuario.setTipo(usuario.getTipo() != null ? usuario.getTipo() : TipoUsuario.ROLE_USER);
                usuario.setSenha(passwordEncoder.encode(usuario.getSenha()));
                return Optional.of(usuarioRepository.save(usuario));
            });
    }

    public Optional<UsuarioTokenDTO> autenticarUsuario(UsuarioLoginDTO usuarioLogin) {
        if (usuarioLogin == null || usuarioLogin.getUsuario() == null || usuarioLogin.getSenha() == null) {
            return Optional.empty();
        }

        return usuarioRepository.findByUsuario(usuarioLogin.getUsuario()) // Verifique se o método está correto
            .filter(usuario -> passwordEncoder.matches(usuarioLogin.getSenha(), usuario.getSenha()))
            .map(usuario -> {
                String token = jwtService.gerarToken(usuario.getUsuario());
                return new UsuarioTokenDTO(
                    usuario.getId(),
                    usuario.getNome(),
                    usuario.getUsuario(),
                    usuario.getFoto(),
                    token,
                    usuario.getTipo().name() 
                );
            });
    }

    @Transactional
    public Optional<Usuario> atualizarUsuario(Usuario usuarioAtualizado) {
        return usuarioRepository.findById(usuarioAtualizado.getId())
            .map(usuario -> {
                usuario.setNome(usuarioAtualizado.getNome());
                usuario.setUsuario(usuarioAtualizado.getUsuario());
                usuario.setFoto(usuarioAtualizado.getFoto());
                
                // Atualiza role apenas se for ADMIN
                if (usuarioAtualizado.getTipo() != null && usuario.getTipo() == TipoUsuario.ROLE_ADMIN) {
                    usuario.setTipo(usuarioAtualizado.getTipo());
                }

                if (usuarioAtualizado.getSenha() != null && !usuarioAtualizado.getSenha().isBlank()) {
                    usuario.setSenha(passwordEncoder.encode(usuarioAtualizado.getSenha()));
                }

                return usuarioRepository.save(usuario);
            });
    }

    @Transactional
    public boolean deletarUsuario(Long id) {
        return usuarioRepository.findById(id)
            .map(usuario -> {
                usuarioRepository.delete(usuario);
                return true;
            })
            .orElse(false);
    }

    public Optional<Usuario> buscarPorId(Long id) {
        return usuarioRepository.findById(id);
    }
}
