/*
 * Copyright © 2011 jbundle.org. All rights reserved.
 */
package org.jbundle.base.screen.view.zml;

/**
 * @(#)ScreenField.java   0.00 12-Feb-97 Don Corley
 *
 * Copyright (c) 2009 tourapp.com. All Rights Reserved.
 *      don@tourgeek.com
 */
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;

import org.jbundle.base.db.DatabaseException;
import org.jbundle.base.db.Record;
import org.jbundle.base.field.BaseField;
import org.jbundle.base.message.core.trx.TrxMessageHeader;
import org.jbundle.base.model.DBConstants;
import org.jbundle.base.model.DBParams;
import org.jbundle.base.model.HtmlConstants;
import org.jbundle.base.model.ScreenFieldView;
import org.jbundle.base.model.Utility;
import org.jbundle.base.screen.model.BasePanel;
import org.jbundle.base.screen.model.GridScreen;
import org.jbundle.base.screen.model.ScreenField;
import org.jbundle.base.screen.model.util.DisplayToolbar;
import org.jbundle.base.screen.model.util.HelpToolbar;
import org.jbundle.base.screen.model.util.MaintToolbar;
import org.jbundle.base.screen.model.util.MenuToolbar;
import org.jbundle.base.screen.view.ScreenFieldViewAdapter;
import org.jbundle.model.DBException;
import org.jbundle.model.screen.ScreenComponent;
import org.jbundle.thin.base.db.Constants;
import org.jbundle.thin.base.util.ThinMenuConstants;


/**
 * The base view for XML and HTML components.
 * All shared *ml handling should go here.
 * Note: Some of these methods are just utility methods since
 * the entire class tree cannot be shared between HTML and XML.
 */
public abstract class ZScreenField extends ScreenFieldViewAdapter
{
    /**
     * Constructor.
     */
    public ZScreenField()
    {
        super();
    }
    /**
     * Constructor.
     * @param model The model object for this view object.
     * @param bEditableControl Is this control editable?
     */
    public ZScreenField(ScreenField model, boolean bEditableControl)
    {
        this();
        this.init(model, bEditableControl);
    }
    /**
     * Constructor.
     * @param model The model object for this view object.
     * @param bEditableControl Is this control editable?
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
     * Get the HTML Alignment type.
     * @return the HTML alignment (as a string).
     */
    public String getControlAlignment()
    {
        return Constants.BLANK;
    }
    /**
     * Move the HTML input to the screen record fields.
     * @param strSuffix value to add to the end of the field name before retrieving the param.
     * @exception DBException File exception.
     * @return bParamsFound True if params were found and moved.
     */
    public int moveControlInput(String strSuffix)
        throws DBException
    {
        int iDefaultParamsFound = DBConstants.NO_PARAMS_FOUND;
        if (!(this.getScreenField() instanceof BasePanel))
        {   // Regular input field
            if (this.getScreenField().isInputField())
            {
                String strFieldName = this.getScreenField().getSFieldParam(strSuffix);
                String strParamValue = this.getSFieldProperty(strFieldName);

                if (strParamValue != null)
                {
                    int iErrorCode = this.getScreenField().setSFieldValue(strParamValue, DBConstants.DONT_DISPLAY, DBConstants.SCREEN_MOVE);
                    if (iErrorCode != DBConstants.NORMAL_RETURN)
                    {
                        DatabaseException ex = new DatabaseException(iErrorCode);
                        String strError = ex.getMessage(this.getScreenField().getParentScreen().getTask());
                        String strFieldDesc = null;
                        if (this.getScreenField().getConverter() != null)
                            strFieldDesc = this.getScreenField().getConverter().getFieldDesc();
                        if ((strFieldDesc == null) || (strFieldDesc.length() == 0))
                            strFieldDesc = strFieldName;
                        if (strError != null) // Add the field name to the error
                            ex = new DatabaseException(strError + " on " + strFieldDesc + " field");
                        throw ex;
                    }
                    iDefaultParamsFound = DBConstants.NORMAL_RETURN;
                }
            }      
        }
        else
        {   
            int iNumCols = ((BasePanel)this.getScreenField()).getSFieldCount();
            String strMoveValue = this.getProperty(DBParams.COMMAND);      // Display record
            if (strMoveValue == null)
                strMoveValue = Constants.BLANK;
            if ((strMoveValue != null) && (strMoveValue.length() > 0))    // Only move params on submit
            {  // Move the input params to the record fields
                for (int iIndex = 0; iIndex < iNumCols; iIndex++)
                {
                    ScreenFieldView vField = ((BasePanel)this.getScreenField()).getSField(iIndex).getScreenFieldView();
                    if (vField.moveControlInput(strSuffix) == DBConstants.NORMAL_RETURN)
                        iDefaultParamsFound = DBConstants.NORMAL_RETURN;
                }
            }
        }
        return iDefaultParamsFound;
    }
    /**
     * Get this control's value as it was submitted by the HTML post operation.
     * @return The value the field was set to.
     */
    public String getSFieldProperty(String strFieldName)
    {
        return this.getScreenField().getSFieldProperty(strFieldName);
    }
    /**
     * Move the HTML input format to the fields and do the action requested.
     * @param bDefaultParamsFound If the params have been found yet.
     * @return true if input params have been found.
     * @exception DBException File exception.
     */
    public boolean processServletCommand()
        throws DBException
    {
        String strCommand = this.getProperty(DBParams.COMMAND);        // Display record
        if (strCommand == null)
            strCommand = Constants.BLANK;
        boolean bSuccess = false;
        if (this.getScreenField() instanceof BasePanel)
        {   // Always
            if (strCommand.equalsIgnoreCase(ThinMenuConstants.FIRST))
                bSuccess = ((BasePanel)this.getScreenField()).onMove(DBConstants.FIRST_RECORD);
            else if (strCommand.equalsIgnoreCase(ThinMenuConstants.PREVIOUS))
                bSuccess = ((BasePanel)this.getScreenField()).onMove(DBConstants.PREVIOUS_RECORD);
            else if (strCommand.equalsIgnoreCase(ThinMenuConstants.NEXT))
                bSuccess = ((BasePanel)this.getScreenField()).onMove(DBConstants.NEXT_RECORD);
            else if (strCommand.equalsIgnoreCase(ThinMenuConstants.LAST))
                bSuccess = ((BasePanel)this.getScreenField()).onMove(DBConstants.LAST_RECORD);
            else if (strCommand.equalsIgnoreCase(ThinMenuConstants.SUBMIT))
                bSuccess = ((BasePanel)this.getScreenField()).onAdd();
            else if (strCommand.equalsIgnoreCase(ThinMenuConstants.DELETE))
                bSuccess = ((BasePanel)this.getScreenField()).onDelete();
            else
            { // Must have sent a user-defined command (send command to this screen)
                bSuccess = false;   // Too dangerous ((BasePanel)this.getScreenField()).handleCommand(strCommand, this.getScreenField(), false);
            }
        }
        return bSuccess;
    }
    /**
     * Display this screen's toolbars in html input format.
     * @param out The html out stream.
     * @return true if default params were found for this form.
     * @exception DBException File exception.
     */
    public boolean printZmlToolbarControls(PrintWriter out, int iHtmlOptions)
    {
        boolean bFieldsFound = false;
        int iNumCols = ((BasePanel)this.getScreenField()).getSFieldCount();
        for (int iIndex = 0; iIndex < iNumCols; iIndex++)
        {
            ScreenField sField = ((BasePanel)this.getScreenField()).getSField(iIndex);
            if (sField.isToolbar())
            {
                if (sField.printControl(out, iHtmlOptions | HtmlConstants.HTML_INPUT | HtmlConstants.HTML_ADD_DESC_COLUMN))
                    bFieldsFound = true;
            }
        }
        return bFieldsFound;
    }
    /**
     * Display this screen's toolbars in html input format.
     * @param out The html out stream.
     * @return true if default params were found for this form.
     * @exception DBException File exception.
     */
    public boolean printZmlToolbarData(PrintWriter out, int iHtmlOptions)
    {
        boolean bFieldsFound = false;
        int iNumCols = ((BasePanel)this.getScreenField()).getSFieldCount();
        for (int iIndex = 0; iIndex < iNumCols; iIndex++)
        {
            ScreenField sField = ((BasePanel)this.getScreenField()).getSField(iIndex);
            if (sField.isToolbar())
            {
                if (sField.printData(out, HtmlConstants.HTML_INPUT | HtmlConstants.HTML_ADD_DESC_COLUMN))
                    bFieldsFound = true;
            }
        }
        return bFieldsFound;
    }
    /**
     * Get the path to the image in this control.
     * @return
     */
    public String getZmlImagePath()
    {
        String strImage = null;
        if (this.getScreenField().getConverter() != null)
            if (this.getScreenField().getConverter().getField() != null)
                if (!this.getScreenField().getConverter().getField().isNull())
        {
            BaseField field = (BaseField)this.getScreenField().getConverter().getField();
            Record record = field.getRecord();
            String strObjectID = null;
            if ((record.getEditMode() == DBConstants.EDIT_CURRENT) || (record.getEditMode() == DBConstants.EDIT_IN_PROGRESS))
            {
                try   {
                    strObjectID = record.getHandle(DBConstants.OBJECT_ID_HANDLE).toString();
                } catch (DBException ex)    {
                    strObjectID = null;
                }
            }
            if (strObjectID != null) if (strObjectID.length() > 0)
            {
                String strGraphicFormat = "jpg";
                String strImageServlet = HtmlConstants.SERVLET_PATH + '/' + DBParams.IMAGE_PATH;
                strImageServlet = Utility.addURLParam(strImageServlet, DBParams.DATATYPE, strGraphicFormat);
                strImageServlet = Utility.addURLParam(strImageServlet, DBParams.RECORD, record.getClass().getName());
                strImageServlet = Utility.addURLParam(strImageServlet, DBConstants.STRING_OBJECT_ID_HANDLE, strObjectID);
                strImageServlet = Utility.addURLParam(strImageServlet, "field", field.getFieldName(false, false));
//?                String strImageSize = "";
//+             strSize = " width=" + strWidth + " height=" + strHeight;
                strImage = strImageServlet;
            }
        }
        return strImage;
    }
    /**
     * Display this screen's hidden params.
     * @param out The html out stream.
     * @exception DBException File exception.
     */
    public Map<String, Object> getHiddenParams()
    {
        Map<String, Object> mapParams = this.getScreenField().getBasePanel().getHiddenParams();
        String strParamRecord = this.getProperty(DBParams.RECORD);      // Display record
        if (strParamRecord == null)
            strParamRecord = Constants.BLANK;
        String strParamScreen = this.getProperty(DBParams.SCREEN);      // Display screen
        if (strParamScreen == null)
            strParamScreen = Constants.BLANK;
        String strParamForms = this.getProperty(HtmlConstants.FORMS);   // Display record
        if ((strParamForms == null) || (strParamForms.length() == 0))
        {
            strParamForms = Constants.BLANK;
            if (this.getScreenField().isToolbar())
                if ((!(this.getScreenField() instanceof DisplayToolbar)) &&
                (!(this.getScreenField() instanceof MaintToolbar)) &&
                (!(this.getScreenField() instanceof MenuToolbar)) &&
                (!(this.getScreenField() instanceof HelpToolbar)))
                    strParamForms = HtmlConstants.BOTH;
        }
        String strTrxID = this.getProperty(TrxMessageHeader.LOG_TRX_ID);

        Record record = ((BasePanel)this.getScreenField()).getMainRecord();
        String strRecord = strParamRecord;
        if (record != null)
            strRecord = record.getClass().getName().toString();
        if (strParamRecord.length() > 0) if (!strRecord.equals(strParamRecord))
            strParamScreen = Constants.BLANK; // Special case - Main rec is not rec passed.
        if (mapParams.get(DBParams.RECORD) == null)
            mapParams.put(DBParams.RECORD, strRecord);
        if (mapParams.get(DBParams.SCREEN) == null)
            mapParams.put(DBParams.SCREEN, strParamScreen);
        if (strParamForms.equalsIgnoreCase(HtmlConstants.INPUT))
            strParamForms = HtmlConstants.DATA;
        String strDefaultFormParam = this.getDefaultFormsParam();
        if (strDefaultFormParam != null)
            strParamForms = strDefaultFormParam;
        if (mapParams.get(HtmlConstants.FORMS) == null)
            mapParams.put(HtmlConstants.FORMS, strParamForms);
        if (strTrxID != null)
            if (mapParams.get(TrxMessageHeader.LOG_TRX_ID) == null)
                mapParams.put(TrxMessageHeader.LOG_TRX_ID, strTrxID);
        if (record != null)
            if ((record.getEditMode() == Constants.EDIT_IN_PROGRESS) || (record.getEditMode() == Constants.EDIT_CURRENT))
        {
            try {
                String strBookmark = record.getHandle(DBConstants.OBJECT_ID_HANDLE).toString();
                if (mapParams.get(DBConstants.STRING_OBJECT_ID_HANDLE) == null)
                    mapParams.put(DBConstants.STRING_OBJECT_ID_HANDLE, URLEncoder.encode(strBookmark, DBConstants.URL_ENCODING));
            } catch (DBException ex) {
                ex.printStackTrace();
            } catch (java.io.UnsupportedEncodingException ex) {
                ex.printStackTrace();
            }
        }
        return mapParams;
    }
    /**
     * Display this screen's hidden params.
     * @param out The html out stream.
     * @exception DBException File exception.
     */
    public void addHiddenParams(PrintWriter out, Map<String, Object> mapParams)
    {
        for (String key : mapParams.keySet())
        {
            if (mapParams.get(key) != null)
                this.addHiddenParam(out, key, mapParams.get(key).toString());
        }
    }
    /**
     * Add this hidden param to the output stream.
     * @param out The html output stream.
     * @param strParam The parameter.
     * @param strValue The param's value.
     */
    public void addHiddenParam(PrintWriter out, String strParam, String strValue)
    {
        // Override this (if you don't want straight XML written)
        out.print("<param name=\"" + strParam + "\">");
        if (strValue != null)
            out.print(strValue);
        out.println(Utility.endTag("param"));
    }
    /**
     * Get the Forms param to be passed on submit.
     * @return  The hidden "forms" param to be passed on submit (input/diplay/both/bothifdata).
     */
    public String getDefaultFormsParam()
    {
        if (this.getScreenField().getParentScreen() != null)
            return ((ZScreenField)this.getScreenField().getParentScreen().getScreenFieldView()).getDefaultFormsParam();
        else
            return null;
    }
    /**
     * Get the Html parameter for this field.
     * (Only for XML/HTML fields).
     * @return The parameter name.
     * @exception DBException File exception.
     */
    public String getHtmlFieldParam()
    {
        String strFieldName = this.getScreenField().getSFieldParam(null, false);

        if (this.getScreenField().getParentScreen() instanceof GridScreen)
        { // These are command buttons such as "Form" or "Detail"
            GridScreen gridScreen = (GridScreen)this.getScreenField().getParentScreen();
            Record record = gridScreen.getMainRecord();
            try {
                Object objBookmark = record.getHandle(DBConstants.OBJECT_ID_HANDLE);
                if (objBookmark == null)
                    objBookmark = record.getHandle(DBConstants.BOOKMARK_HANDLE);
                if (objBookmark != null)
                    strFieldName = strFieldName + '@' + objBookmark.toString();
            } catch (DBException ex)    {
                ex.printStackTrace();
            }
        }

        try {
            strFieldName = URLEncoder.encode(strFieldName, DBConstants.URL_ENCODING);
        } catch (java.io.UnsupportedEncodingException ex) {
            ex.printStackTrace();
        }
        return strFieldName;
    }
    /**
     * Get the Html keywords.
     */
    public String getHtmlKeywords()
    {
        return Constants.BLANK;
    }
    /**
     * Get the Html Description.
     */
    public String getHtmlMenudesc()
    {
        return Constants.BLANK;
    }
	public static final String WEB_START_DEFAULT =
		"<script src=\"http://java.com/js/deployJava.js\"></script>\n" +
		"<script>\n" +
		"deployJava.runApplet({{code:\"{" + DBParams.APPLET + "}\", \n" +
		"codebase:\"{" + HtmlConstants.CODEBASE + "}\", " +
		"width:\"{" + HtmlConstants.WIDTH + "}\", height:\"{" + HtmlConstants.HEIGHT + "}\", name:\"{" + HtmlConstants.NAME + "}\"}}, {{\n" +
		"{other}\n" +
		"jnlp_href:\"{jnlpURL}\"}},\n" +
		"\"1.6\");\n" +
		"</script>";
    public static final String DEFAULT_JNLP_URL = DBConstants.DEFAULT_SERVLET + "?datatype=" + DBParams.WEBSTART_APPLET_PARAM;
    /**
     * Create a URL to retrieve the JNLP file.
     * @param propApplet
     * @param properties
     * @return
     */
    public String getJnlpURL(String strJnlpURL, Map<String,Object> propApplet, Map<String,Object> properties)
    {
        String strBaseURL = (String)properties.get(DBParams.BASE_URL);
        if (strBaseURL == null)
            strBaseURL = DBConstants.BLANK;
        else if ((!strBaseURL.startsWith("/")) && (!strBaseURL.startsWith("http:")))
            strBaseURL = "//" + strBaseURL;
        if (!strBaseURL.startsWith("http:"))
            strBaseURL = "http:" + strBaseURL;
        StringBuffer sbJnlpURL = new StringBuffer(strBaseURL);

        if ((strJnlpURL == null) || (strJnlpURL.length() == 0))
        	strJnlpURL = DEFAULT_JNLP_URL;
        sbJnlpURL.append(strJnlpURL);
        
        sbJnlpURL.append("&" + DBParams.APPLET + "=" + propApplet.get(DBParams.APPLET));
        try {
            for (Map.Entry<String,Object> entry : properties.entrySet())
            {
                String strKey = entry.getKey();
                Object objValue = entry.getValue();
                String strValue = null;
                if (objValue != null)
                    strValue = objValue.toString();
                if (strValue != null)
//x                    if (strValue.length() > 0)
                        sbJnlpURL.append("&" + strKey + "=" + URLEncoder.encode(strValue, DBConstants.URL_ENCODING));
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return sbJnlpURL.toString();
    }

//  Helpers
////////////////////////////////////////// //
     /**
      * Is this recordowner a batch process, or an interactive screen?
      * @return True if this is a batch process.
      */
     public boolean isBatch()
     {
         return true;   // Yes, XML and HTML components are 'batch'
     }
}
