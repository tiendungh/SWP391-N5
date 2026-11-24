package tool;

import java.util.regex.Pattern;

/**
 * Shared field validation for account creation/edit forms (Admin create
 * account, Manager account request, Admin manage accounts).
 */
public final class InputValidator {

    private static final Pattern PHONE_PATTERN = Pattern.compile("^0\\d{9}$");
    public static final int TEXT_MAX_LENGTH = 100;

    private InputValidator() {
    }

    public static boolean isValidPhone(String phone) {
        return phone != null && PHONE_PATTERN.matcher(phone).matches();
    }

    public static boolean isValidLength(String value) {
        return value == null || value.length() <= TEXT_MAX_LENGTH;
    }
}
