package com.example.ejike.smarttermometer;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by ejike on 18/11/2016.
 */

public class EncryptionMessage {

    public static String SHAhash(String password)
    {
        try {
            //The Algorithm that will digest the message is chosen
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            //The stream of bytes of the message are initialized
            md.update(password.getBytes());

            //The hashed results. A stream of bytes
            byte[] digest = md.digest();
            //A cointaner is created to contain the value. Each byte digested is being converted into hexadecimal
            StringBuffer sb = new StringBuffer();
            for (byte b : digest) {
                sb.append(String.format("%02x", b & 0xff));
            }

            return (sb.toString());
        }

        catch (NoSuchAlgorithmException e) {
            System.err.println("I'm sorry, but SHA is not a valid message digest algorithm");
        }

        return null;

    }

}
