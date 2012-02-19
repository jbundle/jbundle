/*
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.base.model;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Hashtable;
import java.util.Map;

/**
 * @(#)TableException.java  1.16 95/12/14 Don Corley
 *
 * Copyright © 2012 tourapp.com. All Rights Reserved.
 *      don@tourgeek.com
 */

/**
 * Debug utilities (comment out in production version).
 */
public class Debug extends Object
{
    protected static boolean gbFirstTime = true;
    
    /**
     * Constructor for standard errors.
     */
    public Debug(int iError)
    {
        super();    // Get the error text, or make it up
    }
    public static int giEnableLog4J = -1;   // 0 = no; -1 = yes
    /**
     * Print error.
     */
    public static void pl(String strError, String strDebugType, int iPriority)
    {
        if (giEnableLog4J == -1)
        { // First time
            giEnableLog4J = 1;  // Yes
//            try   {
  //              Class c = Class.forName("org.apache.log4j.Category"); // Test if this exists
    //        } catch (Exception ex)  {
      //          giEnableLog4J = 0;  // No
        //    }
        }
        if (giEnableLog4J == 0)
            return;
        DebugLogger.pl(strError, strDebugType, iPriority);
    }
    /**
     * Print error.
     */
    public static void pl(String strError, String iDebugType)
    {
        // The constant iDebugType allows you to filter out the message you don't want to see
        Debug.pl(strError, iDebugType, DBConstants.DEBUG_INFO);
    }
    /**
     * Print stack trace if not true.
     */
    public static void pl(String strError)
    {
        Debug.pl(strError, null, DBConstants.DEBUG_INFO);
/**
if (strError.indexOf("INSERT INTO UserReg") != -1)
{
Exception ex = new Exception();
ex.printStackTrace();
System.out.println(ex);
System.exit(0);
}
*/
    }
    /**
     * Print stack trace if not true.
     */
    public static void print(Exception ex, String strDebugType, int iPriority)
    {
        System.out.println(ex.getMessage());
        ex.printStackTrace();
    }
    /**
     * Print stack trace if not true.
     */
    public static void print(Exception ex, String strDebugType)
    {
        Debug.print(ex, strDebugType, DBConstants.DEBUG_INFO);
    }
    /**
     * Print stack trace if not true.
     */
    public static void print(Exception ex)
    {
        Debug.print(ex, null, DBConstants.DEBUG_INFO);
    }
    /**
     * Print stack trace if not true.
     */
    public static void doAssert(boolean bSuccess)
    {
        if (!bSuccess)
        {
            Exception ex = new Exception();
            System.out.println("Assert False:");
            ex.printStackTrace();
            System.out.println("---Assert False");
        }
    }
    private static int m_iNextCount = 0;
    private static Map<Object,Integer> m_mapObject = new Hashtable<Object,Integer>();
    public static int getObjectID(Object object, boolean bRemove)
    {
        if (m_mapObject.get(object) != null)
        {
            if (bRemove)
                return m_mapObject.remove(object).intValue();
            else
                return m_mapObject.get(object).intValue();
        }
        m_iNextCount++;
        m_mapObject.put(object, new Integer(m_iNextCount));
        return m_iNextCount;
    }
    public static String getClassName(Object object)
    {
        if (object == null)
            return "null";
        String strClassName = object.getClass().getName();
        if (strClassName.indexOf('.') != -1)
            strClassName = strClassName.substring(strClassName.lastIndexOf('.') + 1);
        return strClassName;
    }
    public static String getStackTrace()
    {
        Exception ex = new Exception();
        return Debug.getStackTrace(ex);
    }
    public static String getStackTrace(Exception ex)
    {
        StringWriter sw = new StringWriter();
        PrintWriter s = new PrintWriter(sw);
        ex.printStackTrace(s);
        s.flush();
        return sw.toString();
    }
}
