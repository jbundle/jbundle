package org.jbundle.app.test.manual.crypto;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SealedObject;

public class RSAProcess {

    public RSAProcess()
    {
        
    }
    public static final void main(String[] args)
    {
        RSAProcess pwd = new RSAProcess();
        pwd.store();
    }
    
    public void store()
    {
        try {
            // Get an instance of the RSA key generator
            KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
            // Generate the keys — might take sometime on slow computers
            KeyPair myPair = kpg.generateKeyPair();

            Cipher desCipher;

            // Create the cipher 
            desCipher = Cipher.getInstance("RSA");

            // Initialize the cipher for encryption
            desCipher.init(Cipher.ENCRYPT_MODE, myPair.getPublic());

            // Our cleartext
            byte[] cleartext = "This is just an example".getBytes();

            // Encrypt the cleartext
            byte[] ciphertext = desCipher.doFinal(cleartext);

            desCipher = Cipher.getInstance("RSA");
            // Initialize the same cipher for decryption
            desCipher.init(Cipher.DECRYPT_MODE, myPair.getPrivate());

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
