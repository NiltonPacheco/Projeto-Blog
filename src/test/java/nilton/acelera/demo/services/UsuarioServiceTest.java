package nilton.acelera.demo.services;

import nilton.acelera.demo.dto.UsuarioLoginDTO;
import nilton.acelera.demo.dto.UsuarioTokenDTO;
import nilton.acelera.demo.model.TipoUsuario;
import nilton.acelera.demo.model.Usuario;
import nilton.acelera.demo.repository.UsuarioRepository;
import nilton.acelera.demo.security.jwt.JwtService;
import nilton.acelera.demo.service.UsuarioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UsuarioServiceTest {

    @InjectMocks
    private UsuarioService usuarioService;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private JwtService jwtService;

    @Mock
    private PasswordEncoder passwordEncoder;

    private Usuario usuario1;
    private Usuario usuario2;
    private UsuarioLoginDTO usuarioLoginDTO;

    @BeforeEach
    void setUp() {
        usuario1 = new Usuario("Teste1", "teste1", "senha1", "foto1", TipoUsuario.ROLE_USER);
        usuario1.setId(1L); // Define o ID separadamente se necessário para os testes

        usuario2 = new Usuario("Teste2", "teste2", "senha2", "foto2", TipoUsuario.ROLE_ADMIN);
        usuario2.setId(2L); // Define o ID separadamente se necessário para os testes

        usuarioLoginDTO = new UsuarioLoginDTO("teste1", "senha1");
    }

    @Test
    void cadastrarUsuario_deveCadastrarNovoUsuario() {
        Usuario novoUsuario = new Usuario("Novo", "novo", "nova_senha", "nova_foto", TipoUsuario.ROLE_USER);
        when(usuarioRepository.findByUsuario("novo")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("nova_senha")).thenReturn("nova_senha_criptografada");
        Usuario usuarioSalvo = new Usuario("Novo", "novo", "nova_senha_criptografada", "nova_foto", TipoUsuario.ROLE_USER);
        usuarioSalvo.setId(3L);
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuarioSalvo);

        Optional<Usuario> usuarioCadastrado = usuarioService.cadastrarUsuario(novoUsuario);

        assertTrue(usuarioCadastrado.isPresent());
        assertEquals("novo", usuarioCadastrado.get().getUsuario());
        assertEquals("nova_senha_criptografada", usuarioCadastrado.get().getSenha());
        verify(usuarioRepository, times(1)).findByUsuario("novo");
        verify(passwordEncoder, times(1)).encode("nova_senha");
        verify(usuarioRepository, times(1)).save(any(Usuario.class));
    }

    @Test
    void autenticarUsuario_deveRetornarTokenSeCredenciaisValidas() {
        when(usuarioRepository.findByUsuario("teste1")).thenReturn(Optional.of(usuario1));
        when(passwordEncoder.matches("senha1", "senha1")).thenReturn(true);
        when(jwtService.gerarToken("teste1")).thenReturn("token_valido"); // Passando o nome de usuário para gerar o token

        Optional<UsuarioTokenDTO> tokenDTO = usuarioService.autenticarUsuario(usuarioLoginDTO);

        assertTrue(tokenDTO.isPresent());
        assertEquals("token_valido", tokenDTO.get().getToken());
        verify(usuarioRepository, times(1)).findByUsuario("teste1");
        verify(passwordEncoder, times(1)).matches("senha1", "senha1");
        verify(jwtService, times(1)).gerarToken("teste1");
    }

    @Test
    void autenticarUsuario_deveRetornarOptionalVazioSeUsuarioNaoEncontrado() {
        when(usuarioRepository.findByUsuario("teste_nao_existe")).thenReturn(Optional.empty());
        Optional<UsuarioTokenDTO> tokenDTO = usuarioService.autenticarUsuario(new UsuarioLoginDTO("teste_nao_existe", "senha"));
        assertFalse(tokenDTO.isPresent());
        verify(usuarioRepository, times(1)).findByUsuario("teste_nao_existe");
        verify(passwordEncoder, never()).matches(anyString(), anyString());
        verify(jwtService, never()).gerarToken(anyString());
    }

    @Test
    void buscarPorId_deveRetornarUsuarioExistente() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario1));
        Optional<Usuario> usuarioEncontrado = usuarioService.buscarPorId(1L);
        assertTrue(usuarioEncontrado.isPresent());
        assertEquals("teste1", usuarioEncontrado.get().getUsuario());
        verify(usuarioRepository, times(1)).findById(1L);
    }

    @Test
    void buscarPorId_deveRetornarOptionalVazioSeUsuarioNaoExiste() {
        when(usuarioRepository.findById(3L)).thenReturn(Optional.empty());
        Optional<Usuario> usuarioEncontrado = usuarioService.buscarPorId(3L);
        assertFalse(usuarioEncontrado.isPresent());
        verify(usuarioRepository, times(1)).findById(3L);
    }
}