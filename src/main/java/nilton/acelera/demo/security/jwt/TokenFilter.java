package nilton.acelera.demo.security.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import nilton.acelera.demo.model.Usuario;
import nilton.acelera.demo.repository.UsuarioRepository;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;

@Component
public class TokenFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UsuarioRepository usuarioRepository;

    public TokenFilter(JwtService jwtService, UsuarioRepository usuarioRepository) {
        this.jwtService = jwtService;
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    protected void doFilterInternal(
        HttpServletRequest request,
        HttpServletResponse response,
        FilterChain filterChain
    ) throws ServletException, IOException {
        String requestUri = request.getRequestURI();
        logger.info("Requisição recebida para: " + requestUri);
    
        // Ignorar endpoints públicos
        if (requestUri.equals("/usuarios/login") || requestUri.equals("/usuarios/cadastrar")) {
            logger.info("Ignorando filtro para o endpoint público: " + requestUri);
            filterChain.doFilter(request, response);
            return;
        }
    
        String token = recuperarToken(request);
        logger.info("Token recuperado: " + token);
    
        if (token != null && jwtService.validarToken(token)) {
            logger.info("Token é válido. Extraindo informações do usuário...");
            String usuarioEmail = jwtService.extrairUsuario(token);
            logger.info("Usuário extraído do token: " + usuarioEmail);
    
            Optional<Usuario> usuarioOpt = usuarioRepository.findByUsuario(usuarioEmail); // Retorna um Optional<Usuario>
    
            if (usuarioOpt.isPresent()) {
                Usuario usuario = usuarioOpt.get();  
                logger.info("Usuário encontrado: " + usuario.getUsuario());
                var authentication = new UsernamePasswordAuthenticationToken(
                    usuario,
                    null,
                    usuario.getAuthorities() 
                );
                SecurityContextHolder.getContext().setAuthentication(authentication);
                logger.info("Autenticação configurada no contexto de segurança para o usuário: " + usuarioEmail);
            } else {
                logger.warn("Usuário não encontrado para o token JWT: " + usuarioEmail);
            }
        } else {
            logger.warn("Token JWT ausente ou inválido.");
        }
    
        filterChain.doFilter(request, response);
    }

    private String recuperarToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        logger.info("Cabeçalho Authorization recebido: " + authHeader);
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        logger.error("Token JWT ausente ou mal formatado.");
        return null;
    }
    
}
