package org.jbundle.thin.base.message;



/**
 * External version of a message.
 */
public interface ExternalMessage
{
    /**
     * Get the raw data for this external format.
     * @return The raw data.
     */
    public Object getRawData();
    /**
     * Set the raw data for this external format.
     * @param Set the raw data.
     */
    public void setRawData(Object data);
    /**
     * Convert this external format to the standard internal format and move the data to the message.
     * @param recordOwner TODO
     * @return TODO
     */
    public int convertExternalToInternal(Object recordOwner);
    /**
     * Convert the current internal format to this external format and set the data data.
     * @param recordOwner TODO
     * @return TODO
     */
    public int convertInternalToExternal(Object recordOwner);
}
