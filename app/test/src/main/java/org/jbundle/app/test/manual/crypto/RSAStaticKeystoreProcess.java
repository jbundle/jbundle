package org.jbundle.app.test.manual.crypto;

import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.UnrecoverableEntryException;
import java.security.cert.Certificate;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;
import java.util.Arrays;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

/**
 * Test RSA public key encryption.
 * 1. Reads public and private key from keystore which is created by executing this command:
 * keytool -genkey -keyalg RSA -keysize 1024 -alias tourgeekrsa  -keystore /usr/local/java/jdk/jre/lib/security/keystore
 *  
 * @author don
 *
 */
public class RSAStaticKeystoreProcess {

    public RSAStaticKeystoreProcess()
    {
        
    }
    public static final void main(String[] args)
    {
        RSAStaticKeystoreProcess pwd = new RSAStaticKeystoreProcess();
        pwd.store();
    }
    
    public void store()
    {
        try {
                KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());

                // get user password and file input stream
                
                char[] password = /*getPassword();*/readPasswd(System.in);

                java.io.FileInputStream fis = null;
                try {
                        fis = new java.io.FileInputStream("/usr/local/java/jdk/jre/lib/security/keystore");
                        ks.load(fis, password);
                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                } finally {
                    if (fis != null) {
                        fis.close();
                    }
                }
                
                // get my private key
                java.security.KeyStore.PasswordProtection pwd = new KeyStore.PasswordProtection(password);
                KeyStore.PrivateKeyEntry pkEntry = (KeyStore.PrivateKeyEntry)ks.getEntry("rsa", pwd);
                
                Certificate cert = pkEntry.getCertificate();
                System.out.println("cert: " + cert);
      //          System.out.println("public key: " + cert.getPublicKey());
                
                PrivateKey privateKey = pkEntry.getPrivateKey();
                System.out.println("key: " + privateKey); 
                PublicKey publicKey2 = cert.getPublicKey();
                String str2 = toHexString(publicKey2.getEncoded());
                System.out.println("public: " + str2);
                
                RSAPublicKeySpec dhSkipParamSpec = new RSAPublicKeySpec(skip1024Modulus, skip1024Base);
                KeyFactory keyFactory = KeyFactory.getInstance("RSA");
                PublicKey publicKey = keyFactory.generatePublic(dhSkipParamSpec);
                String str = toHexString(publicKey.getEncoded());
                System.out.println("public: " + str);

                // Create the cipher 
                Cipher desCipher = Cipher.getInstance("RSA");

                // Initialize the cipher for encryption
                desCipher.init(Cipher.ENCRYPT_MODE, publicKey);

                // Our cleartext
                byte[] cleartext = "This is just an example".getBytes();

                // Encrypt the cleartext
                byte[] ciphertext = desCipher.doFinal(cleartext);

                desCipher = Cipher.getInstance("RSA");
                // Initialize the same cipher for decryption
                desCipher.init(Cipher.DECRYPT_MODE, privateKey);

                byte[] cleartext1 = desCipher.doFinal(ciphertext);
                
                System.out.println("result: " + new String(cleartext1));
            } catch (KeyStoreException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (UnrecoverableEntryException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
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
        } catch (InvalidKeySpecException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    
    }
    /**
     * Reads user password from given input stream.
     */
    public char[] readPasswd(InputStream in) throws IOException {
        char[] lineBuffer;
        char[] buf;

        buf = lineBuffer = new char[128];

        int room = buf.length;
        int offset = 0;
        int c;

loop:   while (true) {
            switch (c = in.read()) {
              case -1: 
              case '\n':
                break loop;

              case '\r':
                int c2 = in.read();
                if ((c2 != '\n') && (c2 != -1)) {
                    if (!(in instanceof PushbackInputStream)) {
                        in = new PushbackInputStream(in);
                    }
                    ((PushbackInputStream)in).unread(c2);
                } else 
                    break loop;

              default:
                if (--room < 0) {
                    buf = new char[offset + 128];
                    room = buf.length - offset - 1;
                    System.arraycopy(lineBuffer, 0, buf, 0, offset);
                    Arrays.fill(lineBuffer, ' ');
                    lineBuffer = buf;
                }
                buf[offset++] = (char) c;
                break;
            }
        }

        if (offset == 0) {
            return null;
        }

        char[] ret = new char[offset];
        System.arraycopy(buf, 0, ret, 0, offset);
        Arrays.fill(buf, ' ');

        return ret;
    }
    /*
     * Converts a byte to hex digit and writes to the supplied buffer
     */
    private void byte2hex(byte b, StringBuffer buf) {
        char[] hexChars = { '0', '1', '2', '3', '4', '5', '6', '7', '8',
                            '9', 'A', 'B', 'C', 'D', 'E', 'F' };
        int high = ((b & 0xf0) >> 4);
        int low = (b & 0x0f);
        buf.append(hexChars[high]);
        buf.append(hexChars[low]);
    }

    /*
     * Converts a byte array to hex string
     */
    private String toHexString(byte[] block) {
        StringBuffer buf = new StringBuffer();

        int len = block.length;

        for (int i = 0; i < len; i++) {
             byte2hex(block[i], buf);
             if (i < len-1) {
                 buf.append(":");
             }
        } 
        return buf.toString();
    }

    private static final BigInteger skip1024Modulus
    = new BigInteger("90242639829853749672390237687730602469835068946957879215819757950415403219474497386056794085513035666391540835472767141106791638089730690793760981933262309119993395230010972486643432150940900905678345312917068395181911103105877082010079638574407308225389170936611620606089865440154690951722618401112109539323");

    // The base used with the SKIP 1024 bit modulus
    private static final BigInteger skip1024Base = BigInteger.valueOf(65537);


}
