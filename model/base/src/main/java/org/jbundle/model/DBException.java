/*
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.model;

/**
 * @(#)TableException.java  1.16 95/12/14 Don Corley
 *
 * Copyright © 2012 tourgeek.com. All Rights Reserved.
 *      don@tourgeek.com
 */

/**
 * Table and Record Exceptions.
 * Errors are handled in several ways in the system.
 *
 * @version 1.0.0
 * @author    Don Corley
 */
public class DBException extends Exception
{
	private static final long serialVersionUID = 1L;

	/**
     * Error code.
     */
    protected int m_iErrorCode = 0;

    /**
     * Constructor for standard errors.
     */
    public DBException()
    {
        super();    // Get the error text, or make it up
    }
    /**
     * Constructor for standard errors.
     */
    public DBException(int iErrorCode)
    {
        super();    // Get the error text, or make it up
        this.init(null, iErrorCode, -1);
    }
    /**
     * Constructor.
     */
    public DBException(String strError)
    {
        super(strError);
        this.init(strError, -1, -1);
    }
    /**
     * Constructor.
     */
    public void init(String strError, int iErrorCode, int iWarningCode)
    {
        m_iErrorCode = iErrorCode;
    }
    /**
     * Return the error code.
     */
    public int getErrorCode()
    {
        return m_iErrorCode;
    }
    /**
     * Returns the detail message string of this throwable.
     *
     * @return  the detail message string of this <tt>Throwable</tt> instance
     *          (which may be <tt>null</tt>).
     */
    public String getMessage() {
        String message = super.getMessage();
        if (m_iErrorCode != -1)
            message = message + " Error code: " + m_iErrorCode;
        return message;
    }
}
