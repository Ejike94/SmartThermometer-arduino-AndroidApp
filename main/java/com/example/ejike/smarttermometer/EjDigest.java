package com.example.ejike.smarttermometer;

import static com.example.ejike.smarttermometer.AES.*;
/**
 * Created by ejike on 26/01/2017.
 */

public class EjDigest {

    public static int keyN = 3;

    //Method that handles the creation of the key, concatenation of bits + the digest
    public static String keyDigestIn(String password ){


        //Creation of the key
        keyN++;
        int key;
        if (keyN>15)
            key = 3;
        key = keyN^55;// XORING keyN with
        int g = Integer.parseInt(password);
        g <<= 7;
        g|=key;
        setKey("r!r".trim());
        encrypt(Integer.toString(g).trim());

        return (getEncryptedString());
    }

    //Method that handles the lightweight decoding of the data
    public static String keyDigestOut(String data){

        keyN++; // Creation of the Key
        int key;
        if (keyN>15)
            key = 3;
        key = keyN^55;// Xoring the key

        /*setKey("r!r".trim()); //Key Sent to decrypt data
        decrypt(data); // Decryption process of the data*/

       try{ //Try if value is integer
           int g = Integer.parseInt(data);
           int b = g&63;//Extraction of the key
           if(b != key)//If key is different, data is being destroyed
               g = 0; // data is being destroyed.
           else
               g>>=7;//Data is being normalized to be recognized
           return (Integer.toString(g));
       }//Recieving String from AES class, and then convert it into an integer
        catch(NumberFormatException e){//If value not integer, return zero
            int g = 0;
            return (Integer.toString(g));
        }


    }
}
