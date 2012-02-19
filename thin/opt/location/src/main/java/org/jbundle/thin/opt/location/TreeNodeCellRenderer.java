/*
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.thin.opt.location;

/**
 * @(#)SampleTreeCellRenderer.java  1.11 99/04/23
 */

import java.awt.Component;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeCellRenderer;

public class TreeNodeCellRenderer extends JLabel
    implements TreeCellRenderer
{
    private static final long serialVersionUID = 1L;

    /** Icon to use when the item is collapsed. */
    static protected ImageIcon        collapsedIcon;
    /** Icon to use when the item is expanded. */
    static protected ImageIcon        expandedIcon;

    /** Whether or not the item that was last configured is selected. */
    protected boolean            selected;

    /**
      * This is messaged from JTree whenever it needs to get the size
      * of the component or it wants to draw it.
      * This attempts to set the font based on value, which will be
      * a TreeNode.
      */
    public Component getTreeCellRendererComponent(JTree tree, Object value,
                      boolean selected, boolean expanded,
                      boolean leaf, int row,
                          boolean hasFocus) {
        String stringValue = tree.convertValueToText(value, selected, expanded, leaf, row, hasFocus);

        /** Set the text. */
        this.setText(stringValue);
        /** Tooltips used by the tree. */
//      this.setToolTipText(stringValue);

        /** Set the image. */
    /**   if(expanded)
            setIcon(expandedIcon);
        else if(!leaf)
            setIcon(collapsedIcon);
        else
            setIcon(null);
    */
        /** Set the color and the font based on the SampleData userObject. */
        NodeData userObject = (NodeData)((DefaultMutableTreeNode)value).getUserObject();
        /** Update the selected flag for the next paint. */
        this.selected = selected;

        if (selected)
            this.setOpaque(true);
        else
            this.setOpaque(false);

        return this;
    }

    /**
      * paint is subclassed to draw the background correctly.  JLabel
      * currently does not allow backgrounds other than white, and it
      * will also fill behind the icon.  Something that isn't desirable.
      */
/**    public void paint(Graphics g) {
    Color            bColor;
    Icon             currentI = getIcon();

    if (selected)
    {
//      bColor = SelectedBackgroundColor;
    }
    else if(getParent() != null)
        // Pick background color up from parent (which will come from the JTree we're contained in).
        bColor = getParent().getBackground();
    else
        bColor = getBackground();
//  g.setColor(bColor);
    if(currentI != null && getText() != null) {
        int          offset = (currentI.getIconWidth() + getIconTextGap());

        g.fillRect(offset, 0, getWidth() - 1 - offset,
               getHeight() - 1);
    }
    else
        g.fillRect(0, 0, getWidth()-1, getHeight()-1);
    super.paint(g);
    }
*/
}
