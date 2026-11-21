package cl.duoc.huerto.huerto.controller;

import cl.duoc.huerto.huerto.dto.CultivoRequest;
import cl.duoc.huerto.huerto.dto.EstadoRequest;
import cl.duoc.huerto.huerto.model.Cultivo;
import cl.duoc.huerto.huerto.service.CultivoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cultivos")
public class CultivoController {

    @Autowired
    private CultivoService cultivoService;

    // GET /cultivos  o  GET /cultivos?sectorId=1
    @GetMapping
    public ResponseEntity<List<Cultivo>> listar(@RequestParam(required = false) Long sectorId) {
        return ResponseEntity.ok(cultivoService.listar(sectorId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Cultivo> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(cultivoService.buscarPorId(id));
    }

    @PostMapping
    public ResponseEntity<Cultivo> crear(@RequestBody CultivoRequest solicitud) {
        return ResponseEntity.status(HttpStatus.CREATED).body(cultivoService.crear(solicitud));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Cultivo> actualizar(@PathVariable Long id, @RequestBody CultivoRequest solicitud) {
        return ResponseEntity.ok(cultivoService.actualizar(id, solicitud));
    }

    @PutMapping("/{id}/estado")
    public ResponseEntity<Cultivo> cambiarEstado(@PathVariable Long id, @RequestBody EstadoRequest solicitud) {
        return ResponseEntity.ok(cultivoService.cambiarEstado(id, solicitud));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> borrar(@PathVariable Long id) {
        cultivoService.borrar(id);
        return ResponseEntity.noContent().build();
    }
}
