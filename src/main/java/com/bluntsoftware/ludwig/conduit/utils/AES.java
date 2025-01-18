package com.bluntsoftware.ludwig.conduit.utils;

import org.apache.tomcat.util.codec.binary.Base64;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class AES {
    private static SecretKeySpec getKeySpec(String myKey){
        try {
            byte[] key = myKey.getBytes(StandardCharsets.UTF_8);
            MessageDigest sha = MessageDigest.getInstance("SHA-1");
            key = sha.digest(key);
            key = Arrays.copyOf(key, 16); // use only first 128 bit
            return new SecretKeySpec(key, "AES");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String encrypt(String strToEncrypt,SecretKeySpec secretKey) {
        try {
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            return Base64.encodeBase64String(cipher.doFinal(strToEncrypt.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception e) {
            System.out.println("Error while encrypting: "+e.toString());
        }
        return null;
    }
    public static String decrypt(String strToDecrypt,SecretKeySpec secretKey) {
        try{
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5PADDING");
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            return new String(cipher.doFinal(Base64.decodeBase64(strToDecrypt)));
        } catch (Exception e) {
            System.out.println("Error while decrypting: "+e.toString());
        }
        return null;
    }
    public static String encrypt(String strClearText,String key) {
        return AES.encrypt(strClearText,getKeySpec(key));
    }

    public static String decrypt(String secret,String key)  {
        return AES.decrypt(secret,getKeySpec(key));
    }

}

