package com.jygoh.anytime.global.security.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class EncodeDecode {

    @Value("${base62.chars}")
    private String BASE62_CHARS;

    private int getBase() {
        return BASE62_CHARS.length();
    }

    public String encode(Long id) {
        if (id == null || id < 0) {
            throw new IllegalArgumentException("ID must be a non-negative number.");
        }

        if (id == 0) {
            return String.valueOf(BASE62_CHARS.charAt(0)); // id가 0일 때는 첫 번째 문자 반환
        }

        StringBuilder result = new StringBuilder();
        int base = getBase(); // BASE 값을 메서드 호출로 가져옵니다.

        while (id > 0) {
            result.append(BASE62_CHARS.charAt((int) (id % base)));
            id /= base;
        }

        return result.reverse().toString();
    }

    public Long decode(String str) {
        if (str == null || str.isEmpty()) {
            throw new IllegalArgumentException("Input string cannot be null or empty.");
        }

        long result = 0;
        int base = getBase(); // BASE 값을 메서드 호출로 가져옵니다.

        for (char c : str.toCharArray()) {
            int index = BASE62_CHARS.indexOf(c);
            if (index < 0) {
                throw new IllegalArgumentException("Invalid character found in the input string: " + c);
            }
            result = result * base + index;
        }

        return result;
    }
}
