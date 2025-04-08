package nilton.acelera.demo.services;

import nilton.acelera.demo.model.Postagem;
import nilton.acelera.demo.model.Tema;
import nilton.acelera.demo.model.TipoUsuario;
import nilton.acelera.demo.model.Usuario;
import nilton.acelera.demo.repository.PostagemRepository;
import nilton.acelera.demo.service.PostagemService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PostagemServiceTest {

    @InjectMocks
    private PostagemService postagemService;

    @Mock
    private PostagemRepository postagemRepository;

    private Usuario usuario1;
    private Tema tema1;
    private Postagem postagem1;
    private Postagem postagem2;

    @BeforeEach
    void setUp() {
        usuario1 = new Usuario("Teste1", "teste1", "senha1", "foto1", TipoUsuario.ROLE_USER);
        usuario1.setId(1L);

        tema1 = new Tema(1L, "Tecnologia");

        postagem1 = new Postagem("Título 1", "Texto 1", usuario1, tema1);
        postagem1.setId(10L);
        postagem1.setData(LocalDateTime.now());

        postagem2 = new Postagem("Título 2", "Texto 2", usuario1, tema1);
        postagem2.setId(20L);
        postagem2.setData(LocalDateTime.now().plusDays(1));
    }

    @Test
    void listarTodas_deveRetornarListaDePostagens() {
        when(postagemRepository.findAll()).thenReturn(Arrays.asList(postagem1, postagem2));
        List<Postagem> postagens = postagemService.listarTodas();
        assertEquals(2, postagens.size());
        assertTrue(postagens.contains(postagem1));
        assertTrue(postagens.contains(postagem2));
        verify(postagemRepository, times(1)).findAll();
    }

    @Test
    void buscarPorId_deveRetornarPostagemExistente() {
        when(postagemRepository.findById(10L)).thenReturn(Optional.of(postagem1));
        Optional<Postagem> postagem = postagemService.buscarPorId(10L);
        assertTrue(postagem.isPresent());
        assertEquals("Título 1", postagem.get().getTitulo());
        verify(postagemRepository, times(1)).findById(10L);
    }

    @Test
    void buscarPorId_deveRetornarOptionalVazioSePostagemNaoExiste() {
        when(postagemRepository.findById(30L)).thenReturn(Optional.empty());
        Optional<Postagem> postagem = postagemService.buscarPorId(30L);
        assertFalse(postagem.isPresent());
        verify(postagemRepository, times(1)).findById(30L);
    }

    @Test
    void buscarPorTitulo_deveRetornarListaDePostagensCorrespondentes() {
        when(postagemRepository.findAllByTituloContainingIgnoreCase("Título")).thenReturn(Arrays.asList(postagem1, postagem2));
        List<Postagem> postagens = postagemService.buscarPorTitulo("Título");
        assertEquals(2, postagens.size());
        assertEquals("Título 1", postagens.get(0).getTitulo());
        assertEquals("Título 2", postagens.get(1).getTitulo());
        verify(postagemRepository, times(1)).findAllByTituloContainingIgnoreCase("Título");
    }

    @Test
    void criar_deveSalvarNovaPostagem() {
        Postagem novaPostagem = new Postagem("Novo Título", "Novo Texto", usuario1, tema1);
        when(postagemRepository.save(any(Postagem.class))).thenReturn(new Postagem("Novo Título", "Novo Texto", usuario1, tema1));
        Postagem postagemSalva = postagemService.criar(novaPostagem);
        assertEquals("Novo Título", postagemSalva.getTitulo());
        verify(postagemRepository, times(1)).save(novaPostagem);
    }

    @Test
    void atualizar_deveAtualizarPostagemExistente() {
        Postagem postagemAtualizada = new Postagem("Título Atualizado", "Texto Atualizado", usuario1, tema1);
        postagemAtualizada.setId(10L);
        when(postagemRepository.findById(10L)).thenReturn(Optional.of(postagem1));
        when(postagemRepository.save(postagemAtualizada)).thenReturn(postagemAtualizada);

        Optional<Postagem> resultado = postagemService.atualizar(10L, postagemAtualizada);

        assertTrue(resultado.isPresent());
        assertEquals("Título Atualizado", resultado.get().getTitulo());
        verify(postagemRepository, times(1)).findById(10L);
        verify(postagemRepository, times(1)).save(postagemAtualizada);
    }

    @Test
    void deletar_deveRetornarTrueSePostagemDeletada() {
        when(postagemRepository.findById(10L)).thenReturn(Optional.of(postagem1));
        doNothing().when(postagemRepository).deleteById(10L);
        assertTrue(postagemService.deletar(10L));
        verify(postagemRepository, times(1)).findById(10L);
        verify(postagemRepository, times(1)).deleteById(10L);
    }

    @Test
    void deletar_deveRetornarFalseSePostagemNaoExiste() {
        when(postagemRepository.findById(30L)).thenReturn(Optional.empty());
        assertFalse(postagemService.deletar(30L));
        verify(postagemRepository, times(1)).findById(30L);
        verify(postagemRepository, never()).deleteById(anyLong());
    }

    @Test
    void buscarPorTemaId_deveRetornarListaDePostagensPorTema() {
        when(postagemRepository.findByTemaId(1L)).thenReturn(Arrays.asList(postagem1));
        List<Postagem> postagens = postagemService.buscarPorTemaId(1L);
        assertEquals(1, postagens.size());
        assertEquals("Título 1", postagens.get(0).getTitulo());
        verify(postagemRepository, times(1)).findByTemaId(1L);
    }

    @Test
    void buscarPorUsuarioId_deveRetornarListaDePostagensPorUsuario() {
        when(postagemRepository.findByUsuarioId(1L)).thenReturn(Arrays.asList(postagem1, postagem2));
        List<Postagem> postagens = postagemService.buscarPorUsuarioId(1L);
        assertEquals(2, postagens.size());
        assertEquals("Título 1", postagens.get(0).getTitulo());
        assertEquals("Título 2", postagens.get(1).getTitulo());
        verify(postagemRepository, times(1)).findByUsuarioId(1L);
    }

    @Test
    void buscarPorUsuarioIdETemaId_deveRetornarListaDePostagensPorUsuarioETema() {
        when(postagemRepository.findByUsuarioIdAndTemaId(1L, 1L)).thenReturn(Arrays.asList(postagem1));
        List<Postagem> postagens = postagemService.buscarPorUsuarioIdETemaId(1L, 1L);
        assertEquals(1, postagens.size());
        assertEquals("Título 1", postagens.get(0).getTitulo());
        verify(postagemRepository, times(1)).findByUsuarioIdAndTemaId(1L, 1L);
    }
}