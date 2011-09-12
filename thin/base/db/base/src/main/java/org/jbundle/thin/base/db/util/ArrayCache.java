/*
 * Copyright © 2011 jbundle.org. All rights reserved.
 */
package org.jbundle.thin.base.db.util;

import java.util.ArrayList;

import org.jbundle.model.Freeable;


/**
 * Array cache is a special array list that caches the last 64 objects added.
 * It automatically shifts the array to the area that needs to be cached.
 */
public class ArrayCache extends ArrayList<Object>
{
	private static final long serialVersionUID = 1L;

	private int m_iStartIndex = 0;
    public final static int DEFAULT_SIZE = 64;
    public int m_iMaxSize = DEFAULT_SIZE;

    /**
     * Constructor.
     */
    public ArrayCache()
    {
        super();
    }
    /**
     * Constructor (pass -1 to use default cache size).
     */
    public ArrayCache(int iArraySize)
    {
        this();
        this.init(iArraySize);
    }
    /**
     * Constructor (pass -1 to use default cache size).
     */
    public void init(int iArraySize)
    {
        if (iArraySize == -1)
            iArraySize = DEFAULT_SIZE;
        m_iMaxSize = iArraySize;
    }
    /**
     * Set this element to this object.
     * If this index is not in the current array, shift the array and add it.
     * @see java.util.ArrayList
     * @param index The index to set the value.
     * @param element The object to add.
     * @return The selelement previously at this location.
     */
    public Object set(int index, Object element)
    {
        if ((index < m_iStartIndex) || (index >= m_iStartIndex + m_iMaxSize))
        { // Out of bounds, re-adjust bounds
            int iNewStart = index - m_iMaxSize / 2;   // index should be right in the middle
            if (iNewStart < 0)
                iNewStart = 0;
            int iStart = 0;
            int iEnd = this.size() - 1;
            int iIncrement = +1;
            if (iNewStart < m_iStartIndex)
            {   // Go backwards to avoid overlaying values
                iStart = iEnd;
                iEnd = 0;
                iIncrement = -1;
            }
            for (int i = iStart; i * iIncrement <= iEnd; i = i + iIncrement)
            {
                Object obj = super.set(i, null);                // Get and clear value
                int iShiftedIndex = i + m_iStartIndex - iNewStart;
                if ((iShiftedIndex >= 0) && (iShiftedIndex < this.size()))
                    super.set(iShiftedIndex, obj);              // Move/set it!
                else
                    this.freeObject(m_iStartIndex + i, obj);    // Notify obj
            }
            m_iStartIndex = iNewStart;
        }
        if (index >= m_iStartIndex + this.size())
        { // Need to add empty elements inbetween
            for (int i = m_iStartIndex + this.size(); i <= index; i++)
                this.add(null);     // Add a placeholder
        }
        index = index - m_iStartIndex;
        return super.set(index, element);
    }
    /**
     * Get the object at this index (Add the offset).
     * If this location is not in the cache, a null is returned.
     * @see java.util.ArrayList
     * @param index The index to set the value.
     * @return The element at this location.
     */
    public Object get(int index)
    {
        if ((index - m_iStartIndex < 0) || (index - m_iStartIndex >= this.size()))
            return null;    // Not in current list
        return super.get(index - m_iStartIndex);
    }
    /**
     * Free the object (if it implements freeable).
     * @param @iIndex The index.
     * @param obj The object to free (if freeable).
     */
    public void freeObject(int iIndex, Object obj)
    {
        if (obj instanceof Freeable)
            ((Freeable)obj).free();
    }
    /**
     * Get the start index.
     * @return the starting index.
     */
    public int getStartIndex()
    {
        return m_iStartIndex;
    }
    /**
     * Get the start index.
     * @return the starting index.
     */
    public int getEndIndex()
    {
        return m_iStartIndex + this.size() - 1;
    }
}
