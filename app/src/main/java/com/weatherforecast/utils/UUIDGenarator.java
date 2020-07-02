package com.weatherforecast.utils;

import java.util.UUID;

public class UUIDGenarator {

    public static String randomUUID(int num) {
        UUID uid = null;
        for (int i = 1; i < num; i++) {

            /***** Generating Random UUID's *****/
            uid = UUID.randomUUID();
        }
        return uid.toString();
    }
}
