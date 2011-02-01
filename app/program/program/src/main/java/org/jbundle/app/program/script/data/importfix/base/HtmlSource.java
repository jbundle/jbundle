/**
 *  @(#)HtmlSource.
 *  Copyright © 2010 tourapp.com. All rights reserved.
 */
package org.jbundle.app.program.script.data.importfix.base;

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
import java.io.*;
import org.xml.sax.*;

/**
 *  HtmlSource - .
 */
public class HtmlSource extends BaseSource
{
    protected boolean firstTime = true;
    protected SaxHtmlHandler m_handler = null;
    /**
     * Default constructor.
     */
    public HtmlSource()
    {
        super();
    }
    /**
     * HtmlSource Method.
     */
    public HtmlSource(Reader reader, Record record, SaxHtmlHandler handler)
    {
        this();
        this.init(reader, record, handler);
    }
    /**
     * Initialize class fields.
     */
    public void init(Reader reader, Record record, SaxHtmlHandler handler)
    {
        m_handler = null;
        m_handler = handler;
        super.init(reader, record);
    }
    /**
     * Parse the next line and return false at EOF.
     */
    public boolean parseNextLine()
    {
        if (firstTime)
        {
            firstTime = false;
            
         // Obtain an instance of an XMLReader implementation from a system property
            try {
                XMLReader parser = org.xml.sax.helpers.XMLReaderFactory.createXMLReader();
            
                // Set the ContentHandler...
                parser.setContentHandler( m_handler );
        
                   // Parse the file...
                parser.parse( new InputSource( m_reader ));
        
            } catch (IOException e) {
                e.printStackTrace();
            } catch (SAXException e) {
                e.printStackTrace();
            }
            
            m_record.close();
        }
        try {
            return m_record.hasNext();
        } catch (DBException e) {
            e.printStackTrace();
        }
        return false;
    }
    /**
     * MoveDataToRecord Method.
     */
    public Record moveDataToRecord()
    {
        try {
            return m_record.next();
        } catch (DBException e) {
            e.printStackTrace();
        }
        return super.moveDataToRecord();
    }

}
