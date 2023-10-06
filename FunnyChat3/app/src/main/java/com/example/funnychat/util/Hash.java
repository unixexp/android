package com.example.funnychat.util;

import org.apache.commons.codec.digest.Crypt;

public class Hash {

    public static String getHashedString (String s) {
        return Crypt.crypt(s);
    }

    public static boolean checkHashedString (String s, String hashedPassword) {
        String tmpHashedPassword = Crypt.crypt (s, hashedPassword);
        return hashedPassword.equalsIgnoreCase(tmpHashedPassword);
    }

}
