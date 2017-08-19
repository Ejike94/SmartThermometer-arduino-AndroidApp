package com.example.ejike.smarttermometer;

/**
 * Created by ejike on 26/01/2017.
 */
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import android.util.Base64;


public class AES {

        private static SecretKeySpec secretKey ;
        private static byte[] key ;

        private static String decryptedString;
        private static String encryptedString;

        public static void setKey(String myKey){


           // MessageDigest sha = null;
            try {
                char pad = 0x029;
                if(myKey.length() % 16 != 0) {
                    int missingLength = 16 - (myKey.length() % 16);
                    for (int i = 0; i < missingLength; i++) {
                        myKey += pad;
                    }
                }
                key = myKey.getBytes("UTF-8");
                System.out.println(key.length);
              /*  sha = MessageDigest.getInstance("SHA-1");
                key = sha.digest(key);*/
                key = Arrays.copyOf(key, 16); // use only first 128 bit
                System.out.println(key.length);
                System.out.println(new String(key,"UTF-8"));
                secretKey = new SecretKeySpec(key, "AES");


            } /*catch (NoSuchAlgorithmException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } */

            catch (UnsupportedEncodingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }



        }

        public static String getDecryptedString() {
            return decryptedString;
        }
        public static void setDecryptedString(String decryptedString) {
            AES.decryptedString = decryptedString;
        }
        public static String getEncryptedString() {
            return encryptedString;
        }
        public static void setEncryptedString(String encryptedString) {
            AES.encryptedString = encryptedString;
        }
        public static String encrypt(String strToEncrypt)
        {
            try
            {
                Cipher cipher = Cipher.getInstance("AES/ECB/NoPadding");

                cipher.init(Cipher.ENCRYPT_MODE, secretKey);
                char pad = 0x029;
                if(strToEncrypt.length() % 16 != 0) {
                    int missingLength = 16 - (strToEncrypt.length() % 16);
                    for (int i = 0; i < missingLength; i++) {
                        strToEncrypt += pad;
                    }
                }
                setEncryptedString(Base64.encodeToString(cipher.doFinal(strToEncrypt.getBytes("UTF-8")),Base64.DEFAULT));
                System.out.println(encryptedString);


            }
            catch (Exception e)
            {

                System.out.println("Error while encrypting: "+e.toString());
            }
            return null;
        }
        public static String decrypt(String strToDecrypt)
        {
            try
            {
                Cipher cipher = Cipher.getInstance("AES/ECB/NoPadding");

                cipher.init(Cipher.DECRYPT_MODE, secretKey);

                 setDecryptedString(new String(cipher.doFinal(Base64.decode(strToDecrypt, Base64.DEFAULT))));

            }
            catch (Exception e)
            {

                System.out.println("Error while decrypting: "+e.toString());
            }
            return null;
        }
}
