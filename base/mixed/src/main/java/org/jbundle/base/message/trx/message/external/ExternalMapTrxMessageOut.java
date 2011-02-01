/*
 * BaseTrxMessage.java
 *
 * Created on October 12, 2001, 3:32 AM
 */

package org.jbundle.base.message.trx.message.external;

import org.jbundle.thin.base.message.BaseMessage;
import org.w3c.dom.Node;


/**
 * This is the base class for a transaction which is sent externally.
 * The two main sub-classes of this class are InternalTrxMessage and ExternalTrxMessage.
 * An InternalTrxMessage is the data I create internally to send to the destination. It
 * usually contains all the relative information needed to send to the destination.
 * An ExternalTrxMessage is the message converted to a format that the receiver can
 * understand (such as ebXML).
 * @author  don
 * @version 
 */
public class ExternalMapTrxMessageOut extends ExternalTrxMessageOut
{

    /**
      * Creates new BaseTrxMessage
     */
    public ExternalMapTrxMessageOut() 
    {
        super();
    }
    /**
      * Initialize new BaseTrxMessage.
     * @param objRawMessage The (optional) raw data of the message.
     */
    public ExternalMapTrxMessageOut(BaseMessage message, Object objRawMessage)
    {
        this();
        this.init(message, objRawMessage);
    }
    /**
      * Initialize new BaseTrxMessage.
     * @param objRawMessage The (optional) raw data of the message.
     */
    public void init(BaseMessage message, Object mapRawMessage)
    {
        super.init(message, mapRawMessage);
    }
    /**
     * Get the raw data object for this incoming message.
     * @return the data object.
     */
    public Object getRawData()
    {
        return this.getMessage().getData();
    }
    /**
     * Get the raw data object for this incoming message.
     * @return the data object (Must override).
     */
    public void setRawData(Object rawData)
    {
        this.getMessage().setData(rawData);
    }
    /**
     * Convert this tree to a DOM object.
     * Override this.
     * @param root The room jaxb item.
     * @return The dom tree.
     */
    public Node getDOM()
    {
        return this.getMessage().getDOM();
    }
    /**
     * Convert this tree to a DOM object.
     * Override this.
     * @param root The room jaxb item.
     * @return True if successful.
     * @return The dom tree.
     */
    public boolean setDOM(Node node)
    {
        return this.getMessage().setDOM(node);
    }
    /**
     * Convert this tree to a XML string.
     * Override this.
     * @param root The room jaxb item.
     * @return The dom tree.
     */
    public String getXML()
    {
        return this.getMessage().getXML(true);
    }
}
