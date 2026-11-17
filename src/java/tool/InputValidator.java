package tool;

import java.util.regex.Pattern;

/**
 * Shared field validation for account creation/edit forms (Admin create
 * account, Manager account request, Admin manage accounts).
 */
public final class InputValidator {

    private static final Pattern PHONE_PATTERN = Pattern.compile("^\\d{10}$");
    public static final int TEXT_MAX_LENGTH = 100;

    private InputValidator() {
    }

    /** Phone number must be exactly 10 digits, no spaces/dashes/letters. */
    public static boolean isValidPhone(String phone) {
        return phone != null && PHONE_PATTERN.matcher(phone).matches();
    }

    /** General text fields (name, address, nationality, department, position...) capped at 100 chars. */
    public static boolean isValidLength(String value) {
        return value == null || value.length() <= TEXT_MAX_LENGTH;
    }
}
