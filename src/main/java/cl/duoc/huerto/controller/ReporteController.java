package cl.duoc.huerto.controller;

import cl.duoc.huerto.dto.EstadoReporteRequest;
import cl.duoc.huerto.dto.ReporteRequest;
import cl.duoc.huerto.model.ReporteProblema;
import cl.duoc.huerto.service.ReporteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/reportes")
public class ReporteController {

    @Autowired
    private ReporteService reporteService;

    @GetMapping
    public ResponseEntity<List<ReporteProblema>> listar() {
        return ResponseEntity.ok(reporteService.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReporteProblema> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(reporteService.buscarPorId(id));
    }

    @PostMapping
    public ResponseEntity<ReporteProblema> registrar(@RequestBody ReporteRequest solicitud) {
        return ResponseEntity.status(HttpStatus.CREATED).body(reporteService.registrar(solicitud));
    }

    @PutMapping("/{id}/estado")
    public ResponseEntity<ReporteProblema> actualizarEstado(@PathVariable Long id,
                                                            @RequestBody EstadoReporteRequest solicitud) {
        return ResponseEntity.ok(reporteService.actualizarEstado(id, solicitud));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> borrar(@PathVariable Long id) {
        reporteService.borrar(id);
        return ResponseEntity.noContent().build();
    }
}
