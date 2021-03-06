/**
 * @(#)DetailScreen.
 * Copyright © 2013 jbundle.org. All rights reserved.
 * GPL3 Open Source Software License.
 */
package org.jbundle.main.screen;

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

/**
 *  DetailScreen - .
 */
public class DetailScreen extends Screen
{
    protected Record m_recHeader = null;
    /**
     * Default constructor.
     */
    public DetailScreen()
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
    public DetailScreen(Record record, ScreenLocation itsLocation, BasePanel parentScreen, Converter fieldConverter, int iDisplayFieldDesc, Map<String,Object> properties)
    {
        this();
        this.init(record, itsLocation, parentScreen, fieldConverter, iDisplayFieldDesc, properties);
    }
    /**
     * Initialize class fields.
     */
    public void init(Record record, ScreenLocation itsLocation, BasePanel parentScreen, Converter fieldConverter, int iDisplayFieldDesc, Map<String,Object> properties)
    {
        m_recHeader = null;
        if ((iDisplayFieldDesc & ScreenConstants.DETAIL_MODE) == ScreenConstants.DETAIL_MODE)
        {
            m_recHeader = record;
            record = null;
        }
        super.init(record, itsLocation, parentScreen, fieldConverter, iDisplayFieldDesc, properties);
    }
    /**
     * DetailScreen Method.
     */
    public DetailScreen(Record recHeader, Record record, ScreenLocation itsLocation, BasePanel parentScreen, Converter fieldConverter, int iDisplayFieldDesc, Map<String,Object>
     properties)
    {
        this();
        this.init(recHeader, record, itsLocation, parentScreen, fieldConverter, iDisplayFieldDesc, properties);
    }
    /**
     * Initialize class fields.
     */
    public void init(Record recHeader, Record record, ScreenLocation itsLocation, BasePanel parentScreen, Converter fieldConverter, int iDisplayFieldDesc, Map<String,Object>
     properties)
    {
        m_recHeader = null;
        m_recHeader = recHeader;
        super.init(record, itsLocation, parentScreen, fieldConverter, iDisplayFieldDesc, properties);
    }
    /**
     * Free Method.
     */
    public void free()
    {
        m_recHeader = null;
        super.free();
    }
    /**
     * If there is a header record, return it, otherwise, return the main record.
     * The header record is the (optional) main record on gridscreens and is sometimes used
     * to enter data in a sub-record when a header is required.
     * @return The header record.
     */
    public Record getHeaderRecord()
    {
        if (m_recHeader != null)
            return m_recHeader;
        Utility.getLogger().severe("Forgot to override getHeaderRecord()");
        return super.getHeaderRecord();   // Remember to override this!
    }
    /**
     * Override this to open the other files in the query.
     */
    public void openOtherRecords()
    {
        super.openOtherRecords();
        Record mainRecord = this.getMainRecord();
        if (m_recHeader != null)
        {
            if ((mainRecord != m_recHeader)
                && (mainRecord.getClass() == m_recHeader.getClass())
                && (mainRecord.getRecordName().equals(m_recHeader.getRecordName())))
            {   // Special case... the main record was passed in, but I was expecting a header record, swap them
                Record oldHeaderRecord = m_recHeader;
                m_recHeader = this.openHeaderRecord();  // The new correct header record
                if ((m_recHeader != null) && (mainRecord.getClass() != m_recHeader.getClass()))    // Just being sure that the main and header are different
                {   // That's what I thought the header is different
                    mainRecord.free();  // Should not have opened this
                    this.addRecord(oldHeaderRecord, true);  // The passed in header is the correct main record
                }
                else
                {   // Hmmm, the header and detail records are the same (record class not object)
                    if (m_recHeader != oldHeaderRecord)
                    {   // Always
                        if (m_recHeader != null)
                            m_recHeader.free();
                        m_recHeader = oldHeaderRecord;  // It was okay to begin with, restore the original values
                    }
                }
            }
            if (this.getRecord(m_recHeader.getTableNames(false)) != m_recHeader) // May be in a parent screen
                this.addRecord(m_recHeader, false);
        }
        else
        {
            m_recHeader = this.openHeaderRecord();
            if (mainRecord == m_recHeader)  // Passed in the header record as main
                this.openMainRecord();
        }
    }
    /**
     * Open the header record.
     * @return The new header record.
     */
    public Record openHeaderRecord()
    {
        Utility.getLogger().warning("Override openHeaderRecord");
        return null;
    }
    /**
     * Add all the screen listeners.
     */
    public void addListeners()
    {
        super.addListeners();
        this.addSubFileFilter();
        this.syncScreenFieldToHeader();
    }
    /**
     * Add the sub file filter (linking the header to the main file)
     * Override this if the header does not have a direct link to the detail.
     */
    public void addSubFileFilter()
    {
        // Override this if it is not correct.
        SubFileFilter listener = null;
        this.getMainRecord().addListener(listener = new SubFileFilter(this.getHeaderRecord()));
        if (this.getMainRecord().getKeyArea().getField(DBConstants.MAIN_KEY_FIELD).isNullable() == false)
        {
            listener.setFilterIfNull(true);
            listener.setAddNewHeaderOnAdd(false);
        }
        this.syncHeaderToMain();    // Read in the current (optional) Header record.
    }
    /**
     * Read the current file in the header record given the current detail record.
     */
    public void syncHeaderToMain()
    {
        super.syncHeaderToMain();
        //x I Don't think I need this anymore
        //xRecord recMain = this.getMainRecord();
        //xRecord recHeader = this.getHeaderRecord();
        //xReferenceField fldMain = recMain.getReferenceField(recHeader);
        //xif (fldMain != null)
        //x{
        //x    fldMain.setReferenceRecord(recHeader);
        //x    if ((recHeader.getEditMode() != Constants.EDIT_IN_PROGRESS) && (recHeader.getEditMode() != Constants.EDIT_CURRENT))
        //x        if (recMain.getEditMode() != Constants.EDIT_NONE)
        //x    {   // Wow... If there isn't a valid header, but I have a detail record, read the header record.
        //x        Record record = fldMain.getReference();   // This should read the header record.
        //x    }
        //x}
    }
    /**
     * Sync the screen field to the header record.
     * This method does nothing - Override it to provide the link.
     */
    public void syncScreenFieldToHeader()
    {
        // Do something like this to Link the screen field to the passed in record
        //((ReferenceField)this.getScreenRecord().getField(ProductScreenRecord.PRODUCT_ID)).syncReference(this.getHeaderRecord());
    }

}
