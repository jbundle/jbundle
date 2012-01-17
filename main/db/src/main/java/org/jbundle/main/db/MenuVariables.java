/**
 * @(#)MenuVariables.
 * Copyright © 2011 jbundle.org. All rights reserved.
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
import org.jbundle.base.util.*;
import org.jbundle.model.*;
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

    public static final int kCurrentMenu = kScreenRecordLastField + 1;
    public static final int kMenuTitle = kCurrentMenu + 1;
    public static final int kMenuFilter = kMenuTitle + 1;
    public static final int kMenuHistory = kMenuFilter + 1;
    public static final int kMenuFormat = kMenuHistory + 1;
    public static final int kMenuVariablesLastField = kMenuFormat;
    public static final int kMenuVariablesFields = kMenuFormat - DBConstants.MAIN_FIELD + 1;
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

    public static final String kMenuVariablesFile = null; // Screen field
    /**
     * Add this field in the Record's field sequence.
     */
    public BaseField setupField(int iFieldSeq)
    {
        BaseField field = null;
        if (iFieldSeq == kCurrentMenu)
            field = new StringField(this, "CurrentMenu", 30, null, null);
        if (iFieldSeq == kMenuTitle)
            field = new StringField(this, "MenuTitle", 255, null, null);
        if (iFieldSeq == kMenuFilter)
            field = new StringField(this, "MenuFilter", 255, null, null);
        if (iFieldSeq == kMenuHistory)
            field = new StringField(this, "MenuHistory", 255, null, null);
        if (iFieldSeq == kMenuFormat)
            field = new MenuVariables_MenuFormat(this, "MenuFormat", Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (field == null)
        {
            field = super.setupField(iFieldSeq);
            if (field == null) if (iFieldSeq < kMenuVariablesLastField)
                field = new EmptyField(this);
        }
        return field;
    }

}
