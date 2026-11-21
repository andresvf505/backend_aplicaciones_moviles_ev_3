package cl.duoc.huerto.service;

import cl.duoc.huerto.exception.ReglaNegocioException;
import cl.duoc.huerto.model.Rol;
import cl.duoc.huerto.model.Usuario;
import cl.duoc.huerto.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Revisa el rol de quien crea, entrega o valida una tarea, o reporta un problema.
 *
 * Antes, cuando el backend estaba dividido en tres microservicios, esto se
 * consultaba por HTTP a ms-usuarios (RestClient). Ahora los usuarios viven en
 * la misma base de datos, asi que se lee directo desde el repositorio: es mas
 * simple y ya no falla si otro servicio esta apagado.
 */
@Service
public class PermisoService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    /** Devuelve el usuario si existe y esta activo; si no, informa el error. */
    public Usuario obtenerActivo(Long id) {
        if (id == null) {
            throw ReglaNegocioException.invalido("Falta indicar el usuario");
        }
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> ReglaNegocioException.invalido("El usuario " + id + " no existe"));
        if (!usuario.isActivo()) {
            throw ReglaNegocioException.invalido("El usuario " + id + " no está activo");
        }
        return usuario;
    }

    public boolean esAdministrador(Long id) {
        return obtenerActivo(id).getRol() == Rol.ADMINISTRADOR;
    }

    public boolean esEstudiante(Long id) {
        return obtenerActivo(id).getRol() == Rol.ESTUDIANTE;
    }
}
