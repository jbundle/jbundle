package org.jbundle.base.message.trx.message.external;

import org.jbundle.thin.base.message.BaseMessage;

/**
 * This is the base class to process an external message.
 */
public class EMailTrxMessageOut extends ExternalTrxMessageOut
{
    /**
     * The smtp host.
     */
    public static final String HOST_PARAM = "host";
    /**
     * Message subject.
     */
    public static final String SUBJECT_PARAM = "subject";
    /**
     * The param for the smtp api.
     */
    public static final String SMTP_HOST = "mail.smtp.host";

    /**
     * Default constructor.
     */
    public EMailTrxMessageOut()
    {
        super();
    }
    /**
      * Initialize new ExternalTrxMessage.
     * @param objRawMessage The (optional) raw data of the message.
     */
    public EMailTrxMessageOut(BaseMessage message, Object objRawMessage)
    {
        this();
        this.init(message, objRawMessage);
    }
    /**
      * Initialize new ExternalTrxMessage.
     * @param objRawMessage The (optional) raw data of the message.
     */
    public void init(BaseMessage message, Object objRawMessage)
    {
        super.init(message, objRawMessage);
    }
    /**
     * Convert this tree to a XML string.
     * Override this.
     * @param root The room jaxb item.
     * @return The dom tree.
     */
    public boolean setXML(String strXML)
    {
       this.setRawData(strXML);
       return true;
    }
}
