/*
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.base.screen.model;

/**
 * @(#)STreeCtrl.java 0.00 12-Feb-97 Don Corley
 *
 * Copyright © 2012 tourapp.com. All Rights Reserved.
 *      don@tourgeek.com
 */
import java.util.Map;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import org.jbundle.base.db.Record;
import org.jbundle.base.model.ScreenConstants;
import org.jbundle.base.screen.model.util.ScreenLocation;
import org.jbundle.model.DBException;
import org.jbundle.model.db.Convert;
import org.jbundle.thin.base.db.Converter;


/**
 * Implements a tree control.
 */
public class STreeCtrl extends BaseScreen
{

    /**
     * Constructor.
     */
    public STreeCtrl()  {
        super();
    }
    /**
     * Constructor.
     * @param record The main record for this screen.
     * @param itsLocation The location of this component within the parent.
     * @param parentScreen The parent screen.
     * @param fieldConverter The field this screen field is linked to.
     * @param iDisplayFieldDesc Do I display the field desc?
     */
    public STreeCtrl(Record record, ScreenLocation itsLocation, BasePanel parentScreen, Converter fieldConverter, int iDisplayFieldDesc, Map<String, Object> properties)
    {
        this();
        this.init(record, itsLocation, parentScreen, fieldConverter, iDisplayFieldDesc, properties);
    }
    /**
     * Open the files and setup the screen.
     * @param record The main record for this screen.
     * @param itsLocation The location of this component within the parent.
     * @param parentScreen The parent screen.
     * @param fieldConverter The field this screen field is linked to.
     * @param iDisplayFieldDesc Do I display the field desc?
     */
    public void init(Record mainRecord, ScreenLocation itsLocation, BasePanel parentScreen, Converter fieldConverter, int iDisplayFieldDesc, Map<String, Object> properties)
    {
        super.init(mainRecord, itsLocation, parentScreen, fieldConverter, iDisplayFieldDesc, properties);
// Put the grid table in front of this record, so grid operations will work.
        // Add in the columns
        JTree control = (JTree)this.getScreenFieldView().getControl();
        DefaultMutableTreeNode node[] = new DefaultMutableTreeNode[20];
        for (int i = 0; i < 20; i++)
            node[i] = null;
        node[0] = new DefaultMutableTreeNode("Products");
        Record record = this.getMainRecord();
        try   {
            record.close();
            while (record.hasNext())
            {
                record.next();
                int i = 0;
                for (int iIndex = 0; iIndex < this.getSFieldCount(); iIndex++)
                {
                    ScreenField sField = this.getSField(iIndex);
                    if (sField instanceof ToolScreen)
                        continue;
                    if (sField.getConverter() == null)
                        continue;
                    String string = sField.getConverter().toString();
                    if ((node[i + 1] == null) || 
                        (iIndex == this.getSFieldCount() - 1) || 
                        (!node[i + 1].toString().equalsIgnoreCase(string)))
                    {   // New level, or leaf, or New value
                        node[i].add(node [i + 1] = new DefaultMutableTreeNode(string));
                        node[i + 2] = null;     // New level - add to all
                    }
                    i++;
                }
            }
        } catch (DBException ex)    {
        }
        control.setModel(new DefaultTreeModel(node[0]));

        this.resizeToContent(this.getTitle());
    }
    /**
     * Set up the screen fields (default = set them all up for the current record).
     * @param converter The converter to set up the next default control for.
     */
    public Object addColumn(Converter converter)
    {
        return converter.setupDefaultView(this.getNextLocation(ScreenConstants.NEXT_LOGICAL, ScreenConstants.SET_ANCHOR), this, ScreenConstants.DEFAULT_DISPLAY);
    }
    /**
     * The title for this screen.
     * @return The title.
     */
    public String getTitle()    // Standard file maint for this record (returns new record)
    { // This is almost always overidden!
        String windowName = "Tree"; // Default
        Record query = this.getMainRecord();
        if (query != null)
            windowName = query.getRecordName() + ' ' + windowName;
        return windowName;
    }
    /**
     *  Get the converter for this column.
     *  This method is only used by FlexTreeHandler!
     * @param iSelectIndex The field to return.
     * @return The converter at this location.
     */
    public Convert getTreeField(int iSelectIndex)
    {
        return this.getSField(iSelectIndex).getConverter();
    }
}
