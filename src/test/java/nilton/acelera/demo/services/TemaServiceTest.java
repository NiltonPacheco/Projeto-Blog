package nilton.acelera.demo.services;

import nilton.acelera.demo.model.Tema;
import nilton.acelera.demo.repository.TemaRepository;
import nilton.acelera.demo.repository.PostagemRepository; // Import necessário para o mock
import nilton.acelera.demo.service.TemaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TemaServiceTest {

    @InjectMocks
    private TemaService temaService;

    @Mock
    private TemaRepository temaRepository;

    @Mock
    private PostagemRepository postagemRepository; // Mock do PostagemRepository

    private Tema tema1;
    private Tema tema2;

    @BeforeEach
    void setUp() {
        tema1 = new Tema(1L, "Tecnologia");
        tema2 = new Tema(2L, "Viagem");
    }

    @Test
    void listarTodos_deveRetornarListaDeTemas() {
        when(temaRepository.findAll()).thenReturn(Arrays.asList(tema1, tema2));
        List<Tema> temas = temaService.listarTodos();
        assertEquals(2, temas.size());
        assertTrue(temas.contains(tema1));
        assertTrue(temas.contains(tema2));
        verify(temaRepository, times(1)).findAll();
    }

    @Test
    void buscarPorId_deveRetornarTemaExistente() {
        when(temaRepository.findById(1L)).thenReturn(Optional.of(tema1));
        Optional<Tema> tema = temaService.buscarPorId(1L);
        assertTrue(tema.isPresent());
        assertEquals("Tecnologia", tema.get().getDescricao());
        verify(temaRepository, times(1)).findById(1L);
    }

    @Test
    void buscarPorId_deveRetornarOptionalVazioSeTemaNaoExiste() {
        when(temaRepository.findById(3L)).thenReturn(Optional.empty());
        Optional<Tema> tema = temaService.buscarPorId(3L);
        assertFalse(tema.isPresent());
        verify(temaRepository, times(1)).findById(3L);
    }

    @Test
    void buscarPorDescricao_deveRetornarListaDeTemasCorrespondentes() {
        when(temaRepository.findAllByDescricaoContainingIgnoreCase("tecnologia")).thenReturn(Arrays.asList(tema1));
        List<Tema> temas = temaService.buscarPorDescricao("tecnologia");
        assertEquals(1, temas.size());
        assertEquals("Tecnologia", temas.get(0).getDescricao());
        verify(temaRepository, times(1)).findAllByDescricaoContainingIgnoreCase("tecnologia");
    }

    @Test
    void criar_deveSalvarNovoTema() {
        Tema novoTema = new Tema(null, "Culinária"); // ID será gerado ao salvar
        when(temaRepository.save(any(Tema.class))).thenReturn(new Tema(3L, "Culinária")); // Simula o ID sendo gerado
        Tema temaSalvo = temaService.criar(novoTema);
        assertNotNull(temaSalvo.getId());
        assertEquals("Culinária", temaSalvo.getDescricao());
        verify(temaRepository, times(1)).save(novoTema);
    }

    @Test
    void atualizar_deveAtualizarTemaExistente() {
        Tema temaAtualizado = new Tema(1L, "Novotecnologia");
        when(temaRepository.findById(1L)).thenReturn(Optional.of(tema1));
        when(temaRepository.save(temaAtualizado)).thenReturn(temaAtualizado);

        Optional<Tema> resultado = temaService.atualizar(1L, temaAtualizado);

        assertTrue(resultado.isPresent());
        assertEquals("Novotecnologia", resultado.get().getDescricao());
        verify(temaRepository, times(1)).findById(1L);
        verify(temaRepository, times(1)).save(temaAtualizado);
    }

    @Test
    void atualizar_deveRetornarOptionalVazioSeTemaNaoExiste() {
        Tema temaAtualizado = new Tema(3L, "OutroTema");
        when(temaRepository.findById(3L)).thenReturn(Optional.empty());

        Optional<Tema> resultado = temaService.atualizar(3L, temaAtualizado);

        assertFalse(resultado.isPresent());
        verify(temaRepository, times(1)).findById(3L);
        verify(temaRepository, never()).save(any());
    }

    @Test
    void deletar_deveDeletarTemaExistenteSeNaoHouverPostagens() {
        when(temaRepository.existsById(1L)).thenReturn(true);
        when(postagemRepository.findByTemaId(1L)).thenReturn(List.of()); // Simula não haver postagens
        doNothing().when(temaRepository).deleteById(1L);

        ResponseEntity<Void> response = temaService.deletar(1L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(temaRepository, times(1)).existsById(1L);
        verify(postagemRepository, times(1)).findByTemaId(1L);
        verify(temaRepository, times(1)).deleteById(1L);
    }

    @Test
    void deletar_deveRetornarNotFoundSeTemaNaoExiste() {
        when(temaRepository.existsById(3L)).thenReturn(false);

        ResponseEntity<Void> response = temaService.deletar(3L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(temaRepository, times(1)).existsById(3L);
        verify(postagemRepository, never()).findByTemaId(any());
        verify(temaRepository, never()).deleteById(any());
    }

    @Test
    void deletar_deveRetornarBadRequestSeHouverPostagensAssociadas() {
        when(temaRepository.existsById(1L)).thenReturn(true);
        when(postagemRepository.findByTemaId(1L)).thenReturn(List.of(mock(nilton.acelera.demo.model.Postagem.class))); // Simula haver postagens

        ResponseEntity<Void> response = temaService.deletar(1L);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        verify(temaRepository, times(1)).existsById(1L);
        verify(postagemRepository, times(1)).findByTemaId(1L);
        verify(temaRepository, never()).deleteById(any());
    }
}