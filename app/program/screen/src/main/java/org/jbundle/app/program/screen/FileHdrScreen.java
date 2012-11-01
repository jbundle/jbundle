/**
 * @(#)FileHdrScreen.
 * Copyright © 2012 jbundle.org. All rights reserved.
 * GPL3 Open Source Software License.
 */
package org.jbundle.app.program.screen;

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
import org.jbundle.main.screen.*;
import org.jbundle.app.program.db.*;

/**
 *  FileHdrScreen - .
 */
public class FileHdrScreen extends DetailScreen
{
    /**
     * Default constructor.
     */
    public FileHdrScreen()
    {
        super();
    }
    /**
     * Constructor.
     * @param record The main record for this screen.
     * @param itsLocation The location of this component within the parent.
     * @param parentScreen The parent screen.
     * @param fieldConverter The field this screen field is linked to.
     * @param iDisplayFieldDesc Do I display the field desc?
     * @param properties Addition properties to pass to the screen.
     */
    public FileHdrScreen(Record record, ScreenLocation itsLocation, BasePanel parentScreen, Converter fieldConverter, int iDisplayFieldDesc, Map<String,Object> properties)
    {
        this();
        this.init(record, itsLocation, parentScreen, fieldConverter, iDisplayFieldDesc, properties);
    }
    /**
     * Initialize class fields.
     */
    public void init(Record record, ScreenLocation itsLocation, BasePanel parentScreen, Converter fieldConverter, int iDisplayFieldDesc, Map<String,Object> properties)
    {
        if (record == null)
            if (parentScreen.getRecord(ClassInfo.CLASS_INFO_FILE) != null)
        {
            iDisplayFieldDesc = iDisplayFieldDesc | ScreenConstants.DETAIL_MODE;
            record = parentScreen.getRecord(ClassInfo.CLASS_INFO_FILE); // Header record
        }
        super.init(record, itsLocation, parentScreen, fieldConverter, iDisplayFieldDesc, properties);
    }
    /**
     * Override this to open the main file.
     * <p />You should pass this record owner to the new main file (ie., new MyNewTable(thisRecordOwner)).
     * @return The new record.
     */
    public Record openMainRecord()
    {
        return new FileHdr(this);
    }
    /**
     * Open the header record.
     * @return The new header record.
     */
    public Record openHeaderRecord()
    {
        return new ClassInfo(this);
    }
    /**
     * If there is a header record, return it, otherwise, return the main record.
     * The header record is the (optional) main record on gridscreens and is sometimes used
     * to enter data in a sub-record when a header is required.
     * @return The header record.
     */
    public Record getHeaderRecord()
    {
        return this.getRecord(ClassInfo.CLASS_INFO_FILE);
    }
    /**
     * Add all the screen listeners.
     */
    public void addListeners()
    {
        super.addListeners();
        this.addMainKeyBehavior();
        if (!this.getHeaderRecord().getField(ClassInfo.CLASS_NAME).isNull())
        {
            if (this.getMainRecord().getEditMode() != DBConstants.EDIT_CURRENT)
                if (this.getMainRecord().getEditMode() != DBConstants.EDIT_IN_PROGRESS)
                    this.getMainRecord().getField(FileHdr.FILE_NAME).moveFieldToThis(this.getHeaderRecord().getField(ClassInfo.CLASS_NAME));    // Current filename
        }
    }
    /**
     * Add the sub file filter (linking the header to the main file)
     * Override this if the header does not have a direct link to the detail.
     */
    public void addSubFileFilter()
    {
        // None
    }
    /**
     * Set up all the screen fields.
     */
    public void setupSFields()
    {
        Record query = this.getMainRecord();
        for (int fieldSeq = query.getFieldSeq(FileHdr.FILE_NAME); fieldSeq < query.getFieldSeq(FileHdr.FILE_NOTES); fieldSeq++)
            query.getField(fieldSeq).setupFieldView(this);  // Add this view to the list
        for (int fieldSeq = query.getFieldSeq(FileHdr.FILE_NOTES)+1; fieldSeq < query.getFieldCount(); fieldSeq++)
            query.getField(fieldSeq).setupFieldView(this);  // Add this view to the list
        query.getField(FileHdr.FILE_NOTES).setupFieldView(this);    // Add this view to the list
    }

}
