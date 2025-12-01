package br.com.evento.controller;
import br.com.evento.dto.*;
import br.com.evento.service.EventoService;
import org.bson.Document;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/eventos")
public class EventoController {

    private final EventoService service;

    public EventoController(EventoService service) {
        this.service = service;
    }

    // 1. Criar Evento
    @PostMapping
    public ResponseEntity<String> criarEvento(@RequestBody EventoDTO dto) {
        service.criarEvento(dto);
        return ResponseEntity.ok("Evento criado com sucesso!");
    }

    // 2. Criar Pessoa
    @PostMapping("/pessoas")
    public ResponseEntity<String> criarPessoa(@RequestBody PessoaDTO dto) {
        service.criarPessoa(dto);
        return ResponseEntity.ok("Pessoa cadastrada!");
    }

    // 3. Vincular Pessoa ao Evento
    @PostMapping("/{id}/participacao")
    public ResponseEntity<String> adicionarParticipacao(
            @PathVariable String id, 
            @RequestBody VinculoDTO dto) {
        // dto.tipo deve ser "PARTICIPOU" ou "ORGANIZOU"
        service.registrarParticipacao(dto.email(), id, dto.tipo());
        return ResponseEntity.ok("Vínculo " + dto.tipo() + " registrado!");
    }

    // 4. Migrar para Organizador (Requisito Específico)
    @PutMapping("/{id}/promover")
    public ResponseEntity<String> promoverOrganizador(
            @PathVariable String id, 
            @RequestParam String email) {
        service.registrarParticipacao(email, id, "ORGANIZOU");
        return ResponseEntity.ok("Usuário promovido a organizador!");
    }

    // 5. Consultar Neo4j (Quem participa/organiza)
    @GetMapping("/{id}/rede")
    public ResponseEntity<List<Map<String, Object>>> consultarRede(@PathVariable String id) {
        return ResponseEntity.ok(service.listarRedeEvento(id));
    }

    // 6. Filtros Dinâmicos Mongo
    @GetMapping("/buscar")
    public ResponseEntity<List<Document>> buscarEventos(
            @RequestParam String criterio, 
            @RequestParam String valor) {
        return ResponseEntity.ok(service.buscarEventos(criterio, valor));
    }

    // 7. Exportar SQL
    @GetMapping(value = "/exportar/sql", produces = "application/sql")
    public ResponseEntity<String> exportarSql() {
        return ResponseEntity.ok(service.gerarSQL());
    }

    // 8. Exportar JSON
    @GetMapping(value = "/exportar/json", produces = "application/json")
    public ResponseEntity<String> exportarJson() {
        return ResponseEntity.ok(service.gerarJSON());
    }
}