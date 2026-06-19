package com.url.shortner.util;

public class Base62Encoder {
    private static final String CHARSET =
            "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

    public static String encode(long number) {

        if (number == 0) {
            return "a";
        }

        StringBuilder encoded = new StringBuilder();

        while (number > 0) {
            int remainder = (int) (number % 62);
            encoded.append(CHARSET.charAt(remainder));
            number /= 62;
        }

        return encoded.reverse().toString();
    }
}
