package fr.umlv.thaw.security;

import java.util.Random;

/**
 * Created by Quentin Béacco and Charles Da Silva Costa
 * Thaw Project M1 Informatique
 */
public class RandonString {

    /**
     * Static method for have random string, use for token cause must be uniq for users
     * @return random string
     */
    public static String randomString() {
        char[] chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789_-".toCharArray();
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < 20; i++) {
            char c = chars[random.nextInt(chars.length)];
            sb.append(c);
        }
        return sb.toString();
    }
}
