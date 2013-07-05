/**
 * @(#)MergeHtml.
 * Copyright © 2013 jbundle.org. All rights reserved.
 * GPL3 Open Source Software License.
 */
package org.jbundle.app.program.script.data.importfix.base;

import java.util.*;

import org.jbundle.base.db.*;
import org.jbundle.thin.base.util.*;
import org.jbundle.thin.base.db.*;
import org.jbundle.base.db.event.*;
import org.jbundle.base.db.filter.*;
import org.jbundle.base.field.*;
import org.jbundle.base.field.convert.*;
import org.jbundle.base.field.event.*;
import org.jbundle.base.model.*;
import org.jbundle.base.util.*;
import org.jbundle.model.*;
import org.jbundle.model.db.*;
import org.jbundle.model.screen.*;
import java.io.*;
import java.net.*;
import org.jbundle.app.program.script.data.importfix.db.*;

/**
 *  MergeHtml - .
 */
public class MergeHtml extends MergeData
{
    /**
     * Default constructor.
     */
    public MergeHtml()
    {
        super();
    }
    /**
     * Constructor.
     */
    public MergeHtml(RecordOwnerParent taskParent, Record recordMain, Map<String,Object> properties)
    {
        this();
        this.init(taskParent, recordMain, properties);
    }
    /**
     * Initialize class fields.
     */
    public void init(RecordOwnerParent taskParent, Record recordMain, Map<String, Object> properties)
    {
        super.init(taskParent, recordMain, properties);
    }
    /**
     * GetSource Method.
     */
    public Iterator<Record> getSource()
    {
        String strURL = this.getProperty("source");
        if (strURL == null)
            return null;
        
        Reader reader = null;
        try {
            URL url = new URL(strURL);
            InputStream inputStream = url.openStream();
            InputStreamReader inStream = new InputStreamReader(inputStream);
            reader = inStream;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        Record record = this.getMergeRecord();
        SaxHtmlHandler handler = this.getSaxHandler(record);
        return new HtmlSource(reader, record, handler);
    }
    /**
     * OpenMergeRecord Method.
     */
    public Record openMergeRecord()
    {
        Record record = super.openMergeRecord();
        if (record == null)
            record = new EmptyMemoryRecord(this); // Usually
        return record;
    }
    /**
     * GetSaxHandler Method.
     */
    public SaxHtmlHandler getSaxHandler(Record record)
    {
        return new SaxHtmlHandler(record);
    }

}
