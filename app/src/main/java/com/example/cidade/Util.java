package com.example.cidade;

import java.sql.Timestamp;

public class Util {

    public static long getTimeStamp(){
        return new Timestamp(System.currentTimeMillis()).getTime();
    }
}
