package org.jbundle.base.screen.model.report;

/**
* @(#)Screen.java 0.00 12-Feb-97 Don Corley
*
* Copyright (c) 2009 tourapp.com. All Rights Reserved.
*   don@tourgeek.com
*/
import java.util.Map;

import org.jbundle.base.db.KeyArea;
import org.jbundle.base.db.Record;
import org.jbundle.base.field.BaseField;
import org.jbundle.base.field.DateTimeField;
import org.jbundle.base.field.NumberField;
import org.jbundle.base.screen.model.BasePanel;
import org.jbundle.base.screen.model.SButtonBox;
import org.jbundle.base.screen.model.ScreenField;
import org.jbundle.base.screen.model.util.ScreenLocation;
import org.jbundle.base.util.DBConstants;
import org.jbundle.base.util.DBParams;
import org.jbundle.base.util.ScreenConstants;
import org.jbundle.model.DBException;
import org.jbundle.thin.base.db.Converter;

/**
 * This is the base screen for reports.
 * This class can simply output the record in report format.
 */
public class AnalysisScreen extends ReportScreen
{
    public static final int SUMMARY = 0;
    public static final int BASIS = 1;

    /**
     * Constructor.
     */
    public AnalysisScreen()
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
    public AnalysisScreen(Record mainFile, ScreenLocation itsLocation, BasePanel parentScreen, Converter fieldConverter, int iDisplayFieldDesc, Map<String, Object> properties)
    {
        this();
        this.init(mainFile, itsLocation, parentScreen, fieldConverter, iDisplayFieldDesc, properties);
    }
    /**
     * Initialize.
     * @param record The main record for this screen.
     * @param itsLocation The location of this component within the parent.
     * @param parentScreen The parent screen.
     * @param fieldConverter The field this screen field is linked to.
     * @param iDisplayFieldDesc Do I display the field desc?
     */
    public void init(Record record, ScreenLocation itsLocation, BasePanel parentScreen, Converter fieldConverter, int iDisplayFieldDesc, Map<String, Object> properties)
    {
        super.init(record, itsLocation, parentScreen, fieldConverter, iDisplayFieldDesc, properties);
    }
    /**
     * Free.
     */
    public void free()
    {
        super.free();
    }
    /**
     * Just before the view prints out the screen.
     * This is a good time to adjust the variables or screen fields before printing.
     * (Called from the view in the printScreen method).
     */
    public void prePrintReport()
    {
        // First step - Get the source and analysis files.
        Record recBasis = this.getBasisRecord();        // Record to analyze
        Record recSummary = this.getSummaryRecord();    // Record to summarize the data into
        if (recSummary == null)
            recSummary = this.makeDefaultAnalysisRecord(recBasis);

        if (recSummary.getCounterField() == recSummary.getKeyArea().getField(0))
            if (recSummary.getKeyAreaCount() > 1)
                recSummary.setKeyArea(DBConstants.MAIN_KEY_FIELD + 1);
        // Step 2 - Get the field mappings (keys and summary fields).
        BaseField[][] mxKeyFields = this.getKeyMap(recSummary, recBasis);
        BaseField[][] mxDataFields = this.getDataMap(recSummary, recBasis);
        // Step 3 - Read through the source file and update the summary file.
        try{
            while (recBasis.hasNext())
            {
                recBasis.next();
                this.addSummary(recSummary, mxKeyFields, mxDataFields);
            }
        } catch (DBException ex)    {
            ex.printStackTrace();
        }
    }
    /**
     * Add/update this summary record.
     * @param recSummary The destination summary record.
     * @param mxKeyFields The key fields map.
     * @param mxDataFields The data fields map.
     */
    public void addSummary(Record recSummary, BaseField[][] mxKeyFields, BaseField[][] mxDataFields)
    {
        try {
            recSummary.addNew();
            // First move the key to see if a record exists
            this.setupSummaryKey(mxKeyFields);
            boolean bSuccess = recSummary.seek("=");
            if (bSuccess)
                recSummary.edit();
            else
            {
                recSummary.addNew();
                this.setupSummaryKey(mxKeyFields);
            }
            this.addSummaryData(mxDataFields);
            if (bSuccess)
                recSummary.set();
            else
                recSummary.add();
        } catch (DBException ex)    {
            ex.printStackTrace();
        }
    }
    /**
     * Create a record to do the data analysis with.
     * @param recBasis The record that will be analyzed.
     * @return The new record that will be used for analysis.
     */
    public Record makeDefaultAnalysisRecord(Record recBasis)
    {   // Set one up from scratch using the output params
        Record recSummary = new AnalysisRecord(this);
                
        KeyArea keyArea = recSummary.makeIndex(DBConstants.UNIQUE, "SummaryKey");
        boolean bAddKeyField = true;
        for (int i = 0; i < this.getSourceFieldCount(); i++)
        {
            BaseField field = this.getSourceField(i);
            if (field == null)
                continue;
            try {
                BaseField fldSummary = BaseField.cloneField(field);
                if (field.getRecord() != recBasis)
                {
                    String strRecord = field.getRecord().getRecordName();
                    if (field.getRecord() == this.getScreenRecord())
                        strRecord = "ScreenRecord";
                    fldSummary.setFieldName(strRecord + '.' + field.getFieldName());
                }
                if (field == field.getRecord().getCounterField())
                {
                    fldSummary.setFieldName("Count");
                    fldSummary.setFieldDesc("Count");
                }
                recSummary.addField(fldSummary);
                if (!this.isKeyField(fldSummary, i))
                    if (i != 0)
                        bAddKeyField = false;
                if (bAddKeyField)
                    keyArea.addKeyField(fldSummary, DBConstants.ASCENDING);
            } catch (CloneNotSupportedException ex) {
                ex.printStackTrace();   // Never
            }
        }
        recSummary.setKeyArea(AnalysisRecord.kIDKey + 1);
        // Get rid of the current screen controls.
        for (int i = this.getSFieldCount() - 1; i >= 0 ; i--)
        {
            ScreenField sField = this.getSField(i);
            if (!(sField instanceof BasePanel))
                sField.free();
        }
        int iToolbars = this.getSFieldCount();
        // Add the new screen controls.
        for (int i = AnalysisRecord.kID + 1; i < recSummary.getFieldCount(); i++)
        {
            recSummary.getField(i).setupDefaultView(this.getNextLocation(ScreenConstants.NEXT_LOGICAL, ScreenConstants.ANCHOR_DEFAULT), this, ScreenConstants.DEFAULT_DISPLAY);
        }
        while (iToolbars > 0)
        {       // Move the toolbars up
            ScreenField sField = this.getSField(0);
            this.removeSField(sField);
            this.addSField(sField);
            iToolbars--;
        }
        // New main record!
        this.addRecord(recSummary, true);
        
        return recSummary;
    }
    /**
     * Get the count of source fields (From the selection).
     * @return The count.
     */
    public int getSourceFieldCount()
    {
        int iCount = 0;
        for (int i = 0; i < this.getSFieldCount(); i++)
        {
            ScreenField sField = this.getSField(i);
            if (!(sField instanceof BasePanel))
                iCount++;
        }
        return iCount;
    }
    /**
     * Get the source field to analyze at this position (From the selection).
     * @param iSeq location of the source field.
     * @return The field.
     */
    public BaseField getSourceField(int iSeq)
    {
        for (int i = 0; i < this.getSFieldCount(); i++)
        {
            ScreenField sField = this.getSField(i);
            if (sField instanceof BasePanel)
                continue;
            if (iSeq == 0)
            {
                if ((sField.getConverter() == null)
                    || (sField.getConverter().getField() == null))
                        return null;
                if (sField instanceof SButtonBox)
                    return null;
                BaseField field = (BaseField)sField.getConverter().getField();
                return field;
            }
            iSeq--;
        }
        return null;
    }
    /**
     * Create a map of the source and destination key fields.
     * @param recSummary The destination (analysis) record.
     * @param recBasic The source record (to analyze).
     * @return A mapping of the source and dest key fields.
     */
    public BaseField[][] getKeyMap(Record recSummary, Record recBasis)
    {
        BaseField[][] mxKeyFields = new BaseField[recSummary.getKeyArea().getKeyFields()][2];
        for (int i = 0; i < mxKeyFields.length; i++)
        {
            mxKeyFields[i][SUMMARY] = recSummary.getKeyArea().getField(i);
            mxKeyFields[i][BASIS] = this.getBasisField(mxKeyFields[i][SUMMARY], recBasis, i);
        }
        return mxKeyFields;
    }
    /**
     * Create a map of the source and destination data fields.
     * @param recSummary The destination (analysis) record.
     * @param recBasic The source record (to analyze).
     * @return A mapping of the source and dest non-key fields.
     */
    public BaseField[][] getDataMap(Record recSummary, Record recBasis)
    {
        int iFieldSeq = 0;
        if (recSummary.getField(iFieldSeq) == recSummary.getCounterField())
            iFieldSeq++;
        int iLength = recSummary.getFieldCount();
        iLength = iLength - recSummary.getKeyArea().getKeyFields() - iFieldSeq;
        BaseField[][] mxDataFields = new BaseField[iLength][2];
        for (int i = 0; i < mxDataFields.length; i++)
        {
            mxDataFields[i][SUMMARY] = recSummary.getField(iFieldSeq);
            mxDataFields[i][BASIS] = this.getBasisField(mxDataFields[i][SUMMARY], recBasis, i);
            iFieldSeq++;
            for (int j = 0; j < recSummary.getKeyArea().getKeyFields(); j++)
            {
                if (mxDataFields[i][SUMMARY] == recSummary.getKeyArea().getField(j))
                {
                    i--;    // Skip this one, it is in the key area.
                    break;
                }
            }
        }
        return mxDataFields;
    }
    /**
     * Get the matching basis field given the summary field.
     * Override this if you don't want the defaults.
     * @param fldSummary The summary field to match.
     * @param recBasic The basis record.
     * @param iSummarySeq The position in the summary record.
     * @return The basis field.
     */
    public BaseField getBasisField(BaseField fldSummary, Record recBasis, int iSummarySeq)
    {
        BaseField fldBasis = null;
        String strFieldName = fldSummary.getFieldName();
        if ((strFieldName != null) && (strFieldName.indexOf('.') != -1))
        {
            Record record = this.getRecord(strFieldName.substring(0, strFieldName.indexOf('.')));
            if ((strFieldName.indexOf('.') == 0) || ("ScreenRecord".equalsIgnoreCase(strFieldName.substring(0, strFieldName.indexOf('.')))))
                record = this.getScreenRecord();
            fldBasis = record.getField(strFieldName.substring(strFieldName.indexOf('.') + 1));
        }
        else
            fldBasis = recBasis.getField(strFieldName);
        return fldBasis;
    }
    /**
     * Move the source key fields to the destinataion keys.
     * @param mxKeyFields The key fields to move.
     */
    public void setupSummaryKey(BaseField[][] mxKeyFields)
    {
        for (int i = 0; i < mxKeyFields.length; i++)
        {
            mxKeyFields[i][SUMMARY].moveFieldToThis(mxKeyFields[i][BASIS]);
        }
    }
    /**
     * Move the source data fields to the destinataion data fields.
     * @param mxDataFields The data fields to move.
     */
    public void addSummaryData(BaseField[][] mxDataFields)
    {
        for (int i = 0; i < mxDataFields.length; i++)
        {
            double dAmount = 1;
            if (mxDataFields[i][BASIS] != null)
                dAmount = mxDataFields[i][BASIS].getValue();
            mxDataFields[i][SUMMARY].setValue(mxDataFields[i][SUMMARY].getValue() + dAmount);
        }
    }
    /**
     * This is field a potential index for analysis.
     * (Override this to allow number fields to be keys).
     * @param field The field to check.
     * @return True if this is a potential key field.
     */
    public boolean isKeyField(BaseField field, int iSourceFieldSeq)
    {
        if (field instanceof DateTimeField)
            return true;
        if (field instanceof NumberField)
            return false;
        if (iSourceFieldSeq == 0)
            return false;   // You must have at least one key field.
        return true;   // Typically any non-number is a key.
    }
    /**
     * Get the record to analyze.
     * @return the source record (defaults to the main record).
     */
    public Record getBasisRecord()
    {
        return this.getMainRecord();    // By default
    }
    /**
     * Get the summary file.
     * @return The summary record (if null, this code creates a default record from the source).
     */
    public Record getSummaryRecord()
    {
        return null;
    }
    /**
     * Get the path to the target servlet.
     * By default, use XML.
     * @return the servlet path.
     */
    public String getServletPath(String strServletParam)
    {
        return super.getServletPath(DBParams.XHTMLSERVLET); // Use cocoon
    }
}
