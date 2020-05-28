package net.tospay.auth.utils;

import androidx.annotation.NonNull;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Used for Validating Email Address
 */
public class EmailValidator {

    private EmailValidator() {
        throw new IllegalStateException("EmailValidator class");
    }

    /**
     * This method validates the mEmail address String and returns true if pattern matches.
     *
     * @param email String for Validation.
     * @return boolean value
     */
    public static boolean isEmailValid(@NonNull String email) {
        final String EMAIL_PATTERN = "^([A-Za-z0-9._&'+!#$()*?/<~`%^=}|:\";,>{\\[\\]\\-]+(\\" +
                ".[A-Za-z0-9" +
                "._&'+!#$()*?/<~`%^=}|:\";,>{\\[\\]\\-]+)*){3}@[A-Za-z0-9-]+(\\" +
                ".[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    /**
     * This method checks whether email contains @ and dot(.).
     *
     * @param str string for validation.
     * @return boolean value.
     */
    public static boolean startValidatingMail(String str) {
        int lastAtPos;
        int lastDotPos;
        if (str.contains("@") && str.contains(".")) {
            lastAtPos = str.lastIndexOf('@');
            lastDotPos = str.lastIndexOf('.');
            return (lastDotPos > lastAtPos + 2);
        } else {
            return false;
        }
    }
}
