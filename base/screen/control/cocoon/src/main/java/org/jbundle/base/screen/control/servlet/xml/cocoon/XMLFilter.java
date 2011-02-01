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

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.Serializable;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.avalon.framework.parameters.Parameters;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.cocoon.ProcessingException;
import org.apache.cocoon.components.source.SourceUtil;
import org.apache.cocoon.environment.SourceResolver;
import org.apache.cocoon.environment.http.HttpEnvironment;
import org.apache.cocoon.generation.ServiceableGenerator;
import org.apache.excalibur.source.Source;
import org.apache.excalibur.source.SourceException;
import org.apache.excalibur.source.SourceValidity;
import org.apache.excalibur.xml.sax.SAXParser;
import org.jbundle.base.screen.control.servlet.BasicServlet;
import org.jbundle.base.screen.control.servlet.ServletTask;
import org.jbundle.base.screen.control.servlet.BaseHttpTask.SERVLET_TYPE;
import org.jbundle.base.screen.control.servlet.xml.XmlScreen;
import org.jbundle.base.screen.model.TopScreen;
import org.jbundle.base.util.DBParams;
import org.jbundle.base.util.Utility;
import org.jbundle.model.RecordOwnerParent;
import org.jbundle.thin.base.db.FieldList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;


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
 * @version CVS $Id: XMLFilter.java,v 1.1 2009/01/16 21:30:08 don Exp $
 */
public class XMLFilter extends ServiceableGenerator
    implements BasicServlet
{
    /** The input source */
    protected Source inputSource;

    /**
     * Recycle this component.
     * All instance variables are set to <code>null</code>.
     */
    public void recycle() {
        if (null != this.inputSource) {
            super.resolver.release(this.inputSource);
            this.inputSource = null;
        }
        super.recycle();
    }

    /**
     * Setup the file generator.
     * Try to get the last modification date of the source for caching.
     */
    public void setup(SourceResolver resolver, Map objectModel, String src, Parameters par)
        throws ProcessingException, SAXException, IOException {

        super.setup(resolver, objectModel, src, par);
        try {
            HttpServletRequest request = (HttpServletRequest) objectModel.get(HttpEnvironment.HTTP_REQUEST_OBJECT);

            inputSource = new MyInputSource(this.getStream(request), request.getRequestURI(), request.getContentType());
        } catch (SourceException se) {
            throw SourceUtil.handle("Error during resolving of '" + src + "'.", se);
        }
    }

    /**
     * Generate the unique key.
     * This key must be unique inside the space of this component.
     *
     * @return The generated key hashes the src
     */
    public Serializable getKey() {
        return this.inputSource.getURI();
    }

    /**
     * Generate the validity object.
     *
     * @return The generated validity object or <code>null</code> if the
     *         component is currently not cacheable.
     */
    public SourceValidity getValidity() {
        return this.inputSource.getValidity();
    }
    
    /**
     * Generate XML data.
     */
    public void generate()
        throws IOException, SAXException, ProcessingException {

            SAXParser parser = null;
            try {
                if (getLogger().isDebugEnabled()) {
                    getLogger().debug("Source " + super.source +
                                      " resolved to " + this.inputSource.getURI());
                }
                parser = (SAXParser) this.manager.lookup(SAXParser.ROLE);
                parser.parse((InputSource)this.inputSource, super.xmlConsumer);
            } catch (SourceException e) {
                throw SourceUtil.handle(e);
            } catch (ServiceException e) {
                throw new ProcessingException("Exception during parsing source.", e);
            } finally {
                manager.release(parser);
            }
    }

    protected static boolean m_bFirstTime = true;
    /**
     * Process the stream and return the request.
     */
    public Reader getStream(HttpServletRequest request)
        throws IOException
    {
        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        try   {
            ServletTask servletTask = new ServletTask(this, SERVLET_TYPE.COCOON);
            if (m_bFirstTime)
            {
                Map<String,Object> properties = servletTask.getApplicationProperties(true);
                if (properties.get(DBParams.DOMAIN) == null)
                {   // TODO(don) Is this necessary?
                    String strDomain = Utility.getDomainFromURL(ServletTask.getParam(request, DBParams.URL), null);
                    if (strDomain != null)
                        properties.put(DBParams.DOMAIN, strDomain);
                }
                m_bFirstTime = false;
            }
            servletTask.doProcess(this, request, null, writer);
            servletTask.free();
        } catch (ServletException ex) {
        } // Never
        writer.flush();
        String string =
//          "<?xml version=\"1.0\"?>" +
//          "<?xml-stylesheet type=\"text/xsl\" href=\"docs/styles/help.xsl\"?>" +
//          "<?cocoon-process type=\"xslt\"?>" +
                stringWriter.toString();
        writer.close();	// Doesn't do anything.
        return new StringReader(string);
    }
    /**
     * Do any of the initial servlet stuff.
     * @param servletTask The calling servlet task.
     */
    public void initServletSession(ServletTask servletTask)
    {
    }
    /**
     * Get the physical path for this internet path.
     */
    public String getRealPath(HttpServletRequest request, String strFilename)
    {
        try {
            Source source = super.resolver.resolveURI(strFilename);
            strFilename = source.getURI();
            if (strFilename.indexOf(':') != -1)
                strFilename = strFilename.substring(strFilename.indexOf(':') + 1);
            return strFilename;
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        String strSeparator = System.getProperty("file.separator");
        return Utility.replace(strFilename, "/", strSeparator); // Never
    }

    /**
     * Set the content type for this type of servlet.
     */
    public void setContentType(HttpServletResponse res)
    {
        // No, set by cocoon/XSL
//      res.setContentType("text/html");
    }

    /**
     * Get the main screen (with the correct view factory!).
     */
    public TopScreen createTopScreen(RecordOwnerParent parent, FieldList recordMain, Object properties)
    {
        return new XmlScreen(parent, recordMain, properties);
    }

    /**
     * Get the output stream.
     */
    public PrintWriter getOutputStream(HttpServletResponse res)
        throws IOException
    {
//      return new PrintWriter(res.getOutputStream());
        return null;    // not needed for filters.
    }
}
