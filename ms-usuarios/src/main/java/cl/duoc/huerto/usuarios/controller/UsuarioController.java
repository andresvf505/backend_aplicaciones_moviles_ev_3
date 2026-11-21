package cl.duoc.huerto.usuarios.controller;

import cl.duoc.huerto.usuarios.dto.LoginRequest;
import cl.duoc.huerto.usuarios.dto.PerfilRequest;
import cl.duoc.huerto.usuarios.dto.RegistroRequest;
import cl.duoc.huerto.usuarios.dto.UsuarioResponse;
import cl.duoc.huerto.usuarios.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    // GET /usuarios : listar todos
    @GetMapping
    public ResponseEntity<List<UsuarioResponse>> listar() {
        return ResponseEntity.ok(usuarioService.listar());
    }

    // GET /usuarios/{id} : buscar por id
    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponse> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(usuarioService.buscarPorId(id));
    }

    // POST /usuarios : registrar una cuenta nueva
    @PostMapping
    public ResponseEntity<UsuarioResponse> registrar(@RequestBody RegistroRequest solicitud) {
        return ResponseEntity.status(HttpStatus.CREATED).body(usuarioService.registrar(solicitud));
    }

    // POST /usuarios/login : iniciar sesion
    @PostMapping("/login")
    public ResponseEntity<UsuarioResponse> login(@RequestBody LoginRequest solicitud) {
        return ResponseEntity.ok(usuarioService.login(solicitud));
    }

    // PUT /usuarios/{id} : actualizar perfil
    @PutMapping("/{id}")
    public ResponseEntity<UsuarioResponse> actualizar(@PathVariable Long id, @RequestBody PerfilRequest solicitud) {
        return ResponseEntity.ok(usuarioService.actualizarPerfil(id, solicitud));
    }

    // DELETE /usuarios/{id} : borrar
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> borrar(@PathVariable Long id) {
        usuarioService.borrar(id);
        return ResponseEntity.noContent().build();
    }
}
