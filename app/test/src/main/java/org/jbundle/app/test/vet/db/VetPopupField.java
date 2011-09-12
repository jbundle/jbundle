/*
 *  @(#)VetPopupField.
 * Copyright © 2011 jbundle.org. All rights reserved.
 */
package org.jbundle.app.test.vet.db;

import java.awt.*;
import java.util.*;

import org.jbundle.base.db.*;
import org.jbundle.thin.base.util.*;
import org.jbundle.thin.base.db.*;
import org.jbundle.base.db.event.*;
import org.jbundle.base.db.filter.*;
import org.jbundle.base.field.*;
import org.jbundle.base.field.convert.*;
import org.jbundle.base.field.event.*;
import org.jbundle.base.screen.model.*;
import org.jbundle.base.screen.model.util.*;
import org.jbundle.base.util.*;
import org.jbundle.model.*;

/**
 *  VetPopupField - Set the view to a popup.
 */
public class VetPopupField extends VetField
{
    /**
     * Default constructor.
     */
    public VetPopupField()
    {
        super();
    }
    /**
     * Constructor.
     * @param record The parent record.
     * @param strName The field name.
     * @param iDataLength The maximum string length (pass -1 for default).
     * @param strDesc The string description (usually pass null, to use the resource file desc).
     * @param strDefault The default value (if object, this value is the default value, if string, the string is the default).
     */
    public VetPopupField(Record record, String strName, int iDataLength, String strDesc, Object strDefault)
    {
        this();
        this.init(record, strName, iDataLength, strDesc, strDefault);
    }
    /**
     * Initialize class fields.
     */
    public void init(Record record, String strName, int iDataLength, String strDesc, Object strDefault)
    {
        super.init(record, strName, iDataLength, strDesc, strDefault);
    }
    /**
     * Set up the default screen control for this field.
     * @param itsLocation Location of this component on screen (ie., GridBagConstraint).
     * @param targetScreen Where to place this component (ie., Parent screen or GridBagLayout).
     * @param converter The converter to set the screenfield to.
     * @param iDisplayFieldDesc Display the label? (optional).
     * @return Return the component or ScreenField that is created for this field.
     */
    public ScreenField setupDefaultView(ScreenLocation itsLocation, BasePanel targetScreen, Converter converter, int iDisplayFieldDesc)
    {
                return this.setupTablePopup(itsLocation, targetScreen, converter, iDisplayFieldDesc, this.makeReferenceRecord(), Vet.kNameKey, Vet.kName, true, true);
        //      return this.setupTableLookup(itsLocation, targetScreen, converter, iDisplayFieldDesc, this.makeReferenceRecord(), -1, Vet.kName, true, true);
    }

}
