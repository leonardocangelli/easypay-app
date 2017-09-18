package br.com.easypayapp.easypay.validation;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by joseleonardocangelli on 17/09/17.
 */

public class ValidateFields {

    public static boolean isValidEmail(String email) {
        String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    public static boolean isValidPassword(String pass) {
        if (pass != null) {
            return true;
        }
        return false;
    }

}
