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
import nilton.acelera.demo.model.Postagem;
import nilton.acelera.demo.service.PostagemService;

@RestController
@RequestMapping("/postagens")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@Tag(name = "Postagem", description = "Endpoints para gerenciamento de postagens")
@SecurityRequirement(name = "BearerAuth")
public class PostagemController {

    @Autowired
    private PostagemService postagemService;

    @Operation(summary = "Listar todas as postagens", description = "Retorna uma lista de todas as postagens.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Postagens encontradas",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Postagem.class))),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor")
    })
    @GetMapping
    public ResponseEntity<List<Postagem>> listarPostagens() {
        return ResponseEntity.ok(postagemService.listarTodas());
    }

    @Operation(summary = "Buscar postagem por ID", description = "Retorna uma postagem específica com base no seu ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Postagem encontrada",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Postagem.class))),
            @ApiResponse(responseCode = "404", description = "Postagem não encontrada"),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Postagem> buscarPorId(@PathVariable Long id) {
        return postagemService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Buscar postagens por título", description = "Retorna uma lista de postagens que contêm o título especificado (ignorando maiúsculas/minúsculas).")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Postagens encontradas",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Postagem.class))),
            @ApiResponse(responseCode = "204", description = "Nenhuma postagem encontrada com o título especificado"),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor")
    })
    @GetMapping("/titulo/{titulo}")
    public ResponseEntity<List<Postagem>> buscarPorTitulo(@PathVariable String titulo) {
        List<Postagem> postagens = postagemService.buscarPorTitulo(titulo);
        return postagens.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(postagens);
    }

    @Operation(summary = "Criar uma nova postagem", description = "Cria uma nova postagem com os dados fornecidos.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Postagem criada com sucesso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Postagem.class))),
            @ApiResponse(responseCode = "400", description = "Dados de entrada inválidos"),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor")
    })
    @PostMapping
    public ResponseEntity<Postagem> criarPostagem(@Valid @RequestBody Postagem postagem) {
        try {
            Postagem novaPostagem = postagemService.criar(postagem);
            return ResponseEntity.status(HttpStatus.CREATED).body(novaPostagem);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Operation(summary = "Atualizar uma postagem existente", description = "Atualiza os dados de uma postagem existente com base no ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Postagem atualizada com sucesso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Postagem.class))),
            @ApiResponse(responseCode = "400", description = "Dados de entrada inválidos"),
            @ApiResponse(responseCode = "404", description = "Postagem não encontrada"),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor")
    })
    @PutMapping("/{id}")
    public ResponseEntity<Postagem> atualizarPostagem(@PathVariable Long id, @Valid @RequestBody Postagem postagem) {
        try {
            return postagemService.atualizar(id, postagem)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Operation(summary = "Excluir uma postagem", description = "Exclui uma postagem com base no seu ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Postagem excluída com sucesso"),
            @ApiResponse(responseCode = "404", description = "Postagem não encontrada"),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarPostagem(@PathVariable Long id) {
        boolean deletado = postagemService.deletar(id);
        return deletado ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

    @Operation(summary = "Filtrar postagens por autor e/ou tema", description = "Retorna uma lista de postagens filtradas por ID do autor e/ou ID do tema (pelo menos um dos parâmetros deve ser fornecido).")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Postagens encontradas",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Postagem.class))),
            @ApiResponse(responseCode = "400", description = "Pelo menos um ID de autor ou tema deve ser fornecido"),
            @ApiResponse(responseCode = "204", description = "Nenhuma postagem encontrada com os critérios especificados"),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor")
    })
    @GetMapping("/filtro")
    public ResponseEntity<List<Postagem>> filtrarPostagens(
            @Parameter(description = "ID do autor para filtrar") @RequestParam(required = false) Long usuarioId,
            @Parameter(description = "ID do tema para filtrar") @RequestParam(required = false) Long temaId) {

        if (usuarioId == null && temaId == null) {
            return ResponseEntity.badRequest().build();
        }

        List<Postagem> postagens = null;

        if (usuarioId != null && temaId != null) {
            postagens = postagemService.buscarPorUsuarioIdETemaId(usuarioId, temaId);
        } else if (usuarioId != null) {
            postagens = postagemService.buscarPorUsuarioId(usuarioId);
        } else if (temaId != null) {
            postagens = postagemService.buscarPorTemaId(temaId);
        }

        return postagens != null && !postagens.isEmpty() ? ResponseEntity.ok(postagens) : ResponseEntity.noContent().build();
    }
}