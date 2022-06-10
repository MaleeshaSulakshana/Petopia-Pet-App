package com.example.petopia.Utiles;

import android.util.Patterns;

public class EmailValidator {

    public static boolean isValidEmail(CharSequence target) {
        return Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

}
