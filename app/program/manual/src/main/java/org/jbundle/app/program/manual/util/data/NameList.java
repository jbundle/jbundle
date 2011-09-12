/*
 * Copyright © 2011 jbundle.org. All rights reserved.
 */
package org.jbundle.app.program.manual.util.data;

/**
 *  WriteJava
 *  Copyright (c) 2005 jbundle.org. All rights reserved.
 */
import java.util.Enumeration;
import java.util.Vector;

//*******************************************************************
//  TNameList - List of names
//*******************************************************************
//
public class NameList extends Vector<String>
{
    private static final long serialVersionUID = 1L;

    public NameList()
    {
        super();
    }
    public boolean addName(String name)
    {   // Return false if this name already exists, otherwise add to list and return true
        boolean flag = this.findName(name);
        if (flag)
            this.addElement(name);  // Add this element
        return flag;                // Return flag
    }
    public boolean findName(String name)
    {   // Return false if this name already exists, otherwise return true
        for  (Enumeration<String> e = this.elements() ; e.hasMoreElements() ;)
        {
            String strName = (String)e.nextElement();
            if (strName.equalsIgnoreCase(name))
                return false;
        }
        return true;                // Name not found!!!
    }
    public void free()
    {
    }
}
