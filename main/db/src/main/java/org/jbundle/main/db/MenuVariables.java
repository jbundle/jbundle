/**
 * @(#)MenuVariables.
 * Copyright © 2012 jbundle.org. All rights reserved.
 * GPL3 Open Source Software License.
 */
package org.jbundle.main.db;

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
import org.jbundle.base.model.*;
import org.jbundle.base.util.*;
import org.jbundle.model.*;
import org.jbundle.model.db.*;
import org.jbundle.model.screen.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.io.*;
import java.net.*;
import java.util.*;
import org.jbundle.base.db.xmlutil.*;

/**
 *  MenuVariables - .
 */
public class MenuVariables extends ScreenRecord
{
    private static final long serialVersionUID = 1L;

    public static final String CURRENT_MENU = "CurrentMenu";
    public static final String MENU_TITLE = "MenuTitle";
    public static final String MENU_FILTER = "MenuFilter";
    public static final String MENU_HISTORY = "MenuHistory";
    public static final String MENU_FORMAT = "MenuFormat";
    /**
     * Default constructor.
     */
    public MenuVariables()
    {
        super();
    }
    /**
     * Constructor.
     */
    public MenuVariables(RecordOwner screen)
    {
        this();
        this.init(screen);
    }
    /**
     * Initialize class fields.
     */
    public void init(RecordOwner screen)
    {
        super.init(screen);
    }

    public static final String MENU_VARIABLES_FILE = null;  // Screen field
    /**
     * Add this field in the Record's field sequence.
     */
    public BaseField setupField(int iFieldSeq)
    {
        BaseField field = null;
        if (iFieldSeq == 0)
            field = new StringField(this, CURRENT_MENU, 30, null, null);
        if (iFieldSeq == 1)
            field = new StringField(this, MENU_TITLE, 255, null, null);
        if (iFieldSeq == 2)
            field = new StringField(this, MENU_FILTER, 255, null, null);
        if (iFieldSeq == 3)
            field = new StringField(this, MENU_HISTORY, 255, null, null);
        if (iFieldSeq == 4)
            field = new MenuVariables_MenuFormat(this, MENU_FORMAT, Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (field == null)
            field = super.setupField(iFieldSeq);
        return field;
    }

}
