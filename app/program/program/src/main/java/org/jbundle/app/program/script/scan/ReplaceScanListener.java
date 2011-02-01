/**
 *  @(#)BaseScanListener.
 *  Copyright © 2010 tourapp.com. All rights reserved.
 */
package org.jbundle.app.program.script.scan;

import org.jbundle.app.program.manual.convert.ConvertCode;
import org.jbundle.model.RecordOwnerParent;

/**
 *  BaseScanListener - .
 */
public class ReplaceScanListener extends BaseScanListener
{
    /**
     * Default constructor.
     */
    public ReplaceScanListener()
    {
        super();
    }
    /**
     * Constructor.
     */
    public ReplaceScanListener(RecordOwnerParent parent, String strSourcePrefix)
    {
        this();
        this.init(parent, strSourcePrefix);
    }
    /**
     * Init Method.
     */
    public void init(RecordOwnerParent parent, String strSourcePrefix)
    {
        super.init(parent, strSourcePrefix);
    }
    /**
     * Free Method.
     */
    public void free()
    {
        super.free();
    }
    /**
     * Do any string conversion on the file text.
     */
    public String convertString(String string)
    {
        return ((ConvertCode)m_parent).convertString(string);
    }

}
