package org.jbundle.model;

/**
 * @(#)Freeable.java    1.16 95/12/14 Don Corley
 *
 * Copyright (c) 2009 tourapp.com. All Rights Reserved.
 *      don@tourgeek.com
 *
 */

/**
 * Freeable - To free this object's resources, call free().
 */
public interface Freeable
{
    /**
     * Free this object's resources.
     */
    public void free();
}
