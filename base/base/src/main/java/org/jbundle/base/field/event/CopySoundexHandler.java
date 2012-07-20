/*
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.base.field.event;

/**
 * @(#)CopySoundexHandler.java  0.00 12-Feb-97 Don Corley
 *
 * Copyright © 2012 tourapp.com. All Rights Reserved.
 *      don@tourgeek.com
 */

import org.jbundle.base.field.BaseField;
import org.jbundle.base.model.DBConstants;

/**
 * Convert this field to a soundex, then move it to the destination field.
 * The destination (soundex) field should be a ShortField and should be
 * a non-unique index.
 *
 * @version 1.0.0
 * @author    Don Corley
 */
public class CopySoundexHandler extends FieldListener
{
    protected String m_iFieldSeq = null;

    /**
     * Constructor.
     */
    public CopySoundexHandler()
    {
        super();
    }
    /**
     * Constructor.
     * @param iFieldSeq The field to move the destination soundex to.
     */
    public CopySoundexHandler(String iFieldSeq)
    {
        this();
        this.init(null, iFieldSeq);
    }
    /**
     * Constructor.
     * @param field The basefield owner of this listener (usually null and set on setOwner()).
     * @param iFieldSeq The field to move the destination soundex to.
     */
    public void init(BaseField field, String iFieldSeq)
    {
        super.init(field);
        m_iFieldSeq = iFieldSeq;

        m_bReadMove = false;
    }
    /**
     * Set this cloned listener to the same state at this listener.
     * @param field The field this new listener will be added to.
     * @param The new listener to sync to this.
     * @param Has the init method been called?
     * @return True if I called init.
     */
    public boolean syncClonedListener(BaseField field, FieldListener listener, boolean bInitCalled)
    {
        if (!bInitCalled)
            ((CopySoundexHandler)listener).init(null, m_iFieldSeq);
        return super.syncClonedListener(field, listener, true);
    }
    /**
     * The Field has Changed.
     * @param bDisplayOption If true, display the change.
     * @param iMoveMode The type of move being done (init/read/screen).
     * @return The error code (or NORMAL_RETURN if okay).
     */
    public int fieldChanged(boolean bDisplayOption, int moveMode)
    {
        String source = this.getOwner().getString();
        String soundex = this.soundex(source.substring(0, Math.min(4, source.length())));
        if (soundex == null)
            return DBConstants.NORMAL_RETURN;
        short hashValue = this.hashSound(soundex);
        BaseField field = this.getOwner().getRecord().getField(m_iFieldSeq);
        return field.setValue(hashValue, bDisplayOption, moveMode);   // Move even if no change
    }
    /*
     *  Convert soundex (range A100 to Z656 to 2 bytes).
     * @param soundex First four bytes to convert.
     * @return The hash number.
     */
    public short hashSound(String soundex)
    {
        return (short)(((soundex.charAt(0) - 'A') << 10) | Integer.parseInt(soundex.substring(1)));
    }
    /**
     * Convert this four character string to a soundex string.
     * @param s Four character string to convert.
     * @retrun The soundex string.
     */
    public String soundex(String s)
    {
        String sound = "01230120022455012623010202";
        // Letters' categories
        // ABCDEFGHIJKLMNOPQRSTUVWXYZ
        char code[] = {'0', '0', '0', '0'};
        if ((s == null) || (s.length() == 0))
            return null;
        code[0] = Character.toUpperCase(s.charAt(0));
        if (!Character.isLetter(code[0]))
            return null;    // Must start with a letter
        int iSrc = 0;   // Start at second char
        int iDest = 1;
        char ch;
        while (iSrc < s.length())
        {
            if (iDest >= 4)
                break;
            iSrc++;
            if (!Character.isLetter(s.charAt(iSrc)))
                continue; // skip all nonalphabetics
            ch = sound.charAt(Character.toUpperCase(s.charAt(iSrc))-'A'); // Determine the category
            if ((ch == '0') || (ch == code[iDest - 1]) )
                continue;
            code[iDest++] = ch;
        }
        return new String(code);
    }
} 
