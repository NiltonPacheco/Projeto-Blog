package nilton.acelera.demo.security.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import nilton.acelera.demo.model.Usuario;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class JwtService {

    private static final Logger logger = LoggerFactory.getLogger(JwtService.class);

    private static final long EXPIRACAO = 3600000; 
    // Chave de 512 bits (64 caracteres)
    private static final String SEGREDO = "suaChaveSecretaParaJWTCom512BitsMuitoSeguraEGrande12345678901234567890";

    private Key gerarChave() {
        return Keys.hmacShaKeyFor(SEGREDO.getBytes());
    }

    // NOVO: Gera token incluindo as authorities do usuário
    public String gerarTokenComAuthorities(Usuario usuario) {
        try {
            List<String> authorities = usuario.getAuthorities().stream()
                .map(a -> a.getAuthority())
                .collect(Collectors.toList());

            String token = Jwts.builder()
                .setSubject(usuario.getUsuario()) // ou getUsername()
                .claim("authorities", authorities)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRACAO))
                .signWith(gerarChave(), SignatureAlgorithm.HS512)
                .compact();

            logger.info("Token gerado: " + token);
            return token;
        } catch (Exception e) {
            logger.error("Erro ao gerar token: ", e);
            throw new RuntimeException("Erro ao gerar token", e);
        }
    }

    // Mantém o método antigo para compatibilidade, se precisar
    public String gerarToken(String usuario) {
        try {
            String token = Jwts.builder()
                .setSubject(usuario)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRACAO))
                .signWith(gerarChave(), SignatureAlgorithm.HS512)
                .compact();

            logger.info("Token gerado: " + token);
            return token;
        } catch (Exception e) {
            logger.error("Erro ao gerar token: ", e);
            throw new RuntimeException("Erro ao gerar token", e);
        }
    }

    public boolean validarToken(String token) {
        try {
            Jwts.parserBuilder()
                .setSigningKey(gerarChave())
                .build()
                .parseClaimsJws(token);
            logger.info("Token válido: " + token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            logger.error("Erro ao validar token: ", e);
            return false;
        }
    }

    public String extrairUsuario(String token) {
        try {
            String usuario = Jwts.parserBuilder()
                .setSigningKey(gerarChave())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();

            logger.info("Usuário extraído do token: " + usuario);
            return usuario;
        } catch (JwtException | IllegalArgumentException e) {
            logger.error("Erro ao extrair usuário do token: ", e);
            throw new RuntimeException("Token inválido ou mal formado", e);
        }
    }
}