/**
 *  @(#)MessageDetailHeaderScreen.
 *  Copyright © 2010 tourapp.com. All rights reserved.
 */
package org.jbundle.main.msg.screen;

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
import org.jbundle.main.screen.*;
import org.jbundle.main.db.*;
import org.jbundle.main.msg.db.*;

/**
 *  MessageDetailHeaderScreen - Header screen.
 */
public class MessageDetailHeaderScreen extends HeaderScreen
{
    /**
     * Default constructor.
     */
    public MessageDetailHeaderScreen()
    {
        super();
    }
    /**
     * Constructor.
     * @param itsLocation The location of this component within the parent.
     * @param parentScreen The parent screen.
     * @param fieldConverter The field this screen field is linked to.
     * @param iDisplayFieldDesc Do I display the field desc?.
     */
    public MessageDetailHeaderScreen(ScreenLocation itsLocation, BasePanel parentScreen, Converter fieldConverter, int iDisplayFieldDesc)
    {
        this();
        this.init(itsLocation, parentScreen, fieldConverter, iDisplayFieldDesc);
    }
    /**
     * Initialize class fields.
     */
    public void init(ScreenLocation itsLocation, BasePanel parentScreen, Converter fieldConverter, int iDisplayFieldDesc)
    {
        super.init(itsLocation, parentScreen, fieldConverter, iDisplayFieldDesc);
    }
    /**
     * Get the screen display title.
     */
    public String getTitle()
    {
        return "Header screen";
    }
    /**
     * SetupSFields Method.
     */
    public void setupSFields()
    {
        Record recContact = ((ReferenceField)this.getMainRecord().getField(MessageDetail.kPersonID)).getReferenceRecord();
        if (recContact != null)
            if (recContact.getEditMode() == DBConstants.EDIT_CURRENT)
            if (recContact instanceof Person)   // Profile
            {
                BaseField field = recContact.getField(recContact.getDefaultDisplayFieldSeq());
                field.setupDefaultView(this.getNextLocation(ScreenConstants.NEXT_LOGICAL, ScreenConstants.ANCHOR_DEFAULT), this, ScreenConstants.DEFAULT_DISPLAY);
            }
    }

}
