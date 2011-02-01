package org.jbundle.thin.opt.location;/**
 * @(#)DynamicTreeNode.java   1.6 99/04/23
 */

import javax.swing.tree.DefaultMutableTreeNode;

import org.jbundle.thin.base.db.FieldList;
import org.jbundle.thin.base.db.FieldTable;

/**
  * DynamicTreeNode illustrates one of the possible ways in which dynamic
  * loading can be used in tree.  The basic premise behind this is that
  * getChildCount() will be messaged from JTreeModel before any children
  * are asked for.  So, the first time getChildCount() is issued the
  * children are loaded.<p>
  */

public class DynamicTreeNode extends DefaultMutableTreeNode
{
    private static final long serialVersionUID = 1L;

    /** Number of names. */
    protected float m_fNameCount;

    /** Have the children of this node been loaded yet? */
    protected boolean m_bHasLoaded = false;
    
    /**
      * Constructs a new DynamicTreeNode instance with o as the user object.
      */
    public DynamicTreeNode(Object objUser)
    {
        super(objUser);
    }
    /**
     * Fake the caller into believing this is a leaf, when I don't really know yet.
     */
    public boolean isLeaf()
    {
        NodeData data = (NodeData)this.getUserObject();
        if (data != null)
            return data.isLeaf();
        return false;
    }
    /**
      * If hasLoaded is false, meaning the children have not yet been
      * loaded, loadChildren is messaged and super is messaged for
      * the return value.
      */
    public int getChildCount()
    {
        if(!m_bHasLoaded)
        {
            NodeData data = (NodeData)this.getUserObject();
            if ((data == null) || (!data.isLeaf()))
                this.loadChildren();
        }
        return super.getChildCount();
    }
    /**
      * Messaged the first time getChildCount is messaged.  Creates
      * children with random names from names.
      */
    protected void loadChildren()
    {
        DynamicTreeNode     newNode;
//        Font                font;
//        int                 randomIndex;

        NodeData dataParent = (NodeData)this.getUserObject();
        FieldList fieldList = dataParent.makeRecord();
        FieldTable fieldTable = fieldList.getTable();

        m_fNameCount = 0;
        try   {
            fieldTable.close();
//          while (fieldTable.hasNext())
            while (fieldTable.next() != null)
            {
                String objID = fieldList.getField(0).toString();
                String strDescription = fieldList.getField(3).toString();
                NodeData data = new NodeData(dataParent.getBaseApplet(), dataParent.getRemoteSession(), strDescription, objID, dataParent.getSubRecordClassName());
                newNode = new DynamicTreeNode(data);
                /** Don't use add() here, add calls insert(newNode, getChildCount())
                  so if you want to use add, just be sure to set hasLoaded = true first. */
                this.insert(newNode, (int)m_fNameCount);
                m_fNameCount++;
            }
        } catch (Exception ex)  {
            ex.printStackTrace();
        }
        /** This node has now been loaded, mark it so. */
        m_bHasLoaded = true;
    }
}
