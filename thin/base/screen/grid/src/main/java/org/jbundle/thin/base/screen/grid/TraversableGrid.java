package org.jbundle.thin.base.screen.grid;

import java.awt.Component;

public interface TraversableGrid
{
    public Component getControl();
    public boolean isFocusTarget(int col);        
};
