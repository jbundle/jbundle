/*
 * Copyright 1999-2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jbundle.base.screen.control.servlet.xml.cocoon;
/**
 * @cocoon.sitemap.component.documentation
 * The <code>FileGenerator</code> is a class that reads XML from a source
 * and generates SAX Events.
 * The FileGenerator implements the <code>CacheableProcessingComponent</code> interface.
 * 
 * @cocoon.sitemap.component.name   file
 * @cocoon.sitemap.component.label  content
 * @cocoon.sitemap.component.logger sitemap.generator.file
 * @cocoon.sitemap.component.documentation.caching
 *               Uses the last modification date of the xml document for validation
 * 
 * @cocoon.sitemap.component.pooling.max  32
 *
 * @author <a href="mailto:pier@apache.org">Pierpaolo Fumagalli</a>
 *         (Apache Software Foundation)
 * @author <a href="mailto:cziegeler@apache.org">Carsten Ziegeler</a>
 * @version CVS $Id: MyInputSource.java,v 1.1 2009/01/16 21:30:08 don Exp $
 */
 public class MyInputSource extends Object { /*InputSource
    implements Source
{
    protected String m_strRequestURI = null;
    protected String m_strMimeType = null;
    
    public MyInputSource()
    {
        super();
    }

    public MyInputSource(Reader inStream, String strRequestURI, String strMimeType)
    {
        super(inStream);
        m_strRequestURI = strRequestURI;
        m_strMimeType = strMimeType;
    }

    /**
     * Does this source exist ?
     * 
     * @return true if the source exists
     *
    public boolean exists()
    {
        return true;
    }
    
    /**
     * Return an <code>InputStream</code> to read from the source.
     * This is the data at the point of invocation of this method,
     * so if this is Modifiable, you might get different content
     * from two different invocations.
     *
     * The returned stream must be closed by the calling code.
     * 
     * @return the <code>InputStream</code> to read data from (never <code>null</code>).
     * @throws IOException if some I/O problem occurs.
     * @throws SourceNotFoundException if the source doesn't exist.
     *
    public InputStream getInputStream()
        throws IOException, SourceNotFoundException
    {
        return this.getByteStream();
    }

    /**
     * Get the absolute URI for this source.
     * 
     * @return the source URI.
     *
    public String getURI()
    {
        return m_strRequestURI;
    }

    /**
     * Return the URI scheme identifier, i.e. the part preceding the fist ':' in the URI
     * (see <a href="http://www.ietf.org/rfc/rfc2396.txt">RFC 2396</a>).
     * <p>
     * This scheme can be used to get the {@link SourceFactory} responsible for this object.
     * 
     * @return the URI scheme.
     *
    public String getScheme()
    {
        if (m_strRequestURI != null)
            if (m_strRequestURI.indexOf(':') != -1)
                return m_strRequestURI.substring(0, m_strRequestURI.indexOf(':'));
        return null;
    }
    
    /**
     * Get the Validity object. This can either wrap the last modification date or
     * some expiry information or anything else describing this object's validity.
     * <p>
     * If it is currently not possible to calculate such an information,
     * <code>null</code> is returned.
     * 
     * @return the validity, or <code>null</code>.
     *
    public SourceValidity getValidity()
    {
        return null;
    }

    /**
     * Refresh the content of this object after the underlying data content has changed.
     * <p>
     * Some implementations may cache some values to speedup sucessive calls. Refreshing
     * ensures you get the latest information.
     *
    public void refresh()
    {
    }

    /**
     * Get the mime-type of the content described by this object.
     * If the source is not able to determine the mime-type by itself
     * this can be <code>null</code>.
     * 
     * @return the source's mime-type or <code>null</code>.
     *
    public String getMimeType()
    {
        return m_strMimeType;
    }

    /**
     * Get the content length of this source's content or -1 if the length is
     * unknown.
     * 
     * @return the source's content length or -1.
     *
    public long getContentLength()
    {
        return -1;
    }

    /**
     * Get the last modification date of this source. The date is
     * measured in milliseconds since the epoch (00:00:00 GMT, January 1, 1970),
     * and is <code>0</code> if it's unknown.
     * 
     * @return the last modification date or <code>0</code>.
     *
    public long getLastModified()
    {
        return 0;
    }
    */
}