/**
 * @(#)MessageTransportTypeField.
 * Copyright © 2012 jbundle.org. All rights reserved.
 * GPL3 Open Source Software License.
 */
package org.jbundle.main.msg.db;

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

/**
 *  MessageTransportTypeField - Type of message transport.
 */
public class MessageTransportTypeField extends StringPopupField
{
    public static final String DIRECT = "Direct";
    public static final String LOCAL = "Local";
    public static final String MANUAL_RESPONSE = "ManualResp";
    public static final String AUTO_RESPONSE = "AutoResp";
    public static final String MANUAL_COMMUNICATION = "ManualComm";
    public static final String THREAD = "Thread";
    public static final String[][] TRANSPORT_TYPES = {
        {DIRECT, "Direct"},
        {LOCAL, "Local"},
        {AUTO_RESPONSE, "Remote automated response"},
        {MANUAL_RESPONSE, "Remote manual response"},
        {MANUAL_COMMUNICATION, "Manual communication"},
        {THREAD, "Thread"},
    };
    /**
     * Default constructor.
     */
    public MessageTransportTypeField()
    {
        super();
    }
    /**
     * Constructor.
     * @param record The parent record.
     * @param strName The field name.
     * @param iDataLength The maximum string length (pass -1 for default).
     * @param strDesc The string description (usually pass null, to use the resource file desc).
     * @param strDefault The default value (if object, this value is the default value, if string, the string is the default).
     */
    public MessageTransportTypeField(Record record, String strName, int iDataLength, String strDesc, Object strDefault)
    {
        this();
        this.init(record, strName, iDataLength, strDesc, strDefault);
    }
    /**
     * Constructor.
     * @param record The parent record.
     * @param strName The field name.
     * @param iDataLength The maximum string length (pass -1 for default).
     * @param strDesc The string description (usually pass null, to use the resource file desc).
     * @param strDefault The default value (if object, this value is the default value, if string, the string is the default).
     */
    public void init(Record record, String strName, int iDataLength, String strDesc, Object strDefault)
    {
        super.init(record, strName, iDataLength, strDesc, strDefault);
        if (iDataLength == DBConstants.DEFAULT_FIELD_LENGTH)
            m_iMaxLength = 10;
    }
    /**
     * Get the default value.
     * @return The default value.
     */
    public Object getDefault()
    {
        Object objDefault = super.getDefault();
        if (objDefault == null)
            objDefault = DIRECT;
        return objDefault;
    }
    /**
     * Get the conversion Map.
     */
    public String[][] getPopupMap()
    {
        return TRANSPORT_TYPES;
    }

}
