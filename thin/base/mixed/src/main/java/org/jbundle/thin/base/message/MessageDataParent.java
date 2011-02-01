/*
 * HotelRateRequestOut.java
 *
 * Created on September 26, 2003, 12:41 AM
 */

package org.jbundle.thin.base.message;

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
public interface MessageDataParent
{
    /**
     * Get the parent message for this data.
     * @return The message.
     */
    public BaseMessage getMessage();
    /**
     * Add a child message data desc.
     */
    public void addMessageDataDesc(MessageDataDesc messageDataDesc);
}
