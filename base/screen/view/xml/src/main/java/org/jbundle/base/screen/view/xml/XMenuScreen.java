/*
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.base.screen.view.xml;

/**
 * @(#)ScreenField.java   0.00 12-Feb-97 Don Corley
 *
 * Copyright © 2012 tourgeek.com. All Rights Reserved.
 *      don@tourgeek.com
 */
import org.jbundle.base.db.Record;
import org.jbundle.base.screen.model.ScreenField;
import org.jbundle.model.main.db.MenusModel;
import org.jbundle.model.screen.ScreenComponent;

/**
 * ScreenField - This is the information which tells the system about a field on the
 *  screen... where it is, what type it is, and the file and field position.
 *  <p> TODO:
 *  1. Scroll into view if focused
 *  2. Tab = tab, even for JTextAreas (Could be a swing bug)
 */
public class XMenuScreen extends XBaseMenuScreen
{

    /**
     * Constructor.
     */
    public XMenuScreen()
    {
        super();
    }
    /**
     * Constructor.
     */
    public XMenuScreen(ScreenField model,boolean bEditableControl)
    {
        this();
        this.init(model, bEditableControl);
    }
    /**
     * Constructor.
     */
    public void init(ScreenComponent model, boolean bEditableControl)
    {
        super.init(model, bEditableControl);
    }
    /**
     * Free.
     */
    public void free()
    {
        super.free();
    }
    /**
     * Get the Html keywords.
     */
    public String getHtmlKeywords()
    {
        Record recMenu = this.getMainRecord();
        if (recMenu.getField(MenusModel.KEYWORDS).getLength() > 0)
            return recMenu.getField(MenusModel.KEYWORDS).toString();
        else
            return super.getHtmlKeywords();
    }
    /**
     * Get the Html Description.
     */
    public String getHtmlMenudesc()
    {
        Record recMenu = this.getMainRecord();
        if (recMenu.getField(MenusModel.COMMENT).getLength() > 0)
            return recMenu.getField(MenusModel.COMMENT).toString();
        else
            return super.getHtmlMenudesc();
    }
}
