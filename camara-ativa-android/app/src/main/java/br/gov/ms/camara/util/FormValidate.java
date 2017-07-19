package br.gov.ms.camara.util;

/**
 * Created by rodolfoortale on 17/03/16.
 */
public class FormValidate {
    public static boolean isValidEmail(CharSequence target) {
        return target != null && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    public static boolean isEmailInRange(String target) {
        return target.length() >= 4; // && target.length() <= 20;
    }

    // Validate password
    public static boolean isPasswordInRange(String target) {
        return target.length() >= 4; // && target.length() <= 20;
    }
}
