/*
 * HotelRateRequestOut.java
 *
 * Created on September 26, 2003, 12:41 AM
 */

package org.jbundle.thin.base.message;

import java.util.HashMap;
import java.util.Map;

import org.jbundle.model.PropertyOwner;
import org.jbundle.model.db.Convert;
import org.jbundle.model.db.Field;
import org.jbundle.model.db.Rec;
import org.jbundle.thin.base.db.Constant;
import org.jbundle.thin.base.db.Converter;

/**
 * This is the base message for sending and receiving requests.
 * Data in this object are stored in the native java object type.
 * Data can either be extracted as the Raw object, an External object, or
 * a string:
 * Raw Object: Native java object.
 * External Object: Externally recognizable data (such as Hotel Name rather than HotelID).
 * String: External Object converted to ASCII (The conversion is specified in the DataDesc).
 * Typically, you override the rawToExternal and externlToRaw to do your conversion (none by default).
 * Also: XML: Typically External to String conversion (with tags) except for items such as dates.
 * @author  don
 */
public class MessageFieldDesc extends MessageDataDesc
{
    private static final long serialVersionUID = 1L;

    /**
     * If this param changes, the message must be resent. (By default, params are unique).
     */
    public final static int UNIQUE = 0;
    /**
     * This param is for information only.
     */
    public final static int NOT_UNIQUE = 1 << 30;

    public static boolean REQUIRED = true;
    public static boolean OPTIONAL = false;

    /**
     * Compound param (you must also supply the max number of params).
     * ie., UNIQUE + COMPOUND_PARAM + 4 (params are QWERTY1 QWERTY2 QWERTY3 QWERTY4).
     * If you don't supply a number, each param is scanned until you hit an empty param.
     */
    public final static int COMPOUND_PARAM = 1 << 29;
    /**
     * To mask the compound param count.
     */
    public final static int COMPOUND_MASK = (1 << 8) - 1;
    /**
     * Do not init on initForMessage.
     */
    public static final int DONT_INIT = 1 << 28;
    /**
     * This param is included on all messages of this type.
     */
    public static final int STANDARD_PARAM = 1 << 27;
    /**
     * This param needs to be moved to the reply message.
     */
    public static final int ECHO_PARAM = 1 << 26;
    /**
     * Class of the raw data object
     */
    protected Class<?> m_classRawObject = null;
    /**
     * Is this required?
     */
    protected boolean m_bRequired = true;
    /**
     * Does this param change require that the message be resent?
     */
    protected int m_iKeyInformation = UNIQUE;
    /**
     * If the param is null, supply this data instead.
     */
    protected Object m_objRawDefault = null;
    /**
     * Constructor.
     */
    public MessageFieldDesc()
    {
        super();
    }
    /**
     * Constructor.
     */
    public MessageFieldDesc(MessageDataParent messageDataParent, String strKey, Class<?> classRawObject, boolean bRequired, Object objRawDefault)
    {
        this.init(messageDataParent, strKey, classRawObject, bRequired, UNIQUE, objRawDefault);
    }
    /**
     * Constructor.
     */
    public MessageFieldDesc(MessageDataParent messageDataParent, String strKey, Class<?> classRawObject, boolean bRequired, int iKeyInformation, Object objRawDefault)
    {
        this.init(messageDataParent, strKey, classRawObject, bRequired, iKeyInformation, objRawDefault);
    }
    /**
     * Constructor.
     */
    public void init(MessageDataParent messageDataParent, String strKey, Class<?> classRawObject, boolean bRequired, int iKeyInformation, Object objRawDefault)
    {
        if (messageDataParent instanceof BaseMessage)
        {   // Should not add MessageFieldDesc to message, if you do, create a MessageRecordDesc
            MessageDataDesc messageRecordParent = ((BaseMessage)messageDataParent).getMessageDataDesc(null);
            if (messageRecordParent == null)
                messageRecordParent = new MessageRecordDesc(messageDataParent, null);
            messageDataParent = messageRecordParent;
        }
        super.init(messageDataParent, strKey);
        m_classRawObject = classRawObject;
        m_bRequired = bRequired;
        m_iKeyInformation = iKeyInformation;
        m_objRawDefault = objRawDefault;
    }
    /**
    *
    */
    public Class<?> getNativeClassType()
    {
        return this.getMessage().getNativeClassType(this.getRawClassType());
    }
    /**
    *
    */
    public Class<?> getRawClassType()
    {
        return m_classRawObject;
    }
    /**
     * 
     * @return
     */
    public Object getDefault()
    {
        return m_objRawDefault;
    }
    /**
    *
    */
   public boolean isRequired()
   {
       return m_bRequired;
   }
   /**
   *
   */
  public void setRequired(boolean bRequired)
  {
      m_bRequired = bRequired;
  }
    /**
     * Does this param change require that the message be resent?
     */
    public int getKeyInformation()
    {
        return m_iKeyInformation;
    }
    /**
     *
     */
    public String addMessageKey(String strKey, String strParam, Map<String, Object> map, int iCount)
    {
        if ((this.getKeyInformation() & NOT_UNIQUE) == 0)
        {
            char chSeparator = '/'; // Character.forDigit(iCount, Character.MAX_RADIX);
            if ((this.getKeyInformation() & COMPOUND_PARAM) == 0)
            {
                Object objValue = map.get(strParam);
                if (objValue != null)
                    strKey = strKey + chSeparator + objValue.toString();
            }
            else
            {
                int iMaxParam = this.getKeyInformation() & COMPOUND_MASK;
                if (iMaxParam == 0)
                    iMaxParam = COMPOUND_MASK;
                for (int i = 1; i <= iMaxParam; i++)
                {
                    String strParamCompound = strParam + Integer.toString(i);
                    Object objValue = map.get(strParamCompound);
                    if (objValue != null)
                        strKey = strKey + chSeparator + objValue.toString();
                    else
                        if (iMaxParam == COMPOUND_MASK)
                            break;  // If there are a unlimited number of params, stop on first null.
                }
            }
        }
        return strKey;  // Not a param that changes the message
    }
    /**
     * Get the message description for this (xpath) key.
     * @return The message data desc.
     */
    public MessageFieldDesc getMessageFieldDesc(String strKey)
    {
        if (strKey.equals(this.getKey()))
            return this;
        return null;
    }
    /**
     * Put the value for this param in the map.
     * If it is not the correct object type, convert it first.
     */
    public void put(Object objValue)
    {
        Class<?> classData = this.getNativeClassType();
        try {
            objValue = Converter.convertObjectToDatatype(objValue, classData, null);  // I do this just to be careful.
        } catch (Exception ex) {
            objValue = null;
        }
        if (this.getMessage() != null)
            this.getMessage().putNative(this.getFullKey(null), objValue);
    }
    /**
     * Convert this external data format to the raw object and put it in the map.
     * Typically this method is overridden to handle specific params.
     */
    public void putString(String strValue)
    {
        Class<?> classData = this.getNativeClassType();
        Object objValue = null;
        try {
            objValue = Converter.convertObjectToDatatype(strValue, classData, null);
        } catch (Exception ex) {
            objValue = null;
        }
        if (this.getMessage() != null)
            this.getMessage().putNative(this.getFullKey(null), objValue);
    }
    /**
     * Do any of the conversion that has to be done for all message types.
     * @param trxMessage The raw internal trx message.
     * @return The new Internal message with additional/refined data.
     */
    public Object get()
    {
        Object data = null;
        if (this.getMessage() != null)
            data = this.getMessage().getNative(this.getFullKey(null));
        try {
            data = Converter.convertObjectToDatatype(data, this.getRawClassType(), this.getDefault());
        } catch (Exception ex) {
            data = null;
        }
        return data;
    }
    /**
     * Do any of the conversion that has to be done for all message types.
     * @param trxMessage The raw internal trx message.
     * @return The new Internal message with additional/refined data.
     */
    public String getString()
    {
        Object objValue = null;
        if (this.getMessage() != null)
            objValue = this.getMessage().getNative(this.getFullKey(null));
        Class<?> classData = this.getRawClassType();
        try {
            return Converter.formatObjectToString(objValue, classData, this.getDefault());
        } catch (Exception ex) {
            return null;
        }
    }
    /**
     * Move the correct fields from this record to the map.
     * If this method is used, is must be overidden to move the correct fields.
     * @param record The record to get the data from.
     */
    public int putRawRecordData(Rec record)
    {
        return this.putRawFieldData(record.getField(this.getKey()));
    }
    /**
     * This utility sets this param to the field's raw data.
     * @return TODO
     */
    public int putRawFieldData(Convert field)
    {
        if (field != null)
        {
            if (this.getKey() == null)
                this.setKey(field.getFieldName());
            this.put(field.getData());
            return Constant.NORMAL_RETURN;
        }
        return Constant.NORMAL_RETURN;	// For now, ignore field not found
    }
    /**
     * Move the correct fields from this record to the map.
     * If this method is used, is must be overidden to move the correct fields.
     */
    public int getRawRecordData(Rec record)
    {
        Field fieldInfo = record.getField(this.getKey());
        if (fieldInfo != null)
            return this.getRawFieldData(fieldInfo);
        else
            return Constant.NORMAL_RETURN;
    }
    /**
     * This utility sets this field to the param's raw data.
     */
    public int getRawFieldData(Convert field)
    {
        if (this.getKey() == null)
            this.setKey(field.getFieldName());
        Object objValue = this.get();
        if ((objValue == null) || (objValue.getClass() == field.getField().getDataClass()))
            return field.setData(objValue); // Should call here
        else
            return field.setString(this.getString());
    }
    /**
     * Move the data from this properyowner to this message.
     */
    public void putRawProperties(PropertyOwner propertyOwner)
    {
        if (propertyOwner != null)
            this.put(propertyOwner.getProperty(this.getKey()));
    }
    /**
     * Initialize the fields in this record to prepare for this message.
     * Also, do any other preparation needed before sending this message.
     * @param record The record to initialize
     * @return An error code if there were any problems.
     */
    public int initForMessage(Rec record)
    {
        int iErrorCode = Constant.NORMAL_RETURN;
        if ((this.getKeyInformation() & DONT_INIT) != 0)
            return iErrorCode;  // Don't clear this field
        Field field = record.getField(this.getKey());
        if (field != null)
            return field.initField(true);
        return iErrorCode;
    }
    /**
     * Check to make sure all the data is present to attempt a cost lookup.
     * Note: You are NOT returning the status, you are returning the status of the params,
     * The calling program will change the status if required.
     * @return DATA_REQUIRED if all the data is not present, DATA_VALID if the data is OKAY.
     */
    public int checkRequestParams(Rec record)
    {
        int iMessageStatus = DATA_VALID;
//        FieldInfo field = record.getFieldInfo(this.getKey());
        // Override this to check field
        return iMessageStatus;
    }
    /**
     * Get a unique key that describes the data in this record.
     * You can use this key to see if any of the data has changed since the message was last sent.
     */
    public StringBuffer getMessageKey(StringBuffer sb)
    {
        if ((this.getKeyInformation() & NOT_UNIQUE) == 0)
        {
            if (sb == null)
                sb = new StringBuffer();
            boolean bCompoundDataAdded = false;
            if ((this.getKeyInformation() & COMPOUND_PARAM) != 0)
            {
                String strSaveKey = this.getKey();
                String strBaseParam = strSaveKey;
                if (Character.isDigit(strSaveKey.charAt(strSaveKey.length() - 1)))
                    strBaseParam = strSaveKey.substring(0, strSaveKey.length() - 1);
                int iMaxParam = this.getKeyInformation() & COMPOUND_MASK;
                if (iMaxParam == 0)
                    iMaxParam = COMPOUND_MASK;
                for (int i = 1; i <= iMaxParam; i++)
                {
                    this.setKey(strBaseParam + Integer.toString(i));
                    String data = this.getString();
                    if (data != null)
                    {
                        if (sb.length() > 0)
                            sb.append('/');
                        sb.append(data);
                        if (data.length() > 0)
                            bCompoundDataAdded = true;
                    }
                    else
                        if (iMaxParam == COMPOUND_MASK)
                            break;  // If there are a unlimited number of params, stop on first null.
                }
                this.setKey(strSaveKey);
            }
            if (!bCompoundDataAdded)
            {
                if (sb.length() > 0)
                    sb.append('/');
                String data = this.getString();
                if (data != null)
                    sb.append(data);
            }
        }
        return sb;
    }
    /**
     * Move the pertinenent information from the request to this reply message.
     * Override this to be more specific.
     * Add some code like: this.put(messageRequest.get());
     */
    public void moveRequestInfoToReply(BaseMessage messageRequest)
    {
        super.moveRequestInfoToReply(messageRequest);
        if ((this.getKeyInformation() & ECHO_PARAM) != 0)
        {   // Move this to reply
            this.put(messageRequest.get(this.getFullKey(null)));
        }
    }
    /**
     * Get the property names and classes that are part of the standard message payload.
     * @param mapPropertyNames
     * @return
     */
    public Map<String, Class<?>> getPayloadPropertyNames(Map<String,Class<?>> mapPropertyNames)
    {
        mapPropertyNames = super.getPayloadPropertyNames(mapPropertyNames);
        if ((this.getKeyInformation() & STANDARD_PARAM) != 0)
        {   // Add this name and class to the map of property names
            if (mapPropertyNames == null)
                mapPropertyNames = new HashMap<String, Class<?>>();
            mapPropertyNames.put(this.getFullKey(null), this.getRawClassType());
        }
        return mapPropertyNames;
    }
}
