/*
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.base.db.event;

/**
 * @(#)MoveOnSelectHandler.java   0.00 12-Feb-97 Don Corley
 *
 * Copyright © 2012 tourgeek.com. All Rights Reserved.
 *      don@tourgeek.com
 */
import org.jbundle.base.db.Record;
import org.jbundle.base.field.BaseField;
import org.jbundle.thin.base.db.Converter;


/**
 * MoveOnSelectHandler - When a record is selected, move/display this field
 * to the destination field.
 *
 * @version 1.0.0
 * @author    Don Corley
 */
public class MoveOnSelectHandler extends MoveOnEventHandler
{
    /**
     * Constructor.
     */
    public MoveOnSelectHandler()
    {
        super();
    }
    /**
     * Constructor.
     * @param fldDest Destination field.
     * @param fldSource Source field.
     * @param convCheckMark If not null, enable/disable to listener using this field's state.
     */
    public MoveOnSelectHandler(BaseField fldDest, BaseField fldSource, Converter convCheckMark)
    {
        this();
        this.init(null, fldDest, fldSource, convCheckMark, false, false, true, false, false, null, false);
    }
    /**
     * Constructor.
     * @param fldDest tour.field.BaseField The destination field.
     * @param fldSource The source field.
     * @param convCheckMark If is field if false, don't move the data.
     * @param bMoveOnNew If true, move on new also.
     * @param bMoveOnValid If true, move on valid also.
     */
    public void init(Record record, BaseField fldDest, BaseField fldSource, Converter convCheckMark, boolean bMoveOnNew, boolean bMoveOnValid, boolean bMoveOnSelect, boolean bMoveOnAdd, boolean bMoveOnUpdate, String strSource, boolean bDontMoveNullSource)
    {
        super.init(record, fldDest, fldSource, convCheckMark, bMoveOnNew, bMoveOnValid, bMoveOnSelect, bMoveOnAdd, bMoveOnUpdate, strSource, bDontMoveNullSource);
    }
}
