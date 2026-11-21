package cl.duoc.huerto.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

/** Hash SHA-256 de las contrasenas (en la base de datos nunca queda la clave real). */
public final class Claves {

    private Claves() {
    }

    public static String hash(String clave) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] bytes = digest.digest(clave.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(bytes);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 no disponible", e);
        }
    }

    public static boolean coincide(String clave, String hashGuardado) {
        return clave != null && hash(clave).equals(hashGuardado);
    }
}
