package org.jbundle.base.screen.model;

/**
 * @(#)SPopupBox.java 0.00 12-Feb-97 Don Corley
 *
 * Copyright (c) 2009 tourapp.com. All Rights Reserved.
 *      don@tourgeek.com
 */

import org.jbundle.base.screen.model.util.ScreenLocation;
import org.jbundle.thin.base.db.Converter;


/**
 * A screen popup box.
 * Note: When the user changes the popup box, the converter is changed to match the record's reference.
 * Because of buffering and caching, there is no guarantee of what record is current in the referenced
 * record if this is a ReferenceField. ie., If you want:
 * <pre>
 * To have the reference in the record match the reference in the field, add new MoveOnChangeBehavavior().
 * To have the referenced record the current record, add ReadSecondaryHandler().
 * If you want a change in the currently referenced record to change this field, then add MoveOnChangeHandler().
 * </pre>
 *
 * @version 1.0.0
 * @author    Don Corley
 */
public class SComboBox extends SPopupBox
{

    /**
     * Constructor.
     */
    public SComboBox()
    {
        super();
    }
    /**
     * Constructor.
     * @param itsLocation The location of this component within the parent.
     * @param parentScreen The parent screen.
     * @param fieldConverter The field this screen field is linked to.
     * @param iDisplayFieldDesc Do I display the field desc?
     */
    public SComboBox(ScreenLocation itsLocation, BasePanel parentScreen, Converter fieldConverter, int iDisplayFieldDesc)
    {
        this();
        this.init(itsLocation, parentScreen, fieldConverter, iDisplayFieldDesc);
    }
    /**
     * Initialize.
     * @param itsLocation The location of this component within the parent.
     * @param parentScreen The parent screen.
     * @param fieldConverter The field this screen field is linked to.
     * @param iDisplayFieldDesc Do I display the field desc?
     */
    public void init(ScreenLocation itsLocation, BasePanel parentScreen, Converter fieldConverter, int iDisplayFieldDesc)
    {
        super.init(itsLocation, parentScreen, fieldConverter, iDisplayFieldDesc);
    }
    /**
     * Free.
     */
    public void free()
    {
        super.free();
    }
}
