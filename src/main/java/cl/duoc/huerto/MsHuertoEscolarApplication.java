package cl.duoc.huerto;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Microservicio unico del Huerto Escolar: usuarios, sectores, cultivos,
 * lecturas simuladas, tareas, evidencias, validaciones y reportes.
 */
@SpringBootApplication
public class MsHuertoEscolarApplication {

    public static void main(String[] args) {
        SpringApplication.run(MsHuertoEscolarApplication.class, args);
    }
}
