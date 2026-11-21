package cl.duoc.huerto.actividades.controller;

import cl.duoc.huerto.actividades.dto.AccionRequest;
import cl.duoc.huerto.actividades.dto.EntregaRequest;
import cl.duoc.huerto.actividades.dto.RevisionRequest;
import cl.duoc.huerto.actividades.dto.TareaRequest;
import cl.duoc.huerto.actividades.model.Tarea;
import cl.duoc.huerto.actividades.service.TareaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tareas")
public class TareaController {

    @Autowired
    private TareaService tareaService;

    // GET /tareas  o  GET /tareas?responsableId=3
    @GetMapping
    public ResponseEntity<List<Tarea>> listar(@RequestParam(required = false) Long responsableId) {
        return ResponseEntity.ok(tareaService.listar(responsableId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Tarea> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(tareaService.buscarPorId(id));
    }

    @PostMapping
    public ResponseEntity<Tarea> crear(@RequestBody TareaRequest solicitud) {
        return ResponseEntity.status(HttpStatus.CREATED).body(tareaService.crear(solicitud));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Tarea> actualizar(@PathVariable Long id, @RequestBody TareaRequest solicitud) {
        return ResponseEntity.ok(tareaService.actualizar(id, solicitud));
    }

    // DELETE /tareas/5?usuarioId=1
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> borrar(@PathVariable Long id, @RequestParam Long usuarioId) {
        tareaService.borrar(id, usuarioId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/iniciar")
    public ResponseEntity<Tarea> iniciar(@PathVariable Long id, @RequestBody AccionRequest solicitud) {
        return ResponseEntity.ok(tareaService.iniciar(id, solicitud));
    }

    @PostMapping("/{id}/entregar")
    public ResponseEntity<Tarea> entregar(@PathVariable Long id, @RequestBody EntregaRequest solicitud) {
        return ResponseEntity.ok(tareaService.entregar(id, solicitud));
    }

    @PostMapping("/{id}/revisar")
    public ResponseEntity<Tarea> revisar(@PathVariable Long id, @RequestBody RevisionRequest solicitud) {
        return ResponseEntity.ok(tareaService.revisar(id, solicitud));
    }
}
