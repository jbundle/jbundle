/**
 * @(#)ProjectTaskParentFilter.
 * Copyright © 2013 jbundle.org. All rights reserved.
 * GPL3 Open Source Software License.
 */
package org.jbundle.app.program.project.db;

import java.util.*;

import org.bson.Document;
import org.jbundle.base.db.*;
import org.jbundle.base.db.filter.*;
import org.jbundle.base.field.*;
import org.jbundle.base.model.*;

/**
 *  ProjectTaskParentFilter - This filter only allows records that are this record or ancestors of this record.
 */
public class ProjectTaskParentFilter extends FileFilter
{
    protected ProjectTask m_recProjectTaskParent = null;
    /**
     * Default constructor.
     */
    public ProjectTaskParentFilter()
    {
        super();
    }
    /**
     * ProjectTaskParentFilter Method.
     */
    public ProjectTaskParentFilter(ProjectTask recProjectTaskParent)
    {
        this();
        this.init(recProjectTaskParent);
    }
    /**
     * Initialize class fields.
     */
    public void init(ProjectTask recProjectTaskParent)
    {
        m_recProjectTaskParent = null;
        super.init(null);
        m_recProjectTaskParent = recProjectTaskParent;
    }
    /**
     * Set up/do the local criteria.
     * @param strbFilter The SQL query string to add to.
     * @param bIncludeFileName Include the file name with this query?
     * @param vParamList The param list to add the raw data to (for prepared statements).
     * @param doc
     * @return True if you should not skip this record (does a check on the local data).
     */
    public boolean doLocalCriteria(StringBuffer strbFilter, boolean bIncludeFileName, Vector vParamList, Document doc)
    {
        Record recProjectTask = this.getOwner();
        while (recProjectTask != null)
        {
            if (recProjectTask.getField(ProjectTask.ID).equals(m_recProjectTaskParent.getField(ProjectTask.ID)))
                break;   // Match! This record is my target
            if (recProjectTask.getField(ProjectTask.PARENT_PROJECT_TASK_ID).equals(m_recProjectTaskParent.getField(ProjectTask.ID)))
                break;   // Match! This record has my target as a ancestor
            recProjectTask = ((ReferenceField)recProjectTask.getField(ProjectTask.PARENT_PROJECT_TASK_ID)).getReference();
            if ((recProjectTask == null)
                    || ((recProjectTask.getEditMode() == DBConstants.EDIT_NONE) || (recProjectTask.getEditMode() == DBConstants.EDIT_ADD)))
                return false;  // No match
        }
        
        return super.doLocalCriteria(strbFilter, bIncludeFileName, vParamList, doc); // Match
    }

}
