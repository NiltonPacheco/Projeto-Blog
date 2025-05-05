package nilton.acelera.demo.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import nilton.acelera.demo.model.Tema;
import nilton.acelera.demo.repository.TemaRepository;

@RestController
@RequestMapping("/temas")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class TemaController {

    @Autowired
    private TemaRepository temaRepository;

    @GetMapping
    public ResponseEntity<List<Tema>> listarTemas() {
        return ResponseEntity.ok(temaRepository.findAll());
    }
 //.
    @GetMapping("/{id}")
    public ResponseEntity<Tema> buscarPorId(@PathVariable Long id) {
        return temaRepository.findById(id)
                .map(resposta -> ResponseEntity.ok(resposta))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Tema> criarTema(@RequestBody Tema tema) {
        return ResponseEntity.status(HttpStatus.CREATED).body(temaRepository.save(tema));
    }

    @PutMapping
    public ResponseEntity<Tema> atualizarTema(@RequestBody Tema tema) {
        return ResponseEntity.ok(temaRepository.save(tema));
    }

    @DeleteMapping("/{id}")
    public void deletarTema(@PathVariable Long id) {
        temaRepository.deleteById(id);
    }
}
