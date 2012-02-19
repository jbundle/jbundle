/*
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.base.field.convert.encode;

/**
 * @(#)PercentConverter.java    0.00 12-Feb-97 Don Corley
 *
 * Copyright © 2012 tourapp.com. All Rights Reserved.
 *      don@tourgeek.com
 */

import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.jbundle.base.field.BaseField;
import org.jbundle.thin.base.db.Converter;


/**
 * Convert does a RSA public key encryption.
 * Also does an RSA private key decryption if the private key is supplied.
 *
 * @version 1.0.0
 * @author    Don Corley
 */
public class RsaEncryptedConverter extends Base64Converter
{

    /**
     * Constructor.
     */
    public RsaEncryptedConverter()
    {
        super();
    }
    /**
     * Constructor.
     * @param numberField The percent field target.
     */
    public RsaEncryptedConverter(Converter converter)
    {
        this();
        this.init(converter, true);
    }
    /**
     * Initialize this converter.
     * @param converter The next converter in the converter chain.
     * @param iFakeLength The maximum field length to return.
     */
    public void init(Converter converter, boolean bDoBase64Conversion)
    {
        super.init(converter, bDoBase64Conversion);
    }
    /**
     * Encode the byte string - OVERRIDE this.
     * @param rgbValue
     * @return
     * @throws NoSuchAlgorithmException
     */
    public byte[] encodeBytes(byte[] rgbValue)
        throws NoSuchAlgorithmException
    {
        rgbValue = this.encrypt(rgbValue);
        rgbValue = super.encodeBytes(rgbValue); // Base64 encoding
        return rgbValue;
    }
    /**
     * Decode this array.
     * @param rgbValue
     * @return
     * @throws NoSuchAlgorithmException
     */
    public byte[] decodeBytes(byte[] rgbValue)
        throws NoSuchAlgorithmException
    {
        rgbValue = super.decodeBytes(rgbValue); // Base64 encoding
        rgbValue = this.decrypt(rgbValue);
        return rgbValue;
    }

    public byte[] encrypt(byte[] rgBytes)
    {
        try {
            // ----------Public key Encryption---------------
            RSAPublicKeySpec dhSkipParamSpec = new RSAPublicKeySpec(publicKeyModulus, publicKeyBase);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PublicKey publicKey = keyFactory.generatePublic(dhSkipParamSpec);

            // Create the cipher
            Cipher desCipher = Cipher.getInstance("RSA");

            // Initialize the cipher for encryption
            desCipher.init(Cipher.ENCRYPT_MODE, publicKey);

            // Encrypt the cleartext
            rgBytes = desCipher.doFinal(rgBytes);
            
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        }
        return rgBytes;
    }
    
    public byte[] decrypt(byte[] rgBytes)
    {
        try {
            // ----------Private key Decryption---------------
            KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
            // get user password and file input stream
            char[] password = this.getPassword();
            if (password == null)
                return null;
            String keystoreLocation = this.getKeystoreLocation();
            if (keystoreLocation == null)
                return null;
            
            java.io.FileInputStream fis = null;
            try {
                fis = new java.io.FileInputStream(keystoreLocation);
                ks.load(fis, password);
            } catch (FileNotFoundException e1) {
                e1.printStackTrace();
                return null;    // No keystore = no private key
            } catch (NoSuchAlgorithmException e1) {
                e1.printStackTrace();
                return null;    // No keystore = no private key
            } catch (CertificateException e1) {
                e1.printStackTrace();
                return null;    // No keystore = no private key
            } catch (IOException e1) {
                e1.printStackTrace();
                return null;    // No keystore = no private key
            } finally {
                if (fis != null) {
                    fis.close();
                }
            }

            // get my private key
            java.security.KeyStore.PasswordProtection pwd = new KeyStore.PasswordProtection(password);
            KeyStore.PrivateKeyEntry pkEntry = (KeyStore.PrivateKeyEntry) ks.getEntry("rsa", pwd);
            PrivateKey privateKey = pkEntry.getPrivateKey();

            Cipher desCipher = Cipher.getInstance("RSA");
            // Initialize the same cipher for decryption
            desCipher.init(Cipher.DECRYPT_MODE, privateKey);

            rgBytes = desCipher.doFinal(rgBytes);
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnrecoverableEntryException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return rgBytes;
    }

    private final byte DEFAULT_PASSWORD[] = {
        (byte)0x22, (byte)0x25, (byte)0x62, (byte)0x6B, (byte)0x08,
        (byte)0x0F, (byte)0x0E, (byte)0x18};    // Just so it's not clear text (Sit right back...)
   
    private char[] getPassword()
    {
        String password = null;
        if (this.getField() != null)
            if (((BaseField)this.getField()).getRecord().getRecordOwner() != null)
                password = ((BaseField)this.getField()).getRecord().getRecordOwner().getProperty("keystorePassword");
        if (password == null)
        {
            byte[] rgBytes = DEFAULT_PASSWORD.clone();
            rgBytes = XorEncryptedConverter.encrypt(rgBytes, (byte)'E');
            password = new String(rgBytes);
        }
        return password.toCharArray();
    }
    private String getKeystoreLocation()
    {
        String keystore = null;
        if (this.getField() != null)
            if (((BaseField)this.getField()).getRecord().getRecordOwner() != null)
                keystore = ((BaseField)this.getField()).getRecord().getRecordOwner().getProperty("keystoreLocation");
        if (keystore == null)
            keystore = "/usr/local/java/jdk/jre/lib/security/keystore"; // For now
        return keystore;
    }

    private static final BigInteger publicKeyModulus = new BigInteger(
            "90242639829853749672390237687730602469835068946957879215819757950415403219474497386056794085513035666391540835472767141106791638089730690793760981933262309119993395230010972486643432150940900905678345312917068395181911103105877082010079638574407308225389170936611620606089865440154690951722618401112109539323");

    // The base used with the SKIP 1024 bit modulus
    private static final BigInteger publicKeyBase = BigInteger.valueOf(65537);

}
