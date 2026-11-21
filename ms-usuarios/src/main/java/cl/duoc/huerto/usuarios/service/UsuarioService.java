package cl.duoc.huerto.usuarios.service;

import cl.duoc.huerto.usuarios.dto.LoginRequest;
import cl.duoc.huerto.usuarios.dto.PerfilRequest;
import cl.duoc.huerto.usuarios.dto.RegistroRequest;
import cl.duoc.huerto.usuarios.dto.UsuarioResponse;
import cl.duoc.huerto.usuarios.exception.ReglaNegocioException;
import cl.duoc.huerto.usuarios.model.Cargo;
import cl.duoc.huerto.usuarios.model.Rol;
import cl.duoc.huerto.usuarios.model.Usuario;
import cl.duoc.huerto.usuarios.repository.UsuarioRepository;
import cl.duoc.huerto.usuarios.util.Claves;
import cl.duoc.huerto.usuarios.util.Validar;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

/** Reglas de negocio de las cuentas de usuario. */
@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    // 1. Listar todos
    public List<UsuarioResponse> listar() {
        return usuarioRepository.findAll().stream().map(UsuarioResponse::desde).toList();
    }

    // 2. Buscar por ID
    public UsuarioResponse buscarPorId(Long id) {
        return UsuarioResponse.desde(obtener(id));
    }

    // 3. Registrar (crear cuenta)
    public UsuarioResponse registrar(RegistroRequest solicitud) {
        String nombreUsuario = ValidadorUsuario.nombreUsuario(solicitud.nombreUsuario());
        String correo = ValidadorUsuario.correo(solicitud.correo());
        Cargo cargo = Validar.enumeracion(Cargo.class, solicitud.cargo(), "El cargo");

        if (cargo.getRol() == Rol.ADMINISTRADOR
                && !ValidadorUsuario.CODIGO_AUTORIZACION_ADMIN.equals(
                solicitud.codigoAutorizacion() == null ? "" : solicitud.codigoAutorizacion().trim())) {
            throw ReglaNegocioException.prohibido("Código de autorización incorrecto");
        }
        if (usuarioRepository.existsByNombreUsuario(nombreUsuario)) {
            throw ReglaNegocioException.conflicto("Ese nombre de usuario ya está en uso");
        }
        if (usuarioRepository.existsByCorreoIgnoreCase(correo)) {
            throw ReglaNegocioException.conflicto("Ese correo ya está registrado");
        }

        Usuario usuario = new Usuario();
        usuario.setNombreUsuario(nombreUsuario);
        usuario.setCorreo(correo);
        usuario.setClaveHash(Claves.hash(ValidadorUsuario.clave(solicitud.clave())));
        usuario.setNombre(ValidadorUsuario.nombre(solicitud.nombre()));
        usuario.setCargo(cargo);
        usuario.setRol(cargo.getRol());
        usuario.setTaller(ValidadorUsuario.taller(solicitud.taller()));
        usuario.setActivo(true);
        return UsuarioResponse.desde(usuarioRepository.save(usuario));
    }

    // 4. Iniciar sesion con usuario o correo
    public UsuarioResponse login(LoginRequest solicitud) {
        String identificador = solicitud.identificador() == null ? "" : solicitud.identificador().trim().toLowerCase();
        Usuario usuario = usuarioRepository.findByNombreUsuario(identificador)
                .or(() -> usuarioRepository.findByCorreoIgnoreCase(identificador))
                .filter(Usuario::isActivo)
                .filter(u -> Claves.coincide(solicitud.clave(), u.getClaveHash()))
                .orElseThrow(() -> new ReglaNegocioException(HttpStatus.UNAUTHORIZED,
                        "Usuario, correo o contraseña incorrectos"));
        return UsuarioResponse.desde(usuario);
    }

    // 5. Actualizar perfil (nombre, correo, taller y opcionalmente la clave)
    public UsuarioResponse actualizarPerfil(Long id, PerfilRequest solicitud) {
        Usuario usuario = obtener(id);
        String correo = ValidadorUsuario.correo(solicitud.correo());
        if (usuarioRepository.existsByCorreoIgnoreCaseAndIdNot(correo, id)) {
            throw ReglaNegocioException.conflicto("Ese correo ya está registrado por otra cuenta");
        }
        String claveNueva = solicitud.claveNueva() == null ? "" : solicitud.claveNueva();
        if (!claveNueva.isEmpty()) {
            if (!Claves.coincide(solicitud.claveActual(), usuario.getClaveHash())) {
                throw ReglaNegocioException.invalido("La contraseña actual no es correcta");
            }
            usuario.setClaveHash(Claves.hash(ValidadorUsuario.clave(claveNueva)));
        }
        usuario.setNombre(ValidadorUsuario.nombre(solicitud.nombre()));
        usuario.setCorreo(correo);
        usuario.setTaller(ValidadorUsuario.taller(solicitud.taller()));
        return UsuarioResponse.desde(usuarioRepository.save(usuario));
    }

    // 6. Borrar
    public void borrar(Long id) {
        usuarioRepository.delete(obtener(id));
    }

    private Usuario obtener(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> ReglaNegocioException.noEncontrado("El usuario " + id + " no existe"));
    }
}
