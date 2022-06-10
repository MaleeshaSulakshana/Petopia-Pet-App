package com.example.petopia.Utiles;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateTimeGenerator {

//    Method for get date and time
    public static String getDateTime()
    {
        String dateTime = "";
        dateTime = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault()).format(new Date());
        return dateTime;
    }

}
