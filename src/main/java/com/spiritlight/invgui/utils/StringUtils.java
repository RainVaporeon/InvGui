package com.spiritlight.invgui.utils;

import java.nio.charset.StandardCharsets;
import java.util.Random;

public class StringUtils {
    private static final char[] validChars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890".toCharArray();

    /**
     * Generates a String with given length.<br>
     * If you require alphabetic and numeric characters only, set validCharacters to true.<br>
     *
     * @param len The length of String to return
     * @param validCharacters Whether only allow {@code [a-z] [A-Z] [0-9]} as the input characters.
     * @return The random String.
     */
    public static String randomString(int len, boolean validCharacters) {
        final Random random = new Random();
        if(validCharacters) {
            char[] ret = new char[len];
            for(int i=0; i<ret.length; i++) {
                ret[i] = validChars[random.nextInt(validChars.length)];
            }
            return String.valueOf(ret);
        } else {
            byte[] bytes = new byte[len];
            random.nextBytes(bytes);
            return new String(bytes, StandardCharsets.UTF_8);
        }
    }
}
