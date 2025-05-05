package nilton.acelera.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import nilton.acelera.demo.security.jwt.JwtService;
import nilton.acelera.demo.dto.UsuarioTokenDTO;
import nilton.acelera.demo.model.Usuario;
import nilton.acelera.demo.service.UsuarioService;

@RestController
@RequestMapping("/usuarios")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private AuthenticationManager authenticationManager;

    // Acesso permitido para todos, não precisa estar logado
    @PostMapping("/cadastrar")
    public ResponseEntity<Usuario> cadastrar(@RequestBody Usuario usuario) {
        return usuarioService.cadastrarUsuario(usuario)
                .map(u -> ResponseEntity.status(HttpStatus.CREATED).body(u))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.BAD_REQUEST).build());
    }

    // Acesso permitido somente para usuários com ROLE_ADMIN
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        return usuarioService.deletarUsuario(id) ?
                ResponseEntity.noContent().build() :
                ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    // Acesso permitido para ROLE_USER ou ROLE_ADMIN
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @PutMapping("/atualizar")
    public ResponseEntity<Usuario> atualizar(@RequestBody Usuario usuario) {
        return usuarioService.atualizarUsuario(usuario)
                .map(u -> ResponseEntity.ok(u))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @PostMapping("/login")
    public ResponseEntity<UsuarioTokenDTO> login(@RequestBody Usuario usuario) {
        UsernamePasswordAuthenticationToken authenticationToken =
            new UsernamePasswordAuthenticationToken(usuario.getUsuario(), usuario.getSenha());
    
        Authentication authentication = authenticationManager.authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    
        Usuario usuarioAutenticado = (Usuario) authentication.getPrincipal();
        String token = jwtService.gerarTokenComAuthorities(usuarioAutenticado);
    
        UsuarioTokenDTO dto = new UsuarioTokenDTO(
            usuarioAutenticado.getId(),
            usuarioAutenticado.getNome(),
            usuarioAutenticado.getUsuario(),
            usuarioAutenticado.getFoto(),
            token,
            usuarioAutenticado.getTipo().name()
        );
    
        return ResponseEntity.ok(dto);
    }
}