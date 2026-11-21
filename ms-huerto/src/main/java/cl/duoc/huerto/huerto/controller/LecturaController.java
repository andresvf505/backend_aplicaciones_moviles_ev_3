package cl.duoc.huerto.huerto.controller;

import cl.duoc.huerto.huerto.model.LecturaSensor;
import cl.duoc.huerto.huerto.service.LecturaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/lecturas")
public class LecturaController {

    @Autowired
    private LecturaService lecturaService;

    @GetMapping
    public ResponseEntity<List<LecturaSensor>> listar() {
        return ResponseEntity.ok(lecturaService.listar());
    }

    // POST /lecturas/simular : crea una lectura para los sectores sin lecturas
    @PostMapping("/simular")
    public ResponseEntity<List<LecturaSensor>> simular() {
        return ResponseEntity.ok(lecturaService.simularFaltantes());
    }
}
