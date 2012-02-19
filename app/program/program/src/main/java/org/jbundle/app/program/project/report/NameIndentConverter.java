/**
 * @(#)NameIndentConverter.
 * Copyright © 2012 jbundle.org. All rights reserved.
 * GPL3 Open Source Software License.
 */
package org.jbundle.app.program.project.report;

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
import org.jbundle.base.model.*;
import org.jbundle.base.util.*;
import org.jbundle.model.*;
import org.jbundle.model.db.*;
import org.jbundle.model.screen.*;

/**
 *  NameIndentConverter - .
 */
public class NameIndentConverter extends FieldConverter
{
    protected String gstrSpaces = "\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0";
    protected Converter m_convIndent = null;
    protected int m_iIndentAmount;
    /**
     * Default constructor.
     */
    public NameIndentConverter()
    {
        super();
    }
    /**
     * Constructor.
     */
    public NameIndentConverter(Converter converter, Converter convIndent, int iIndentAmount)
    {
        this();
        this.init(converter, convIndent, iIndentAmount);
    }
    /**
     * Initialize class fields.
     */
    public void init(Converter converter, Converter convIndent, int iIndentAmount)
    {
        m_convIndent = null;
        m_iIndentAmount = 0;
        super.init(converter);
        m_convIndent = convIndent;
        m_iIndentAmount = iIndentAmount;
    }
    /**
     * GetString Method.
     */
    public String getString()
    {
        String string = super.getString();
        int iIndent = (int)m_convIndent.getValue();
        string = gstrSpaces.substring(0, iIndent * m_iIndentAmount) + string;
        return string;
    }

}
