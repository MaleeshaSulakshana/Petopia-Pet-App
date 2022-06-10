package com.example.petopia.Utiles;

import java.util.concurrent.ThreadLocalRandom;

public class RandomNumber {

//    Method for generate random number
    public static int getRandomNumber()
    {
        int randomNumber = 00000000;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            randomNumber = ThreadLocalRandom.current().nextInt(100000000, 999999999);
        }

        return randomNumber;
    }

}
