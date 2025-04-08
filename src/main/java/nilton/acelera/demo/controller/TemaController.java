package nilton.acelera.demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import nilton.acelera.demo.model.Tema;
import nilton.acelera.demo.service.TemaService;

@RestController
@RequestMapping("/temas")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@Tag(name = "Tema", description = "Endpoints para gerenciamento de temas")
@SecurityRequirement(name = "BearerAuth")
public class TemaController {

    @Autowired
    private TemaService temaService;

    @Operation(summary = "Listar todos os temas", description = "Retorna uma lista de todos os temas cadastrados.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Temas encontrados",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Tema.class))),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor")
    })
    @GetMapping
    public ResponseEntity<List<Tema>> listarTemas() {
        return ResponseEntity.ok(temaService.listarTodos());
    }

    @Operation(summary = "Buscar tema por ID", description = "Retorna um tema específico com base no seu ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tema encontrado",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Tema.class))),
            @ApiResponse(responseCode = "404", description = "Tema não encontrado"),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Tema> buscarPorId(@PathVariable Long id) {
        return temaService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Buscar temas por descrição", description = "Retorna uma lista de temas que contêm a descrição especificada (ignorando maiúsculas/minúsculas).")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Temas encontrados",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Tema.class))),
            @ApiResponse(responseCode = "204", description = "Nenhum tema encontrado com a descrição especificada"),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor")
    })
    @GetMapping("/descricao")
    public ResponseEntity<List<Tema>> buscarPorDescricao(
            @Parameter(description = "Descrição a ser pesquisada", example = "Tecnologia")
            @RequestParam String descricao) {
        List<Tema> temas = temaService.buscarPorDescricao(descricao);
        return temas.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(temas);
    }

    @Operation(summary = "Criar um novo tema", description = "Cria um novo tema com a descrição fornecida.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Tema criado com sucesso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Tema.class))),
            @ApiResponse(responseCode = "400", description = "Dados de entrada inválidos"),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor")
    })
    @PostMapping
    public ResponseEntity<Tema> criarTema(@Valid @RequestBody Tema tema) {
        try {
            Tema novoTema = temaService.criar(tema);
            return ResponseEntity.status(HttpStatus.CREATED).body(novoTema);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Operation(summary = "Atualizar um tema existente", description = "Atualiza a descrição de um tema existente com base no ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tema atualizado com sucesso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Tema.class))),
            @ApiResponse(responseCode = "400", description = "Dados de entrada inválidos"),
            @ApiResponse(responseCode = "404", description = "Tema não encontrado"),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor")
    })
    @PutMapping("/{id}")
    public ResponseEntity<Tema> atualizarTema(@PathVariable Long id, @Valid @RequestBody Tema tema) {
        try {
            return temaService.atualizar(id, tema)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Operation(summary = "Excluir um tema", description = "Exclui um tema com base no seu ID, verificando se não há postagens associadas.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Tema excluído com sucesso"),
            @ApiResponse(responseCode = "400", description = "Não é possível excluir o tema, pois existem postagens associadas"),
            @ApiResponse(responseCode = "404", description = "Tema não encontrado"),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarTema(@PathVariable Long id) {
        return temaService.deletar(id);
    }
}