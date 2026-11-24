package tool;

import java.security.MessageDigest;
import java.util.Base64;

public class EncodePassword {
    private static final String SALT = "vuthienkhiemhahahahahaqwertyuiop123456";

    public static String encodePasswordbyHash(String password) {
        try {
            byte[] data = (password + SALT).getBytes("UTF-8");
            return Base64.getEncoder().encodeToString(
                MessageDigest.getInstance("SHA-1").digest(data)
            );
        } catch (Exception e) {
            return null;
        }
    }
    // Inside EncodePassword.java
public static void main(String[] args) {
    String hash = encodePasswordbyHash("123456");
    System.out.println("Hash for '123456': " + hash);
}
}
