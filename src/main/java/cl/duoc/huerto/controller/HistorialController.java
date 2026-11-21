package cl.duoc.huerto.controller;

import cl.duoc.huerto.model.Evidencia;
import cl.duoc.huerto.model.Validacion;
import cl.duoc.huerto.service.TareaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/** Evidencias y validaciones: se crean al entregar o revisar una tarea, aqui solo se consultan. */
@RestController
public class HistorialController {

    @Autowired
    private TareaService tareaService;

    @GetMapping("/evidencias")
    public ResponseEntity<List<Evidencia>> evidencias() {
        return ResponseEntity.ok(tareaService.evidencias());
    }

    @GetMapping("/validaciones")
    public ResponseEntity<List<Validacion>> validaciones() {
        return ResponseEntity.ok(tareaService.validaciones());
    }
}
