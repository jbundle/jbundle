package org.jbundle.base.message.record;

/**
 * RecordMessage.java
 *
 * Created on June 26, 2000, 3:19 AM
 */
 
import java.io.Serializable;
import java.util.HashSet;
import java.util.Map;

import org.jbundle.base.db.Record;
import org.jbundle.base.util.DBConstants;
import org.jbundle.thin.base.message.BaseMessageFilter;
import org.jbundle.thin.base.message.BaseMessageHeader;
import org.jbundle.thin.base.remote.RemoteSession;


/** 
 * A Record message is a message explaining the changes to this record.
 * This version is tailored to GridTables where a query is made, then valid records are displayed.
 * NOTE: This object is sent over the wire, so make sure you only fill it with simple,
 * serializable objects!
 * @author  Administrator
 * @version 1.0.0
 */
public class GridRecordMessageFilter extends BaseRecordMessageFilter
    implements Serializable
{
	private static final long serialVersionUID = 1L;

	/**
     * The list of bookmarks to watch.
     */
    protected BookmarkList m_htBookmarks = null;
    /**
     * Property to add a bookmark.
     */
    public static final String ADD_BOOKMARK = "addBookmark";
    /**
     * Add this bookmark to clear all the bookmarks.
     */
    public static final String CLEAR_BOOKMARKS = "00";
    /**
     *
     */
    public static final String SECOND_KEY_HINT = "2ndHint";
    /**
     * The secondary key to watch for.
     */
    protected String m_strSecondaryKey = null;
    /**
     * The reference value to watch for.
     */
    protected Object m_objKeyData = null;
    /**
     * Receive all the add notifications?
     */
    protected boolean m_bReceiveAllAdds = false;

    /**
     * Creates new RecordMessage.
     */
    public GridRecordMessageFilter()
    {
        super();
    }
    /**
     * Constructor.
     * @param record The record to watch.
     * @param source The source of this filter, to eliminate echos.
     * @param bReceiveAllAdds If true, receive all add notifications, otherwise just receive the adds on secondary reads.
     */
    public GridRecordMessageFilter(Record record, Object source, boolean bReceiveAllAdds)
    {
        this();
        this.init(record, source, bReceiveAllAdds);
    }
    /**
     * Constructor.
     * @param record The record to watch.
     * @param source The source of this filter, to eliminate echos.
     * @param bReceiveAllAdds If true, receive all add notifications, otherwise just receive the adds on secondary reads.
     */
    public void init(Record record, Object source, boolean bReceiveAllAdds)
    {
        m_htBookmarks = new BookmarkList();
        m_bReceiveAllAdds = bReceiveAllAdds;
        super.init(record, null, source);
        if (record != null)
            record.addListener(new GridSyncRecordMessageFilterHandler(this, true));
    }
    /**
     * Free this object.
     */
    public void free()
    {
        super.free();
        if (m_htBookmarks != null)
            m_htBookmarks.clear();
        m_htBookmarks = null;
    }
    /**
     * Does this message header match this filter?
     * @param messageHeader The message header to check.
     * @return true if match, false if no match.
     */
    public boolean isFilterMatch(BaseMessageHeader messageHeader)
    {
        boolean bMatch = super.isFilterMatch(messageHeader);
        if (bMatch)
        {
            if (!(messageHeader instanceof RecordMessageHeader))
                return false;   // Never
            RecordMessageHeader recMessageHeader = (RecordMessageHeader)messageHeader;
            int iRecordMessageType = recMessageHeader.getRecordMessageType();
            if ((iRecordMessageType == DBConstants.AFTER_UPDATE_TYPE)
                    || ((iRecordMessageType == DBConstants.AFTER_DELETE_TYPE)))
            {
                if (!m_htBookmarks.contains(recMessageHeader.getBookmark(DBConstants.BOOKMARK_HANDLE)))
                    bMatch = false;   // No match
                else
                    bMatch = true;
            }
            else if (iRecordMessageType == DBConstants.AFTER_ADD_TYPE)
            {
                if ((m_strSecondaryKey != null) && (m_objKeyData != null))
                {       // If there is a hint, it won't take much processing power to check for a match
                    bMatch = recMessageHeader.isMatchHint(m_strSecondaryKey, m_objKeyData);
                }
                else
                {       // There is no hint, so you could potentially get a ton of add messages
                    bMatch = true;  // What the heck, as long as I don't have to send it up, process this message.
                    for (int i = 0; (this.getMessageListener(i) != null); i++)
                    {
                        if (this.getMessageListener(i) instanceof org.jbundle.base.remote.message.ReceiveQueueSession)
                        {
                            bMatch = m_bReceiveAllAdds; // Do I receive all the add messages?
                            break;
                        }
                    }
                }
            }
            else if (iRecordMessageType == DBConstants.CACHE_UPDATE_TYPE)
            {
                if (!m_htBookmarks.contains(recMessageHeader.getBookmark(DBConstants.BOOKMARK_HANDLE)))
                    bMatch = false;   // No match
                else
                    bMatch = true;
            }
            else
                bMatch = false;     // No match
        }
        return bMatch;
    }
    /**
     * Update this filter with this new information.
     * Override this to do something locally.
     * Remember to call super after updating this filter, as this method updates the remote copy of this filter.
     * @param properties New filter information (ie, bookmark=345).
     * @return The new filter change map (must contain enough information for the remote filter to sync).
     */
    public Map<String,Object> handleUpdateFilterMap(Map<String,Object> propFilter)
    {
        if (propFilter != null)
        {
            Object bookmark = propFilter.get(ADD_BOOKMARK);
            if (bookmark != null)
            {
                if (CLEAR_BOOKMARKS.equals(bookmark))
                {
                	if (m_htBookmarks != null)
                		m_htBookmarks.clear();
                    m_strSecondaryKey = null;             // The secondary key to watch for.
                    m_objKeyData = null;                  // The reference value to watch for.
                    String strHint = (String)propFilter.get(SECOND_KEY_HINT);
                    if (strHint != null)
                    {
                        Object objKeyData = propFilter.get(strHint);    // The value of the 2nd key reference data.
                        m_strSecondaryKey = strHint;                    // The secondary key to watch for.
                        m_objKeyData = objKeyData;                      // The reference value to watch for.
                    }
                }
                else
                {
                	if (m_htBookmarks != null)
                		if (!m_htBookmarks.contains(bookmark))
                			m_htBookmarks.add(bookmark);
                }
            }
        }
        return super.handleUpdateFilterMap(propFilter);   // Update any remote copy of this.
    }
    /**
     * Update this filter with this new information.
     * Here, I'm looking for a new bookmark property (objectID).
     * Remember to call super after updating this filter, as this method updates the remote copy of this filter.
     * @param properties New filter information (ie, bookmark=345).
     */
    public Object[][] createNameValueTree(Object[][] mxProperties, Map<String, Object> properties)
    {
        return super.createNameValueTree(mxProperties, properties);
    }
    /**
     * Link this filter to this remote session.
     * This is ONLY used in the server (remote) version of a filter.
     * Override this to finish setting up the filter (such as behavior to adjust this filter).
     * In this case, the source cannot be passed to the remote session because it is the
     * record, so the record must be re-linked to this (remote) session.
     */
    public BaseMessageFilter linkRemoteSession(RemoteSession remoteSession)
    {
        if (remoteSession instanceof org.jbundle.base.remote.db.Session)
            if (m_source == null)
        {
            String strTableName = (String)this.getProperties().get(TABLE_NAME);
            Record record = ((org.jbundle.base.remote.db.Session)remoteSession).getRecord(strTableName);
            if (record != null)
            {
                record.addListener(new GridSyncRecordMessageFilterHandler(this, true));
//                m_source = record;
                m_source = remoteSession;   // This will give the remotesession a chance to change the message before being sent
            }
        }
        return this;    // No change to the filter.
    }
    /**
     * This is the same as the HashSet, with the addition on only allowing x objects
     * to be stored. The oldest in the list are discarded first.
     */
    class BookmarkList extends HashSet<Object>
    {
    	private static final long serialVersionUID = 1L;

    	public static final int MAX_OBJECTS = 100;
        protected Object[] m_rgObjects = new Object[MAX_OBJECTS];
        protected int m_iCurrentObject = -1;

        public BookmarkList()
        {
            super(MAX_OBJECTS);
            this.clear();
        }
        /**
         * Add this object to the list.
         * @param obj Bookmark to add.
         * @return true if successful.
         */
        public boolean add(Object obj)
        {
            if (obj == null)
                return false;
            m_iCurrentObject++;
            if (m_iCurrentObject >= MAX_OBJECTS)
                m_iCurrentObject = 0;
            Object objOld = m_rgObjects[m_iCurrentObject];
            m_rgObjects[m_iCurrentObject] = null;
            
            int iOldestEmpty = -1;
            boolean bContained = !super.add(obj);
            if (bContained)
            { // Already in list... remove the old one
                for (int i = 0; i < MAX_OBJECTS; i++)
                {
                    if (obj.equals(m_rgObjects[i]))
                        m_rgObjects[i] = null;
                    else if (m_rgObjects[i] == null)
                    {
                        if (i < m_iCurrentObject)
                        {
                            if (iOldestEmpty == -1)
                                iOldestEmpty = i;   // First one from zero is the oldest
                        }
                        else if (i > m_iCurrentObject)  // First one from current is even older
                        {
                            if (iOldestEmpty < m_iCurrentObject)
                                iOldestEmpty = i;   // Smaller is older
                        }
                    }
                }
            }
            m_rgObjects[m_iCurrentObject] = obj;
            if ((objOld != null) && (objOld != obj))
            {
                if (iOldestEmpty != -1)
                    m_rgObjects[iOldestEmpty] = objOld;
                else
                    super.remove(objOld);   // Oldest in list
            }
            
            return bContained;
        }
        /**
         * Remove this object.
         * @param obj The object to remove.
         * @return True if successful.
         */
        public boolean remove(Object obj)
        {
            if (obj == null)
                return true;
            boolean bContained = super.remove(obj);
            if (bContained)
            { // Already in list... remove the old one
                for (int i = 0; i < MAX_OBJECTS; i++)
                {
                    if (obj.equals(m_rgObjects[i]))
                    {
                        m_rgObjects[i] = null;
                        break;
                    }
                }
            }
            return bContained;
        }
        /**
         * Clear all the entries.
         */
        public void clear()
        {
            super.clear();
            for (int i = 0; i < MAX_OBJECTS; i++)
            {
                m_rgObjects[i] = null;
            }
        }
        /**
         * Display this object as a string.
         * @return the string.
         */
        public String toString()
        {
            String str = super.toString() + ")";
            for (int i = 0; i < MAX_OBJECTS; i++)
            {
                if (i > 0)
                    str += "& ";
                str += m_rgObjects[i];
            }
            str += ")";
            return str;
        }
    }
    /**
     * Receive all the add notifications?
     */
    public void setReceiveAllAdds(boolean bReceiveAllAdds)
    {
        m_bReceiveAllAdds = bReceiveAllAdds;
    }
}
