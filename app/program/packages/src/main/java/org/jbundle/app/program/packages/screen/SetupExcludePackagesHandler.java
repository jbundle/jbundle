/**
 * @(#)SetupExcludePackagesHandler.
 * Copyright © 2013 jbundle.org. All rights reserved.
 * GPL3 Open Source Software License.
 */
package org.jbundle.app.program.packages.screen;

import java.util.*;

import org.jbundle.base.db.*;
import org.jbundle.thin.base.util.*;
import org.jbundle.thin.base.db.*;
import org.jbundle.base.db.event.*;
import org.jbundle.base.db.filter.*;
import org.jbundle.base.field.*;
import org.jbundle.base.field.convert.*;
import org.jbundle.base.field.event.*;
import org.jbundle.base.model.*;
import org.jbundle.base.util.*;
import org.jbundle.model.*;
import org.jbundle.model.db.*;
import org.jbundle.model.screen.*;
import org.jbundle.app.program.packages.db.*;
import java.util.*;

/**
 *  SetupExcludePackagesHandler - .
 */
public class SetupExcludePackagesHandler extends FileListener
{
    protected XmlField m_fldExcludePackages = null;
    protected Record m_recPackagesTree = null;
    protected Record m_recPackagesExclude = null;
    /**
     * Default constructor.
     */
    public SetupExcludePackagesHandler()
    {
        super();
    }
    /**
     * SetupExcludePackagesHandler Method.
     */
    public SetupExcludePackagesHandler(BaseField fldExcludePackages)
    {
        this();
        this.init(fldExcludePackages);
    }
    /**
     * Initialize class fields.
     */
    public void init(BaseField fldExcludePackages)
    {
        m_fldExcludePackages = null;
        m_recPackagesTree = null;
        m_recPackagesExclude = null;
        m_fldExcludePackages = (XmlField)fldExcludePackages;
        super.init(null);
    }
    /**
     * Free Method.
     */
    public void free()
    {
        if (m_recPackagesTree != null)
            m_recPackagesTree.free();
        m_recPackagesTree = null;
        if (m_recPackagesExclude != null)
            m_recPackagesExclude.free();
        m_recPackagesExclude = null;
        super.free();
    }
    /**
     * Called when a valid record is read from the table/query.
     * @param bDisplayOption If true, display any changes.
     */
    public void doValidRecord(boolean bDisplayOption)
    {
        if (m_recPackagesExclude == null)
        {
            RecordOwner recordOwner = this.getOwner().findRecordOwner();
            m_recPackagesExclude = new Packages(recordOwner);
            if (recordOwner != null)
                recordOwner.removeRecord(m_recPackagesExclude);
        }
        
        StringBuffer sb = new StringBuffer();
        sb.append("<excludes>\n");
        this.scanTreeForExcludes(sb, this.getOwner().getField(Packages.ID).toString());
        sb.append("</excludes>");
        
        m_fldExcludePackages.setString(sb.toString());
            
        super.doValidRecord(bDisplayOption);
    }
    /**
     * ScanTreeForExcludes Method.
     */
    public void scanTreeForExcludes(StringBuffer sb, String strParentFolderID)
    {
        m_recPackagesExclude.setKeyArea(Packages.PARENT_FOLDER_ID_KEY);
        StringSubFileFilter listener = null;
        m_recPackagesExclude.addListener(listener = new StringSubFileFilter(strParentFolderID, Packages.PARENT_FOLDER_ID, null, null, null, null));
        try {
            java.util.List<String> list = new ArrayList<String>();
            m_recPackagesExclude.close();
            while (m_recPackagesExclude.hasNext())
            {
                m_recPackagesExclude.next();
                if (m_recPackagesExclude.getField(Packages.EXCLUDE).getState() == true)
                    sb.append("<exclude>" + this.getTree(m_recPackagesExclude) + "</exclude>\n");
                list.add(m_recPackagesExclude.getField(Packages.ID).toString());
            }
            m_recPackagesExclude.removeListener(listener, true);
            listener = null;
            for (String strFolderID : list)
            {
                this.scanTreeForExcludes(sb, strFolderID);
            }
        } catch (DBException ex) {
            ex.printStackTrace();
            if (listener != null)
                m_recPackagesExclude.removeListener(listener, true);
        }
    }
    /**
     * GetTree Method.
     */
    public String getTree(Record recPackages)
    {
        try {
            if (m_recPackagesTree == null)
            {
                RecordOwner recordOwner = this.getOwner().findRecordOwner();
                m_recPackagesTree = new Packages(recordOwner);
                if (recordOwner != null)
                    recordOwner.removeRecord(m_recPackagesTree);
            }
            String strPackagesTree = recPackages.getField(Packages.NAME).toString();
            m_recPackagesTree.addNew();
            m_recPackagesTree.getField(Packages.ID).moveFieldToThis(recPackages.getField(Packages.PARENT_FOLDER_ID));
            while ((m_recPackagesTree.getField(Packages.ID).getValue() > 0) && (m_recPackagesTree.seek(null)))
            {
                strPackagesTree = m_recPackagesTree.getField(Packages.NAME).toString() + '.' + strPackagesTree;
                m_recPackagesTree.getField(Packages.ID).moveFieldToThis(m_recPackagesTree.getField(Packages.PARENT_FOLDER_ID));
            }
            return strPackagesTree;
            
        } catch (DBException ex) {
            ex.printStackTrace();
            return null;
        }
    }

}
