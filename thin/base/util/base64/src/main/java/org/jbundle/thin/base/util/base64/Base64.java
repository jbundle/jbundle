/*
 * Copyright © 2011 jbundle.org. All rights reserved.
 */
package org.jbundle.thin.base.util.base64;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import biz.source_code.base64Coder.Base64Coder;

/**
 * Encode and decode strings using base64.
 * @author Don Corley <don@tourgeek.com>
 * Please don't make me write this code.
 * Thanks Christian d'Heureuse for Base64Coder at www.source-code.biz.
 */

public class Base64 {

    public static final String DEFAULT_ENCODING = "8859_1";

    /**
     * Returns the string run through the SHA hash.
     *
     * @param unencoded the string to encode
     * @return the encoded form of the unencoded string
     */
    public static byte[] encodeSHA(byte[] rgbValue)
        throws NoSuchAlgorithmException
    {
        MessageDigest md = MessageDigest.getInstance("SHA");
    
        md.update(rgbValue);
        rgbValue = md.digest();
        return rgbValue;
    }
    /**
     * Encode this string as base64.
     * @param string
     * @return
     */
    public static char[] encode(byte[] bytes)
    {
        return Base64Coder.encode(bytes);
    }
    /**
     * Encode this string as base64.
     * @param string
     * @return
     */
    @Deprecated
    public static byte[] encodeToBytes(byte[] bytes)
    {
        try {
            char[] chars = Base64.encode(bytes);
            String string = new String(chars);
            return string.getBytes(DEFAULT_ENCODING);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }
    /**
     * Encode this string to base64.
     * @param string
     * @return
     */
    @Deprecated
    public static String encode(String string)
    {
        try {
            byte[] bytes = string.getBytes(DEFAULT_ENCODING);
            char[] chars = Base64.encode(bytes);
            return new String(chars);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }
    /**
     * Decode this base64 string to a regular string.
     * @param string
     * @return
     */
    public static byte[] decode(char[] chars)
    {
        return Base64Coder.decode(chars);
    }
    /**
     * Decode this base64 string to a regular string.
     * @param string
     * @return
     */
    @Deprecated
    public static String decode(String string)
    {
        try {
            byte[] bytes = Base64.decode(string.toCharArray());
            return new String(bytes, DEFAULT_ENCODING);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }
    /**
     * Decode this base64 string to a regular string.
     * @param string
     * @return
     */
    @Deprecated
    public static byte[] decode(byte[] bytes)
    {
        try {
            String string = new String(bytes, DEFAULT_ENCODING);
            bytes = Base64.decode(string.toCharArray());
            return bytes;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }
/*
     /**
      * Encode this string as base64.
      * @param string
      * @return
      *
     public static String encode(String s)
     {
             return Base64Coder.encodeString(s);
     }
     /**
      * Encode this string as base64.
      * @param string
      * @return
      *
     public static byte[] encode(byte[] bytes)
     {
         try {
             char[] chars = Base64Coder.encode(bytes);
             String string = new String(chars);
             return string.getBytes(DEFAULT_ENCODING);
         }
         catch (UnsupportedEncodingException e) {
             e.printStackTrace();
             return null;
         }
     }
     /**
      * Decode this base64 string to a regular string.
      * @param string
      * @return
      *
     public static String decode(String string)
     {
         try {
             byte[] bytes = Base64Coder.decode(string);
             return new String(bytes, DEFAULT_ENCODING);
         }
         catch (UnsupportedEncodingException e) {
             e.printStackTrace();
             return null;
         }
     }
     /**
      * Decode this base64 string to a regular string.
      * @param string
      * @return
      *
     public static byte[] decode(byte[] bytes)
     {
         try {
             String string = new String(bytes, DEFAULT_ENCODING);
             return Base64Coder.decode(string);
         }
         catch (UnsupportedEncodingException e) {
             e.printStackTrace();
             return null;
         }
     }
*/
/*
    // Can't use sun.* in webstart
    import sun.misc.BASE64Decoder;
    import sun.misc.BASE64Encoder;
    // Shouldn't use these sun.* ... change to an open source implementation
    public static BASE64Decoder decoder = new BASE64Decoder();
    public static BASE64Encoder encoder = new BASE64Encoder();
    
    /**
     * Encode this string as base64.
     * @param string
     * @return
     *
    public static String encode(String string)
    {
        try {
            byte[] bytes = string.getBytes(DEFAULT_ENCODING);
            return encoder.encode(bytes);
        }
        catch (UnsupportedEncodingException ignored) {
            return null;
        }
    }
    /**
     * Encode this string as base64.
     * @param string
     * @return
     *
    public static byte[] encode(byte[] bytes)
    {
        try {
            String string = encoder.encode(bytes);
            return string.getBytes(DEFAULT_ENCODING);
        }
        catch (UnsupportedEncodingException ignored) {
            return null;
        }
    }
    /**
     * Decode this base64 string to a regular string.
     * @param string
     * @return
     *
    public static String decode(String string)
    {
        byte[] bytes;
        try {
            bytes = decoder.decodeBuffer(string);
            return new String(bytes, DEFAULT_ENCODING);
        } catch (IOException e) {
            return null;
        }
    }
    /**
     * Decode this base64 string to a regular string.
     * @param string
     * @return
     *
    public static byte[] decode(byte[] bytes)
    {
        try {
            String string = new String(bytes, DEFAULT_ENCODING);
            return decoder.decodeBuffer(string);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
*/
/*
import com.oreilly.servlet.Base64Encoder;

    /**
     * Encode this string to base64.
     * @param string
     * @return
     *
    public static String encode(String s)
    {
        return Base64Encoder.encodeBase64(s);
    }
    /**
     * Encode this string as base64.
     * @param string
     * @return
     *
    public static byte[] encode(byte[] b)
    {
        return Base64Encoder.encodeBase64(b);
    }
    /**
     * Decode this base64 string to a regular string.
     * @param string
     * @return
     *
    public static String decode(String s)
    {
        return Base64Decoder.decodeBase64(s);
    }
    /**
     * Decode this base64 string to a regular string.
     * @param string
     * @return
     *
    public static byte[] decode(byte[] b)
    {
        return Base64Decoder.decodeBase64(b);
    }
 */
 /*
import com.oreilly.servlet.Base64Encoder;

    /**
     * Encode this string as base64.
     * @param string
     * @return
     *
    public static char[] encode(byte[] b)
    {
        try {
            byte[] bytes = Base64Encoder.encodeBase64(b);
            String string = new String(bytes, DEFAULT_ENCODING);
            return string.toCharArray();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }
    /**
     * Encode this string as base64.
     * @param string
     * @return
     *
    @Deprecated
    public static byte[] encodeToBytes(byte[] bytes)
    {
        try {
            char[] chars = Base64.encode(bytes);
            String string = new String(chars);
            return string.getBytes(DEFAULT_ENCODING);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }
    /**
     * Encode this string to base64.
     * @param string
     * @return
     *
    @Deprecated
    public static String encode(String string)
    {
        try {
            byte[] bytes = string.getBytes(DEFAULT_ENCODING);
            char[] chars = Base64.encode(bytes);
            return new String(chars);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }
    /**
     * Decode this base64 string to a regular string.
     * @param string
     * @return
     *
    public static byte[] decode(char[] chars)
    {
        try {
            String string = new String(chars);
            byte[] bytes = string.getBytes(DEFAULT_ENCODING);
            bytes = Base64Decoder.decodeBase64(bytes);
            return bytes;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }
    /**
     * Decode this base64 string to a regular string.
     * @param string
     * @return
     *
    @Deprecated
    public static String decode(String string)
    {
        try {
            byte[] bytes = Base64.decode(string.toCharArray());
            return new String(bytes, DEFAULT_ENCODING);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }
    /**
     * Decode this base64 string to a regular string.
     * @param string
     * @return
     *
    @Deprecated
    public static byte[] decode(byte[] bytes)
    {
        try {
            String string = new String(bytes, DEFAULT_ENCODING);
            bytes = Base64.decode(string.toCharArray());
            return bytes;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }
*/
}
