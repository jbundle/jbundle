/**
 * @(#)SetupPackagesTree.
 * Copyright © 2011 jbundle.org. All rights reserved.
 * GPL3 Open Source Software License.
 */
package org.jbundle.app.program.packages.screen;

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
import org.jbundle.base.util.*;
import org.jbundle.model.*;
import org.jbundle.model.db.*;
import org.jbundle.model.screen.*;
import org.jbundle.base.screen.model.report.*;
import org.jbundle.app.program.packages.db.*;
import org.jbundle.app.program.db.*;

/**
 *  SetupPackagesTree - .
 */
public class SetupPackagesTree extends FileListener
{
    protected Packages m_recPackages = null;
    protected BaseField m_fldTargetTree = null;
    /**
     * Default constructor.
     */
    public SetupPackagesTree()
    {
        super();
    }
    /**
     * SetupPackagesTree Method.
     */
    public SetupPackagesTree(BaseField fldTargetTree)
    {
        this();
        this.init(fldTargetTree);
    }
    /**
     * Initialize class fields.
     */
    public void init(BaseField fldTargetTree)
    {
        m_recPackages = null;
        m_fldTargetTree = null;
        m_fldTargetTree = fldTargetTree;
        super.init(null);
    }
    /**
     * Free Method.
     */
    public void free()
    {
        if (m_recPackages != null)
            m_recPackages.free();
        m_recPackages = null;
        super.free();
    }
    /**
     * Called when a valid record is read from the table/query.
     * @param bDisplayOption If true, display any changes.
     */
    public void doValidRecord(boolean bDisplayOption)
    {
        try {
            if (m_recPackages == null)
            {
                RecordOwner recordOwner = Utility.getRecordOwner(this.getOwner());
                m_recPackages = new Packages(recordOwner);
                if (recordOwner != null)
                    recordOwner.removeRecord(m_recPackages);
            }
            String strPackagesTree = this.getOwner().getField(Packages.kName).toString();
            m_recPackages.addNew();
            m_recPackages.getField(Packages.kID).moveFieldToThis(this.getOwner().getField(Packages.kParentFolderID));
            while ((m_recPackages.getField(Packages.kID).getValue() > 0) && (m_recPackages.seek(null)))
            {
                strPackagesTree = m_recPackages.getField(Packages.kName).toString() + '.' + strPackagesTree;
                m_recPackages.getField(Packages.kID).moveFieldToThis(m_recPackages.getField(Packages.kParentFolderID));
            }
            m_fldTargetTree.setString(strPackagesTree);
        } catch (DBException ex) {
            ex.printStackTrace();
        }
        
        super.doValidRecord(bDisplayOption);
    }

}
