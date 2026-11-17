package tool;

import java.security.MessageDigest;
import java.security.SecureRandom;

/**
 * Generates a random temporary password for accounts created by an Admin,
 * plus the activation token used in the emailed link. The candidate is
 * expected to log in once with the temp password and (ideally) change it -
 * there is currently no "change password" screen in this project.
 */
public class PasswordGenerator {

    private static final String UPPER = "ABCDEFGHJKLMNPQRSTUVWXYZ"; // no I/O to avoid confusion
    private static final String LOWER = "abcdefghijkmnpqrstuvwxyz";
    private static final String DIGITS = "23456789"; // no 0/1 to avoid confusion
    private static final String SYMBOLS = "!@#$%*?";
    private static final String ALL = UPPER + LOWER + DIGITS + SYMBOLS;

    private static final SecureRandom RNG = new SecureRandom();

    /** Random password (min 8 chars), guaranteed at least one upper/lower/digit/symbol. */
    public static String generate(int length) {
        int len = Math.max(8, length);
        char[] pwd = new char[len];

        pwd[0] = UPPER.charAt(RNG.nextInt(UPPER.length()));
        pwd[1] = LOWER.charAt(RNG.nextInt(LOWER.length()));
        pwd[2] = DIGITS.charAt(RNG.nextInt(DIGITS.length()));
        pwd[3] = SYMBOLS.charAt(RNG.nextInt(SYMBOLS.length()));
        for (int i = 4; i < len; i++) {
            pwd[i] = ALL.charAt(RNG.nextInt(ALL.length()));
        }

        for (int i = pwd.length - 1; i > 0; i--) {
            int j = RNG.nextInt(i + 1);
            char tmp = pwd[i];
            pwd[i] = pwd[j];
            pwd[j] = tmp;
        }

        return new String(pwd);
    }

    /** Random raw token to put in the activation link. Never stored as-is - see hashToken(). */
    public static String generateRawToken() {
        byte[] bytes = new byte[24];
        RNG.nextBytes(bytes);
        return toHex(bytes);
    }

    /** SHA-256 hex hash of the raw token. This is what gets stored in
     *  PasswordResetToken.TokenHash, so a DB leak alone can't be replayed as a
     *  working activation link. */
    public static String hashToken(String rawToken) {
        if (rawToken == null) return null;
        try {
            byte[] digest = MessageDigest.getInstance("SHA-256")
                    .digest(rawToken.getBytes("UTF-8"));
            return toHex(digest);
        } catch (Exception e) {
            return null;
        }
    }

    private static String toHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}
