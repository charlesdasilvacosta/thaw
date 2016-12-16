package fr.umlv.thaw.security;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by qbeacco on 05/12/16.
 */
public class SHA1 {

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
