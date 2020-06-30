package com.weatherforecast.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class UrlEncoder {

    public static String encode(String url) {
        try {
            String encodeURL = URLEncoder.encode( url, "UTF-8" );
            return encodeURL;
        } catch (UnsupportedEncodingException e) {
            return "Issue while encoding" + e.getMessage();
        }

    }
}
