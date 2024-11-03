package com.jygoh.anytime.global.security.utils;

import org.springframework.beans.factory.annotation.Value;

public class EncodeDecode {

    @Value("${base62.chars}")
    private static String BASE62_CHARS;
    private static final int BASE = BASE62_CHARS.length();

    public static String encode(Long id) {
        StringBuilder result = new StringBuilder();
        while (id > 0) {
            result.append(BASE62_CHARS.charAt((int) (id % BASE)));
            id /= BASE;
        }

        return result.reverse().toString();
    }

    public static Long decode(String str) {
        long result = 0;
        for (char c : str.toCharArray()) {
            result = result * BASE + BASE62_CHARS.indexOf(c);
        }
        return result;
    }
}
