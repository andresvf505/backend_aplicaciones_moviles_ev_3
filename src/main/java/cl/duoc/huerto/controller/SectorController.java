package cl.duoc.huerto.controller;

import cl.duoc.huerto.dto.SectorRequest;
import cl.duoc.huerto.model.Sector;
import cl.duoc.huerto.service.SectorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/sectores")
public class SectorController {

    @Autowired
    private SectorService sectorService;

    @GetMapping
    public ResponseEntity<List<Sector>> listar() {
        return ResponseEntity.ok(sectorService.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Sector> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(sectorService.buscarPorId(id));
    }

    @PostMapping
    public ResponseEntity<Sector> crear(@RequestBody SectorRequest solicitud) {
        return ResponseEntity.status(HttpStatus.CREATED).body(sectorService.crear(solicitud));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Sector> actualizar(@PathVariable Long id, @RequestBody SectorRequest solicitud) {
        return ResponseEntity.ok(sectorService.actualizar(id, solicitud));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> borrar(@PathVariable Long id) {
        sectorService.borrar(id);
        return ResponseEntity.noContent().build();
    }
}
