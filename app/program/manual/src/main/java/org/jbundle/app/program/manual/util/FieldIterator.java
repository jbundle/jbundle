/*
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.app.program.manual.util;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Vector;

import org.jbundle.app.program.db.ClassInfo;
import org.jbundle.app.program.db.FieldData;
import org.jbundle.app.program.db.FileHdr;
import org.jbundle.base.db.Record;
import org.jbundle.base.db.filter.StringSubFileFilter;
import org.jbundle.base.db.filter.SubFileFilter;
import org.jbundle.base.model.DBConstants;
import org.jbundle.model.DBException;


/**
 *
 */
 public class FieldIterator extends Object
        implements Iterator<Object>
{
    protected Record m_recFileHdr = null;
    protected ClassInfo m_recClassInfo = null;
    protected Record m_recFieldData = null;

    protected boolean markUnusedFields = false;

    protected String[] m_rgstrClasses = null;

    public static final String UNUSED = "UnusedField";
    private boolean m_bFirstTime = true;
    private Vector<FieldSummary> m_vFieldList = null;
    private int m_iCurrentIndex = 0;
    
    /**
     *
     */
    public FieldIterator()
    {
        super();
    }
    /**
     * Constructor.
     */
    public FieldIterator(Record recFileHdr, ClassInfo recClassInfo, Record recFieldData, boolean markUnusedFields)
    {
        this();
        this.init(recFileHdr, recClassInfo, recFieldData, markUnusedFields);
    }
    /**
     * Constructor.
     */
    public void init(Record recFileHdr, ClassInfo recClassInfo, Record recFieldData, boolean markUnusedFields)
    {
        m_recFileHdr = recFileHdr;
        m_recClassInfo = recClassInfo;
        m_recFieldData = recFieldData;
        
        this.markUnusedFields = markUnusedFields;
        
        int iOldKeyOrder = m_recFileHdr.getDefaultOrder();
        try {
            if (!m_recFileHdr.getField(FileHdr.FILE_NAME).equals(m_recClassInfo.getField(ClassInfo.CLASS_NAME)))
            {
                m_recFileHdr.getField(FileHdr.FILE_NAME).moveFieldToThis(m_recClassInfo.getField(ClassInfo.CLASS_NAME));
                m_recFileHdr.setKeyArea(FileHdr.FILE_NAME_KEY);
                if (!m_recFileHdr.seek("="))
                    m_recFileHdr.addNew();
            }
        } catch (DBException ex)    {
            ex.printStackTrace();
        } finally {
            m_recFileHdr.setKeyArea(iOldKeyOrder);
        }
    }
    /**
     * Constructor.
     */
    public void free()
    {
        m_recClassInfo = null;  // Do not free.
        m_recFieldData = null;
        m_recFileHdr = null;
        
        m_bFirstTime = true;
    }
    /**
     * Removes from the underlying collection the last element returned by the
     * iterator (optional operation).  This method can be called only once per
     * call to <tt>next</tt>.  The behavior of an iterator is unspecified if
     * the underlying collection is modified while the iteration is in
     * progress in any way other than by calling this method.
     *
     * @exception UnsupportedOperationException if the <tt>remove</tt>
     * 		  operation is not supported by this Iterator.
     *
     * @exception IllegalStateException if the <tt>next</tt> method has not
     * 		  yet been called, or the <tt>remove</tt> method has already
     * 		  been called after the last call to the <tt>next</tt>
     * 		  method.
     *
     */
    public void remove()
    {
    }
    /**
     * leave the files alone, but reset the fields to the first one.
     */
    public void close()
    {
        m_iCurrentIndex = 0;
        m_recFieldData.close();
    }
    /** Returns <tt>true</tt> if the iteration has more elements. (In other
     * words, returns <tt>true</tt> if <tt>next</tt> would return an element
     * rather than throwing an exception.)
     *
     * @return <tt>true</tt> if the iterator has more elements.
     *
     */
    public boolean hasNext()
    {
        if (m_bFirstTime)
            this.scanSharedFields();
        return (m_iCurrentIndex < m_vFieldList.size());
    }
    /** Returns the next element in the iteration.
     *
     * @return the next element in the iteration.
     * @exception NoSuchElementException iteration has no more elements.
     *
     */
    public Object next()
    {
        int iOldKeyOrder = m_recFieldData.getDefaultOrder();
        try {
            if (m_bFirstTime)
                this.scanSharedFields();

            if (m_iCurrentIndex >= m_vFieldList.size())
                return null;    // EOF
            FieldSummary fieldSummary = (FieldSummary)m_vFieldList.elementAt(m_iCurrentIndex);
            m_iCurrentIndex++;

            m_recFieldData.setKeyArea(FieldData.ID_KEY);
            m_recFieldData.addNew();
            m_recFieldData.getField(FieldData.kID).setData(fieldSummary.m_objID);
            if (!m_recFieldData.seek(null))
                return null;    // Never
            
            if (fieldSummary.m_strNewBaseField != null)
                m_recFieldData.getField(FieldData.BASE_FIELD_NAME).setString(fieldSummary.m_strNewBaseField);
            if (fieldSummary.m_strNewFieldClass == UNUSED)
            {
                m_recFieldData.getField(FieldData.FIELD_CLASS).setString(fieldSummary.m_strNewFieldClass);
                m_recFieldData.getField(FieldData.DEFAULT_VALUE).setString(DBConstants.BLANK);
                m_recFieldData.getField(FieldData.INITIAL_VALUE).setString(DBConstants.BLANK);
            }
            else if (this.inBaseField(fieldSummary.m_strFieldFileName, m_rgstrClasses))
            {   // This field is in a base record class, so fake the record to think it just overrides the field
                m_recFieldData.addNew();
                m_recFieldData.getField(FieldData.BASE_FIELD_NAME).setString(fieldSummary.m_strFieldName);
                m_recFieldData.getField(FieldData.FIELD_NAME).setString(fieldSummary.m_strFieldName);
                m_recFieldData.getField(FieldData.FIELD_FILE_NAME).setString(m_rgstrClasses[m_rgstrClasses.length-1]);
            }
            m_recFieldData.getField(FieldData.INCLUDE_SCOPE).setValue(fieldSummary.m_iIncludeScope);

            return m_recFieldData;  // This is the next field data record
            
        } catch (DBException ex)    {
            ex.printStackTrace();
            return null;
        } finally {
            m_recFieldData.setKeyArea(iOldKeyOrder);
        }
    }
    /**
     * Is this field in my field list already?
     */
    private boolean inBaseField(String strFieldFileName, String[] rgstrClassNames)
    {
        for (int i = 0; i < rgstrClassNames.length - 1; i++)
        {       // In my base (but not in this (top) class).
            if (strFieldFileName.equals(rgstrClassNames[i]))
                return true;
        }
        return false;
    }
    
    /**
     * Build the field list from the concrete and base record classes.
     */
    private void scanSharedFields()
    {
        if (!m_bFirstTime)
            return;
        m_bFirstTime = false;
        m_vFieldList = new Vector<FieldSummary>();
        m_iCurrentIndex = 0;

        String strClassName = m_recClassInfo.getField(ClassInfo.CLASS_NAME).toString();

        // Step one - Get a list of all the base classes to the origin [0] = VirtualRecord.
        m_rgstrClasses = this.getBaseRecordClasses(strClassName);

        String strBaseSharedRecord = null;
        //if (includeSharedFields == true)
        {
	        // Now add all the classes from the first shared record up the chain
	    	FileHdr recFileHdr = new FileHdr(Record.findRecordOwner(m_recFileHdr));
	    	recFileHdr.setKeyArea(FileHdr.FILE_NAME_KEY);
	        try {
		        for (int i = 0; i < m_rgstrClasses.length; i++)
		        {
		        	recFileHdr.addNew();
		        	recFileHdr.getField(FileHdr.FILE_NAME).setString(m_rgstrClasses[i]);
			        if (recFileHdr.seek(null) == true)  // For shared records, include overriding record fields.
//			        	if (recFileHdr.getField(FileHdr.TYPE).toString().indexOf("SHARED_TABLE") != -1)  // For shared records, include overriding record fields.
			        {
		        		strBaseSharedRecord = m_rgstrClasses[i];
			            break;
			        }
		        }
	        } catch (DBException ex) {
	        	ex.printStackTrace();
	        } finally {
	        	if (recFileHdr != null)
	        		recFileHdr.free();
	        }
        }
        
        for (int i = 0; i < m_rgstrClasses.length; i++)
        {
        	this.scanBaseFields(m_rgstrClasses[i], (i == m_rgstrClasses.length - 1));
        	if (m_rgstrClasses[i].equals(strBaseSharedRecord))
        		break;
        }

        if (strBaseSharedRecord != null)
            this.scanExtendedClasses(strBaseSharedRecord);

        if (strBaseSharedRecord != null)
            if (!strClassName.equals(strBaseSharedRecord))
        {   // This class is not the base class, merge the two
             this.scanRecordsFields(m_rgstrClasses);
        }
    }
    /**
     * Get the hierarchy of classes starting with this class name.
     * @param strClassName The class to start with.
     * @param recClassInfo The class record I can use to look through the classes.
     * @param excludeSharedFields Only return the shared class hierarchy (otherwise the entire hierarchy is returned).
     * @return The class name hierarchy (with position[0] being the lowest and the last is the passing in name).
     */
    private String[] getBaseRecordClasses(String strClassName)
    {
        ClassInfo recClassInfo = new ClassInfo(Record.findRecordOwner(m_recClassInfo));
        String[] rgstrClasses = new String[0];
//        rgstrClasses[0] = strClassName;
        Record recFileHdr = new FileHdr(Record.findRecordOwner(m_recFileHdr));
        try {
            // First, get the base shared record
            String strBaseClass = strClassName;
            while (true)
            {
                recClassInfo.setKeyArea(ClassInfo.CLASS_NAME_KEY);
                recClassInfo.getField(ClassInfo.CLASS_NAME).setString(strBaseClass);
                if (!recClassInfo.seek(null))
                    break;  // No class, Parent is the base.
                if ("Record".equalsIgnoreCase(recClassInfo.getField(ClassInfo.CLASS_NAME).toString()))
                    break;
//                if (!includeBaseFields)
  //              {
    //                recFileHdr.getField(FileHdr.FILE_NAME).moveFieldToThis(recClassInfo.getField(ClassInfo.CLASS_NAME));
      //              recFileHdr.setKeyArea(FileHdr.FILE_NAME_KEY);
        //            if (!recFileHdr.seek("="))
          //              break;  // No file, Parent is the base.
            //        if (recFileHdr.getField(FileHdr.TYPE).toString().indexOf("SHARED_TABLE") == -1)
              //          break;  // Not shared, Parent is the base.
                //}
                strClassName = strBaseClass;    // This is the new valid base.
                strBaseClass = recClassInfo.getField(ClassInfo.BASE_CLASS_NAME).toString();  // Continue down.
                // Now add this base class to the bottom of the list
                String[] rgstrClassesTemp = new String[rgstrClasses.length + 1];
                for (int i = 0; i < rgstrClasses.length; i++)
                {
                    rgstrClassesTemp[i + 1] = rgstrClasses[i];
                }
                rgstrClassesTemp[0] = strClassName; // New base class
                rgstrClasses = rgstrClassesTemp;
            }
        } catch (DBException ex)    {
            ex.printStackTrace();
        } finally {
            recClassInfo.free();
            recFileHdr.free();
        }
        return rgstrClasses;
    }
    /**
     * @param recClassInfo Must have the base class to scan.
     */
    private void scanBaseFields(String strBaseClass, boolean topLevel)
    {
        ClassInfo recClassInfo = new ClassInfo(Record.findRecordOwner(m_recClassInfo));
        FieldData recFieldData = new FieldData(Record.findRecordOwner(m_recFieldData));
        try {
            recClassInfo.setKeyArea(ClassInfo.CLASS_NAME_KEY);
            recClassInfo.getField(ClassInfo.CLASS_NAME).setString(strBaseClass);  // Base class
            recClassInfo.seek(null);    // Read this class.

            recFieldData.setKeyArea(FieldData.FIELD_FILE_NAME_KEY);
            recFieldData.addListener(new SubFileFilter(recClassInfo.getField(ClassInfo.CLASS_NAME), FieldData.FIELD_FILE_NAME, null, null, null, null));
            recFieldData.close();
            while (recFieldData.hasNext())
            {
                recFieldData.next();
                
                FieldSummary fieldSummary = new FieldSummary(recFieldData, FieldSummary.BASE_FIELD);
                
                int iIndex = this.findField(recFieldData, true);    // Find the base field in my list
                if (iIndex == -1)
                {
                    m_vFieldList.add(fieldSummary);  // Add this
                }
                else
                {   // If there is a base, replace it with this
                    FieldSummary fieldSummaryBase = (FieldSummary)m_vFieldList.get(iIndex);
                    m_vFieldList.set(iIndex, fieldSummaryBase.mergeNewSummary(fieldSummary, topLevel, strBaseClass));  // Replace this
                }
            }
        } catch (DBException ex)    {
            ex.printStackTrace();
        } finally {
            recClassInfo.free();
            recFieldData.free();
        }
    }
    /**
     * Go through the fields in this class and add them to the field list.
     * @param strClassName The record class to read through.
     */
    private void scanRecordsFields(String[] strClassNames)
    {
        for (int i = 1; i < strClassNames.length; i++)
        {
            this.scanRecordFields(strClassNames, i, markUnusedFields); // Keep unused base fields.
        }
//x        this.scanRecordFields(strClassNames[strClassNames.length -1], recClassInfo, recFieldData, true);
    }
    /**
     * Go through the fields in this class and add them to the field list.
     * @param strClassName The record class to read through.
     */
    protected void scanRecordFields(String[] strClassNames, int iClassIndex, boolean bMarkUnused)
    {
        ClassInfo recClassInfo = new ClassInfo(Record.findRecordOwner(m_recClassInfo));
        FieldData recFieldData = new FieldData(Record.findRecordOwner(m_recFieldData));
        String strClassName = strClassNames[iClassIndex];
        try {
            // First, re-read the original class.
            recClassInfo.getField(ClassInfo.CLASS_NAME).setString(strClassName);
            recClassInfo.setKeyArea(ClassInfo.CLASS_NAME_KEY);
            if (!recClassInfo.seek(null))
                return; // Never
            
            recFieldData.setKeyArea(FieldData.FIELD_FILE_NAME_KEY);
            recFieldData.addListener(new SubFileFilter(recClassInfo.getField(ClassInfo.CLASS_NAME), FieldData.FIELD_FILE_NAME, null, null, null, null));

            strClassName = recClassInfo.getField(ClassInfo.CLASS_NAME).toString();
            recFieldData.close();
            while (recFieldData.hasNext())
            {
                recFieldData.next();
                
                int iIndex = this.findField(recFieldData, true);    // Find the base field in my list
                FieldSummary fieldSummary = new FieldSummary(recFieldData, FieldSummary.RECORD_FIELD);
                if (iIndex == -1)
                {
                    m_vFieldList.add(fieldSummary);  // Add this
                }
                else
                {   // If there is a base, replace it with this
                    FieldSummary fieldSummaryBase = (FieldSummary)m_vFieldList.get(iIndex);
                    m_vFieldList.set(iIndex, fieldSummaryBase.mergeNewSummary(fieldSummary, (iClassIndex >= strClassNames.length - 1), strClassName));  // Replace this
                }
            }
            
            if (bMarkUnused)
            {
                for (int i = 0; i < m_vFieldList.size(); i++)
                {
                    FieldSummary fieldSummary = (FieldSummary)m_vFieldList.elementAt(i);
                    if (!this.isInRecord(strClassNames, iClassIndex, fieldSummary.m_strFieldFileName))
                    {
                        fieldSummary.m_strNewFieldClass = UNUSED;
                        fieldSummary.m_strNewBaseField = fieldSummary.m_strFieldName;
                    }
                }
            }
        } catch (DBException ex)    {
            ex.printStackTrace();
        } finally {
            recClassInfo.free();
            recFieldData.free();
        }
    }
    /**
     * Is this class one of the base class names for this record?
     */
    private boolean isInRecord(String[] strClassNames, int iClassIndex, String strRecordClass)
    {
        boolean bIsInRecord = false;
        for (int i = 0; i <= iClassIndex; i++)
        {
            if (strClassNames[i].equalsIgnoreCase(strRecordClass))
                bIsInRecord = true;   // True, it IS in the record
        }
        return bIsInRecord;    // It is in the record?
    }
    /**
     *
     */
    private void scanExtendedClasses(String strBaseClass)
    {
        ClassInfo recClassInfo = new ClassInfo(Record.findRecordOwner(m_recClassInfo));
        FieldData recFieldData = new FieldData(Record.findRecordOwner(m_recFieldData));
        FileHdr recFileHdr = new FileHdr(Record.findRecordOwner(m_recFileHdr));
        try {
            recClassInfo.setKeyArea(ClassInfo.CLASS_NAME_KEY);
            recClassInfo.getField(ClassInfo.CLASS_NAME).setString(strBaseClass);  // Base class
            recClassInfo.seek(null);    // Read this class.

            recFieldData.setKeyArea(FieldData.FIELD_FILE_NAME_KEY);
            recFieldData.addListener(new SubFileFilter(recClassInfo.getField(ClassInfo.CLASS_NAME), FieldData.FIELD_FILE_NAME, null, null, null, null));

            recClassInfo.setKeyArea(ClassInfo.BASE_CLASS_NAME_KEY);
            recClassInfo.addListener(new StringSubFileFilter(strBaseClass, ClassInfo.BASE_CLASS_NAME, null, null, null, null));
            
            recClassInfo.close();
            while (recClassInfo.hasNext())
            {
                recClassInfo.next();

                recFileHdr.getField(FileHdr.FILE_NAME).moveFieldToThis(recClassInfo.getField(ClassInfo.CLASS_NAME));
                recFileHdr.setKeyArea(FileHdr.FILE_NAME_KEY);
                if (recFileHdr.seek("=") == false)
            		continue;
                if (recFileHdr.getField(FileHdr.TYPE).toString().indexOf("SHARED_TABLE") == -1)
                    continue;  // Not shared, Parent is the base.

                this.scanExtendedClassFields(recClassInfo, recFieldData);
                
                this.scanExtendedClasses(recClassInfo.getField(ClassInfo.CLASS_NAME).toString());
            }
            
        } catch (DBException ex)    {
            ex.printStackTrace();
        } finally {
            recClassInfo.free();
            recFieldData.free();
            recFileHdr.free();
        }
    }
    /**
     *
     */
    private void scanExtendedClassFields(ClassInfo recClassInfo, FieldData recFieldData)
    {
        try {
            recFieldData.close();
            while (recFieldData.hasNext())
            {
                recFieldData.next();
                
                if (recFieldData.getField(FieldData.BASE_FIELD_NAME).isNull())
                {   // New name
                    FieldSummary fieldSummary = new FieldSummary(recFieldData, FieldSummary.EXTENDED_FIELD);

                    m_vFieldList.add(fieldSummary);
                }
            }
            
        } catch (DBException ex)    {
            ex.printStackTrace();
        }
    }
    /**
     *
     */
    private int findField(Record recFieldData, boolean bInBaseField)
    {
        String strFieldName = recFieldData.getField(FieldData.FIELD_NAME).toString();
        String strBaseFieldName = recFieldData.getField(FieldData.BASE_FIELD_NAME).toString();
//?        String strSourceRecord = recFieldData.getField(FieldData.FIELD_FILE_NAME).toString();
        for (int i = 0; i < m_vFieldList.size(); i++)
        {
            FieldSummary fieldData = (FieldSummary)m_vFieldList.elementAt(i);
            if (!bInBaseField)
            {
                if (strFieldName.equalsIgnoreCase(fieldData.m_strFieldName))
                    return i;    // found
            }
            else
            {
                if (strBaseFieldName.equalsIgnoreCase(fieldData.m_strFieldName))
                    return i;    // found
//?                if (strSourceRecord.equalsIgnoreCase(fieldData.m_strFieldFileName))
                    if (strFieldName.equalsIgnoreCase(fieldData.m_strFieldName))
                        return i;    // found (only in base because of me)
            }
        }
        return -1;   // Not found
    }
    /**
     *
     */
    class FieldSummary
    {
        public FieldSummary(Record recFieldData, int iFieldType)
        {
            this.m_objID = recFieldData.getField(FieldData.kID).getData();
            this.m_strFieldName = recFieldData.getField(FieldData.FIELD_NAME).toString();
            this.m_strBaseFieldName = recFieldData.getField(FieldData.BASE_FIELD_NAME).toString();
            this.m_strFieldFileName = recFieldData.getField(FieldData.FIELD_FILE_NAME).toString();
            this.m_iIncludeScope = (int)(recFieldData.getField(FieldData.INCLUDE_SCOPE).getValue() + 0.5);
            this.m_iIncludeScopeCumulative = (int)(recFieldData.getField(FieldData.INCLUDE_SCOPE).getValue() + 0.5);
            this.m_iFieldType = iFieldType;
        }
        public FieldSummary mergeNewSummary(FieldSummary fieldSummary, boolean topLevel, String strClassName)
        {
            if (strClassName.equals(this.m_strFieldFileName))
                fieldSummary.m_strNewBaseField = fieldSummary.m_strFieldName;   // Special case - New field in a shared override = override base shared field.
            fieldSummary.m_iFieldType = FieldSummary.RECORD_FIELD;
            if (this.m_iFieldType == FieldSummary.EXTENDED_FIELD)
            {
                fieldSummary.m_iIncludeScope = this.m_iIncludeScopeCumulative;
                fieldSummary.m_iIncludeScopeCumulative = fieldSummary.m_iIncludeScopeCumulative | this.m_iIncludeScope;
            }
            else
            {
                if (!topLevel)
                {
                    fieldSummary.m_iIncludeScope = this.m_iIncludeScopeCumulative;
                    fieldSummary.m_iIncludeScopeCumulative = fieldSummary.m_iIncludeScopeCumulative | this.m_iIncludeScope;
                }
                else
                {
                    fieldSummary.m_iIncludeScopeCumulative = fieldSummary.m_iIncludeScope & ~this.m_iIncludeScopeCumulative;    // Include if it hasn't been included so far
                    fieldSummary.m_iIncludeScope = fieldSummary.m_iIncludeScopeCumulative;
                }
            }
            return fieldSummary;
        }
        public Object m_objID = null;
        public String m_strFieldName = null;
        public String m_strBaseFieldName = null;
        public String m_strFieldFileName = null;

        public String m_strNewBaseField = null;
        public String m_strNewFieldClass = null;
        public int m_iFieldType = -1;
        public int m_iIncludeScope = 0;
        public int m_iIncludeScopeCumulative = 0;
        
        public static final int BASE_FIELD = 1;
        public static final int RECORD_FIELD = 2;
        public static final int EXTENDED_FIELD = 3;
    }
}
