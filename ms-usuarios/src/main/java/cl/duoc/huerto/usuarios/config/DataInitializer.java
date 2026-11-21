package cl.duoc.huerto.usuarios.config;

import cl.duoc.huerto.usuarios.model.Cargo;
import cl.duoc.huerto.usuarios.model.Usuario;
import cl.duoc.huerto.usuarios.repository.UsuarioRepository;
import cl.duoc.huerto.usuarios.util.Claves;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Cuentas ficticias de prueba (contrasena huerto123). Se crean solo si la
 * tabla esta vacia. Los id 1 y 2 (docente y tallerista) son los
 * responsables de los sectores de prueba de ms-huerto.
 */
@Configuration
public class DataInitializer {

    public static final String CLAVE_DEMO = "huerto123";

    @Bean
    CommandLineRunner cargarUsuariosDemo(UsuarioRepository repositorio) {
        return args -> {
            if (repositorio.count() == 0) {
                repositorio.saveAll(List.of(
                        usuario("profe.ana", "ana.rojas@escuela.cl", "Ana Rojas", Cargo.DOCENTE, "Taller de Huerto"),
                        usuario("taller.luis", "luis.pinto@escuela.cl", "Luis Pinto", Cargo.TALLERISTA, "Taller de Huerto"),
                        usuario("est.camila", "camila.soto@escuela.cl", "Camila Soto", Cargo.ESTUDIANTE, "Taller A"),
                        usuario("est.benjamin", "benjamin.diaz@escuela.cl", "Benjamín Díaz", Cargo.ESTUDIANTE, "Taller A"),
                        usuario("est.martina", "martina.rivas@escuela.cl", "Martina Rivas", Cargo.ESTUDIANTE, "Taller B")
                ));
            }
        };
    }

    private Usuario usuario(String nombreUsuario, String correo, String nombre, Cargo cargo, String taller) {
        Usuario u = new Usuario();
        u.setNombreUsuario(nombreUsuario);
        u.setCorreo(correo);
        u.setClaveHash(Claves.hash(CLAVE_DEMO));
        u.setNombre(nombre);
        u.setCargo(cargo);
        u.setRol(cargo.getRol());
        u.setTaller(taller);
        u.setActivo(true);
        return u;
    }
}
