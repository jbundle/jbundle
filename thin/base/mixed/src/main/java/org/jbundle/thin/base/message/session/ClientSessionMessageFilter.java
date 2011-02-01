/**
 * RecordMessage.java
 *
 * Created on June 26, 2000, 3:19 AM
 */
 
package org.jbundle.thin.base.message.session;

import java.io.Serializable;
import java.util.Map;

import org.jbundle.thin.base.message.BaseMessageFilter;
import org.jbundle.thin.base.message.BaseMessageHeader;
import org.jbundle.thin.base.message.MessageConstants;
import org.jbundle.thin.base.remote.RemoteSession;


/** 
 * A ClientSessionMessageFilter is a filter that passes the remote session to the server.
 * This is used to simplify the task of passing a message from the session up to a thin client. All you need to
 * do in the session is send a message with a SessionMessageHeader and the client will get it. Be careful to
 * specify private if you don't want to receive other messages on this queue (or don't if you want all messages).
 * Listen carefully.... this is a little complicated:
 * Three classes: ClientSessionMessageFilter, ServerSessionMessageFilter, and SessionMessageHeader
 * work together to solve the following problem:
 * A Thin client can listen on any queue, but will fail if a thick message or messageHeader is delivered to it.
 * To solve this problem, the server handles all the messages, converts them to a version a thin client can
 * read and sends that version up.
 * First, the thin client creates a ClientSessionMessageFilter and adds it to a queue that will not get thick
 * messages (usually SERVER_QUEUE_NAME).
 * Then the remote session creates a ServerSessionMessageFilter on the same queue. The ServerSessionMessageFilter
 * will only respond to messages that originate from the server session.
 * Then the remote session registers to listen for the thick message that the client wants.
 * When the remote session receives the thick message, it converts it to a Thin (usually Base) message and sends it
 * to the client by setting the header to a SessionMessageHeader and sending it to the client's queue.
 *
 * NOTE: This object is sent over the wire, so make sure you only fill it with simple,
 * serializable objects!
 * @author  Administrator
 * @version 1.0.0
 */
public class ClientSessionMessageFilter extends BaseMessageFilter
    implements Serializable
{
	private static final long serialVersionUID = 1L;

	/**
     * The remote session (To pass down to the server in the addMessageFilter call).
     */
    protected transient RemoteSession m_session = null;
    /**
     * The properties for the filter.
     */
    protected Map m_propertiesForFilter = null;
    /**
     * Does this filter ONLY receive messages from the remote session?
     */
    protected boolean m_bPrivate = false;

    /**
     * Creates new SessionMessageFilter.
     */
    public ClientSessionMessageFilter()
    {
        super();
    }
    /**
     * Constructor.
     * @param strQueueName The queue name.
     * @param strQueueType The queue type.
     * @param source The source (sender).
     * @param session The remote session.
     * @param propertiesForFilter The properties for the filter.
     */
    public ClientSessionMessageFilter(String strQueueName, String strQueueType, Object source, RemoteSession session, Map propertiesForFilter)
    {
        this();
        this.init(strQueueName, strQueueType, source, session, propertiesForFilter, false);
    }
    /**
     * Constructor.
     * @param strQueueName The queue name.
     * @param strQueueType The queue type.
     * @param source The source (sender).
     * @param session The remote session.
     * @param propertiesForFilter The properties for the filter.
     */
    public ClientSessionMessageFilter(String strQueueName, String strQueueType, Object source, RemoteSession session, Map propertiesForFilter, boolean bPrivate)
    {
        this();
        this.init(strQueueName, strQueueType, source, session, propertiesForFilter, bPrivate);
    }
    /**
     * Constructor.
     * @param strQueueName The queue name.
     * @param strQueueType The queue type.
     * @param source The source (sender).
     * @param session The remote session.
     * @param propertiesForFilter The properties for the filter.
     */
    public ClientSessionMessageFilter(Object source, RemoteSession session, Map propertiesForFilter)
    {
        this();
        this.init(MessageConstants.SESSION_QUEUE_NAME, MessageConstants.INTRANET_QUEUE, source, session, propertiesForFilter, true);
    }
    /**
     * Constructor.
     * @param strQueueName The queue name.
     * @param strQueueType The queue type.
     * @param source The source (sender).
     * @param session The remote session.
     * @param propertiesForFilter The properties for the filter.
     */
    public void init(String strQueueName, String strQueueType, Object source, RemoteSession session, Map propertiesForFilter, boolean bPrivate)
    {
        super.init(strQueueName, strQueueType, source, null);
        m_session = session;
        m_propertiesForFilter = propertiesForFilter;
        m_bPrivate = bPrivate;
        this.setThinTarget(true);   // Yes, this is always a thin target
    }
    /**
     * Free this object.
     */
    public void free()
    {
        super.free();
    }
    /**
     * Does this message header match this filter?
     * @param messageHeader The message header to check.
     * @return true if match, false if no match.
     */
    public boolean isFilterMatch(BaseMessageHeader messageHeader)
    {
        boolean bIsMatch = super.isFilterMatch(messageHeader);
        return bIsMatch;
    }
    /**
     * Try to figure out the remote session that this filter belongs to.
     * @return The remote session.
     */
    public RemoteSession getRemoteSession()
    {
        return m_session;
    }
    /**
     * Get the properties for this filter.
     * @return The filter properties and this SessionMessageFilter's properties merged.
     */
    public Map getProperties()
    {
        Map properties = super.getProperties();
        if (m_propertiesForFilter != null)
            properties.putAll(m_propertiesForFilter);
        return properties;
    }
    /**
     * Link this filter to this remote session.
     * This is ONLY used in the server (remote) version of a filter.
     * Override this to finish setting up the filter (such as behavior to adjust this filter).
     * Or if the server version of this filter is different, you have a chance to change the filter.
     * @param remoteSession The remote session to link/set.
     */
    public BaseMessageFilter linkRemoteSession(RemoteSession remoteSession)
    {
        return new ServerSessionMessageFilter(this.getQueueName(), this.getQueueType(), null, remoteSession, m_bPrivate, true); // This is hooked to a thin filter
    }
}
