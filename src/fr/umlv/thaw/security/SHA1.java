package fr.umlv.thaw.security;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by Quentin Béacco and Charles Dasilva Costa
 * Thaw Project M1 Informatique
 */
public class SHA1 {

    /**
     * Static method to hash password with SHA-1
     * @param password the clear password
     * @return the hashed password
     */
    public static String hash(String password) {
        byte[] result = new byte[0];
        try {
            result = MessageDigest.getInstance("SHA-1").digest(password.getBytes());
        } catch (NoSuchAlgorithmException e) {
            throw new NullPointerException("Can't retrieve algorithm");
        }
        StringBuffer stringBuffer = new StringBuffer();

        for(int i = 0; i < result.length; i++){
            stringBuffer.append(Integer.toString((result[i] & 0xff) + 0x100, 16).substring(1));
        }

        return stringBuffer.toString();
    }
}
