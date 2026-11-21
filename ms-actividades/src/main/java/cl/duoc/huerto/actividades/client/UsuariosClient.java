package cl.duoc.huerto.actividades.client;

import cl.duoc.huerto.actividades.exception.ReglaNegocioException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

/**
 * Comunicacion entre microservicios: consulta a ms-usuarios el rol de una
 * persona antes de permitir crear, validar o entregar tareas.
 */
@Component
public class UsuariosClient {

    private final RestClient restClient;

    public UsuariosClient(@Value("${servicios.usuarios.url}") String urlUsuarios) {
        this.restClient = RestClient.create(urlUsuarios);
    }

    public UsuarioRemoto obtener(Long id) {
        if (id == null) {
            throw ReglaNegocioException.invalido("Falta indicar el usuario");
        }
        try {
            UsuarioRemoto usuario = restClient.get()
                    .uri("/usuarios/{id}", id)
                    .retrieve()
                    .body(UsuarioRemoto.class);
            if (usuario == null || !usuario.activo()) {
                throw ReglaNegocioException.invalido("El usuario " + id + " no está activo");
            }
            return usuario;
        } catch (RestClientResponseException e) {
            throw ReglaNegocioException.invalido("El usuario " + id + " no existe");
        } catch (ResourceAccessException e) {
            throw new ReglaNegocioException(HttpStatus.SERVICE_UNAVAILABLE,
                    "No se pudo verificar el usuario: ms-usuarios no responde");
        }
    }

    public boolean esAdministrador(Long id) {
        return "ADMINISTRADOR".equals(obtener(id).rol());
    }

    public boolean esEstudiante(Long id) {
        return "ESTUDIANTE".equals(obtener(id).rol());
    }
}
