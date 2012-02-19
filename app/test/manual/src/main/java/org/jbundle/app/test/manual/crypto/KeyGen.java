/*
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.app.test.manual.crypto;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

public class KeyGen {
    
    public KeyGen()
    {
        
    }
    
    public static final void main(String[] args)
    {
        try {
            KeyGenerator keygen = KeyGenerator.getInstance("DES");
            SecretKey desKey = keygen.generateKey();
            System.out.println("key: " + new String(desKey.getEncoded()).length());

            Cipher desCipher;

            // Create the cipher 
            desCipher = Cipher.getInstance("DES/ECB/PKCS5Padding");


            // Initialize the cipher for encryption
            desCipher.init(Cipher.ENCRYPT_MODE, desKey);

            // Our cleartext
            byte[] cleartext = "This is just an example".getBytes();

            // Encrypt the cleartext
            byte[] ciphertext = desCipher.doFinal(cleartext);

            // Initialize the same cipher for decryption
            desCipher.init(Cipher.DECRYPT_MODE, desKey);

            // Decrypt the ciphertext
            byte[] cleartext1 = desCipher.doFinal(ciphertext);
            
            System.out.println("result: " + new String(cleartext1));
        } catch (InvalidKeyException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (BadPaddingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
