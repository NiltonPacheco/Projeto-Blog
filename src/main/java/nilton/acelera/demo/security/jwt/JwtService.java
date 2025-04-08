package nilton.acelera.demo.security.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;

@Service
public class JwtService {

    private static final Logger logger = LoggerFactory.getLogger(JwtService.class);

    private static final long EXPIRACAO = 3600000; // 1 hora
    // Chave de 512 bits (64 caracteres)
    private static final String SEGREDO = "suaChaveSecretaParaJWTCom512BitsMuitoSeguraEGrande12345678901234567890";

    private Key gerarChave() {
        return Keys.hmacShaKeyFor(SEGREDO.getBytes()); // Sempre usa o mesmo SEGREDO
    }
    
    public String gerarToken(String usuario) {
        try {
            String token = Jwts.builder()
                .setSubject(usuario)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRACAO)) // Define expiração
                .signWith(gerarChave(), SignatureAlgorithm.HS512) // Usa a chave gerada com o mesmo SEGREDO
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
                .setSigningKey(gerarChave()) // Gera chave de 512 bits para validação
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
                .parseClaimsJws(token) // Valida e analisa o token
                .getBody()
                .getSubject(); // Extrai o campo "subject" do token
    
            logger.info("Usuário extraído do token: " + usuario);
            return usuario;
        } catch (JwtException | IllegalArgumentException e) {
            logger.error("Erro ao extrair usuário do token: ", e);
            throw new RuntimeException("Token inválido ou mal formado", e);
        }
    }
    
    
}
