package org.jbundle.thin.base.screen.grid;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JTable;
import javax.swing.SwingUtilities;

public class TraversableGridPolicy extends AbstractAction
{
    private static final long serialVersionUID = 1L;
    
    TraversableGrid screen = null;
    boolean wasEditingWithFocus = true;
    
    public TraversableGridPolicy(TraversableGrid screen)
    {
        this.screen = screen;
    }
    public static void addGridTraversalPolicy(JTable table, TraversableGrid grid)
    {
        Action action = new TraversableGridPolicy(grid);
        table.getActionMap().put("selectNextColumnCell", action);
        table.getActionMap().put("selectPreviousColumnCell", action);        
    }
    @Override
    public void actionPerformed(ActionEvent e) {
         if ((e.getModifiers() & KeyEvent.SHIFT_MASK) == 0)
             this.nextCell(+1);
         else
             this.nextCell(-1);
    }
    public void nextCell(int direction)
    {
        JTable table = (JTable)screen.getControl();
        int currentColumn = table.getSelectedColumn();
        int columns = table.getColumnCount();

        int col = currentColumn + direction;
        for (; col != currentColumn; col = col + direction)
        {
            if (col == -1)
                col = columns - 1;
            else if (col == columns)
                col = 0;
            if (screen.isFocusTarget(col))
                break;
        }
        int rows = table.getRowCount();
        int row = table.getSelectedRow();
        if (col > currentColumn)
            if (direction == -1)
                if (row > 0)
                    row--;
        if (col < currentColumn)
            if (direction == 1)
                if (row < rows - 1)
                    row++;
        table.changeSelection(row, col, false, false);
        
        if (wasEditingWithFocus) {
            table.editCellAt(row, col);
            final Component editorComp = table.getEditorComponent();
            if (editorComp != null) {
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        editorComp.requestFocus();
                    }
                });
            }
        }
     }
};
