/*
 * Copyright © 2011 jbundle.org. All rights reserved.
 */
package org.jbundle.base.screen.model.html;

/**
 *  DataAccessScreen.
 *  Copyright � 1997 jbundle.org. All rights reserved.
 */

import java.util.Map;

import org.jbundle.base.db.Record;
import org.jbundle.base.model.DBParams;
import org.jbundle.base.model.ScreenFieldView;
import org.jbundle.base.screen.model.BasePanel;
import org.jbundle.base.screen.model.BaseScreen;
import org.jbundle.base.screen.model.util.ScreenLocation;
import org.jbundle.base.screen.view.data.DJnlpAccessScreen;
import org.jbundle.base.screen.view.data.DObjectAccessScreen;
import org.jbundle.base.screen.view.data.DTableAccessScreen;
import org.jbundle.thin.base.db.Converter;


/**
 *  DataAccessScreen.
 * This class formats and sends raw data types such as JPGs and JNLP files.
 */
public class DataAccessScreen extends BaseScreen
{

    /**
     * Constructor.
     */
    public DataAccessScreen()
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
     */
    public DataAccessScreen(Record mainFile,ScreenLocation itsLocation,BasePanel parentScreen,Converter fieldConverter,int iDisplayFieldDesc, Map<String, Object> properties)
    {
        this();
        this.init(mainFile, itsLocation, parentScreen, fieldConverter, iDisplayFieldDesc, properties);
    }
    /**
     *  Initialize the member fields.
     * @param record The main record for this screen.
     * @param itsLocation The location of this component within the parent.
     * @param parentScreen The parent screen.
     * @param fieldConverter The field this screen field is linked to.
     * @param iDisplayFieldDesc Do I display the field desc?
     */
    public void init(Record mainFile, ScreenLocation itsLocation, BasePanel parentScreen, Converter fieldConverter, int iDisplayFieldDesc, Map<String, Object> properties)
    {
        super.init(mainFile, itsLocation, parentScreen, fieldConverter, iDisplayFieldDesc, properties);
    }
    /**
     *  Specify the fields to display.
     */
    public void setupSFields()
    {
    }
    /**
     * Open the main file.
     * DataAccessScreens check for a record param.
     * @return The new main file.
     */
    public Record openMainRecord()
    {
        Record record = null;
        if (this.getProperty(DBParams.APPLET) != null)
        	return null;	// Ignore params if I want the applet html displayed
        String strParamRecord = this.getProperty(DBParams.RECORD);      // Display record
        if ((strParamRecord != null) && (strParamRecord.length() > 0))
            record = Record.makeRecordFromClassName(strParamRecord, this);
        return record;
    }
    /**
     * Set up the physical control (that implements Component).
     * Note: Since AnObjectAccessScreen is only used for HTML, it does not
     * have different implementations based on the screen view.
     * It does although have different implementations depending on the data type
     * requested (and they are always overrides of DataAccessScreen).
     * @param bEditableControl If editable.
     * @return The new view to handle the data type specified in the DATATYPE param.
     */
    public ScreenFieldView setupScreenFieldView(boolean bEditableControl)
    {
        String strDatatype = this.getProperty(DBParams.DATATYPE);
        if (DBParams.TABLE_PARAM.equalsIgnoreCase(strDatatype))
            return new DTableAccessScreen(this, bEditableControl);
        if (DBParams.WEBSTART_PARAM.equalsIgnoreCase(strDatatype))
            return new DJnlpAccessScreen(this, bEditableControl);
        if (DBParams.WEBSTART_APPLET_PARAM.equalsIgnoreCase(strDatatype))
            return new DJnlpAccessScreen(this, bEditableControl);
//        if (DBParams.WSDL_PARAM.equalsIgnoreCase(strDatatype))
//            return new DWsdlAccessScreen(this, bEditableControl);
//        else //if (DBParams.IMAGE_PATH.equalsIgnoreCase(strDatatype))
            return new DObjectAccessScreen(this, bEditableControl);
    }
}
