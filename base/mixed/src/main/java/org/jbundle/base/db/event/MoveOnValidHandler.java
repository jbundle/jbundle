package org.jbundle.base.db.event;

/**
 * @(#)MoveOnValidHandler.java  0.00 12-Feb-97 Don Corley
 *
 * Copyright (c) 2009 tourapp.com. All Rights Reserved.
 *      don@tourgeek.com
 */
import org.jbundle.base.db.Record;
import org.jbundle.base.field.BaseField;
import org.jbundle.base.util.DBConstants;
import org.jbundle.thin.base.db.Converter;


/**
 * On Valid or new record, move source field or string to destination.
 *
 * @version 1.0.0
 * @author    Don Corley
 */
public class MoveOnValidHandler extends MoveOnEventHandler
{
    /**
     * Constructor.
     */
    public MoveOnValidHandler()
    {
        super();
    }
    /**
     * This Constructor moves the current Handle to the (Reference or FullReference) dest field on valid.
     * @param pfldDest tour.field.BaseField
     */
    public MoveOnValidHandler(BaseField fldDest)
    {
        this();
        this.init(null, fldDest, null, null, false, true, false, false, false, null, false);
    }
    /**
     * This Constructor moves this string to the dest field on valid.
     * @param pfldDest tour.field.BaseField The destination field.
     * @param strSource The source string.
     */
    public MoveOnValidHandler(BaseField fldDest, String strSource)
    {
        this();
        this.init(null, fldDest, null, null, false, true, false, false, false, strSource, false);
    }
    /**
     * This Constructor moves this string to the dest field on valid on optionally on new.
     * @param pfldDest tour.field.BaseField The destination field.
     * @param strSource The source string.
     * @param bMoveOnNew If true, move on new also.
     */
    public MoveOnValidHandler(BaseField fldDest, String strSource, boolean bMoveOnNew)
    {
        this();
        this.init(null, fldDest, null, null, bMoveOnNew, true, false, false, false, strSource, false);
    }
    /**
     * This Constructor moves the source field to the dest field on valid.
     * @param pfldDest tour.field.BaseField The destination field.
     * @param fldSource The source field.
     */
    public MoveOnValidHandler(BaseField fldDest, BaseField fldSource)
    {
        this();
        this.init(null, fldDest, fldSource, null, false, true, false, false, false, DBConstants.BLANK, false);
    }
    /**
     * Constructor.
     * @param pfldDest tour.field.BaseField The destination field.
     * @param fldSource The source field.
     * @param pCheckMark If is field if false, don't move the data.
     * @param bMoveOnNew If true, move on new also.
     * @param bMoveOnValid If true, move on valid also.
     */
    public MoveOnValidHandler(BaseField fldDest, BaseField fldSource, Converter convCheckMark, boolean bMoveOnNew, boolean bMoveOnValid)
    {
        this();
        this.init(null, fldDest, fldSource, convCheckMark, bMoveOnNew, bMoveOnValid, false, false, false, DBConstants.BLANK, false);
    }
    /**
     * Constructor.
     * @param pfldDest tour.field.BaseField The destination field.
     * @param fldSource The source field.
     * @param pCheckMark If is field if false, don't move the data.
     * @param bMoveOnNew If true, move on new also.
     * @param bMoveOnValid If true, move on valid also.
     */
    public void init(Record record, BaseField fldDest, BaseField fldSource, Converter convCheckMark, boolean bMoveOnNew, boolean bMoveOnValid, boolean bMoveOnSelect, boolean bMoveOnAdd, boolean bMoveOnUpdate, String strSource, boolean bDontMoveNullSource)
    {
        super.init(record, fldDest, fldSource, convCheckMark, bMoveOnNew, bMoveOnValid, bMoveOnSelect, bMoveOnAdd, bMoveOnUpdate, strSource, bDontMoveNullSource);
    }
}
