/*
 *  @(#)SharedFileHandler.
 * Copyright © 2011 jbundle.org. All rights reserved.
 */
package org.jbundle.base.db.shared;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Vector;

import org.jbundle.base.db.event.FileListener;
import org.jbundle.base.db.filter.FileFilter;
import org.jbundle.base.field.BaseField;
import org.jbundle.base.field.event.InitOnceFieldHandler;
import org.jbundle.base.model.DBConstants;


/**
 *  SharedFileHandler - .
 */
public class SharedFileHandler extends FileFilter
{
    /**
     * Target value for this field.
     */
    protected int m_iTargetValue;
    /**
     * Record type field (sequence).
     */
    protected int m_iTypeField;
    /**
     * Record type field (sequence).
     */
    protected String typeFieldName = null;
    
    /**
     * Default constructor.
     */
    public SharedFileHandler()
    {
        super();
    }
    /**
     * SharedFileHandler Method.
     */
    public SharedFileHandler(int iTypeField, int iTargetValue)
    {
        this();
        this.init(iTypeField, null, iTargetValue);
    }
    /**
     * SharedFileHandler Method.
     */
    public SharedFileHandler(String typeFieldName, int iTargetValue)
    {
        this();
        this.init(-1, typeFieldName, iTargetValue);
    }
    /**
     * Initialize class fields.
     */
    public void init(int iTypeField, String typeFieldName, int iTargetValue)
    {
        m_iTargetValue = 0;
        m_iTypeField = 0;
        m_iTypeField = iTypeField;
        this.typeFieldName = typeFieldName;
        m_iTargetValue = iTargetValue;

        super.init(null);

        this.setMasterSlaveFlag(FileListener.RUN_IN_SLAVE | FileListener.RUN_IN_MASTER);   // This runs on the slave (if there is a slave)
    }
    /**
     * DoNewRecord Method.
     */
    public void doNewRecord(boolean bDisplayOption)
    {
        super.doNewRecord(bDisplayOption);
        BaseField fldTarget = null;
        if (typeFieldName != null)
            fldTarget = this.getOwner().getField(typeFieldName);
        else
            fldTarget = this.getOwner().getField(m_iTypeField);
        boolean[] rgbEnabled = fldTarget.setEnableListeners(false);
        InitOnceFieldHandler listener = (InitOnceFieldHandler)fldTarget.getListener(InitOnceFieldHandler.class.getName());
        if (listener != null)
            listener.setFirstTime(true);    // Special case - you shouldn't have put this listener here, but since you did...
        fldTarget.setValue(m_iTargetValue, DBConstants.DISPLAY, DBConstants.INIT_MOVE);
        fldTarget.setModified(false);
        fldTarget.setEnableListeners(rgbEnabled);
    }
    /**
     * DoValidRecord Method.
     */
    public void doValidRecord(boolean bDisplayOption)
    {
        super.doValidRecord(bDisplayOption);
    }
    /**
     * Add the criteria to the SQL string.
     */
    public boolean doRemoteCriteria(StringBuffer strbFilter, boolean bIncludeFileName, Vector<BaseField> vParamList)
    {
        BaseField fldTarget = null;
        if (typeFieldName != null)
            fldTarget = this.getOwner().getField(typeFieldName);
        else
            fldTarget = this.getOwner().getField(m_iTypeField);
        String strToCompare = Integer.toString(m_iTargetValue);
        boolean bDontSkip = this.fieldCompare(fldTarget, strToCompare, DBConstants.EQUALS, strbFilter, bIncludeFileName, vParamList);
        if (strbFilter != null)
            bDontSkip = true; // Don't need to compare, if I'm creating a filter to pass to SQL 
        if (bDontSkip)
            return super.doRemoteCriteria(strbFilter, bIncludeFileName, vParamList);    // Dont skip this record
        else
            return false;   // Skip this one
    }
    /**
     * InitRemoteStub Method.
     */
    public void initRemoteStub(ObjectOutputStream daOut)
    {
        try   {
            daOut.writeInt(m_iTypeField);
            daOut.writeUTF(typeFieldName);
            daOut.writeInt(m_iTargetValue);
        } catch (IOException ex)    {
            ex.printStackTrace();
        }
    }
    /**
     * InitRemoteSkel Method.
     */
    public void initRemoteSkel(ObjectInputStream daIn)
    {
        try   {
            int iTypeField = daIn.readInt();
            String typeFieldName = daIn.readUTF();
            int iTargetValue = daIn.readInt();
        
            this.init(iTypeField, typeFieldName, iTargetValue);
        } catch (IOException ex)    {
            ex.printStackTrace();
        }
    }

}
