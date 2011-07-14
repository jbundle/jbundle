package org.jbundle.base.message.trx.message.external.convert.jaxb;

import java.util.Hashtable;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.jbundle.util.osgi.ClassFinder;
import org.jbundle.util.osgi.ClassService;
import org.jbundle.util.osgi.finder.ClassServiceUtility;

public class JaxbContexts extends Hashtable<String,JaxbContexts.JAXBContextHolder> {

    public static JaxbContexts gJAXBContexts = null;

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /**
     * 
     */
    public JaxbContexts()
    {
        super();
    }
    /**
     * Get the context holder.
     * Typically you would not call this directly.
     * @param soapPackage
     * @return
     */
    public JAXBContextHolder get(String soapPackage) throws JAXBException
    {
        synchronized(this)
        {
            JAXBContextHolder jAXBContextHolder = super.get(soapPackage);
            if (jAXBContextHolder == null)
            {
            	ClassService classService = ClassServiceUtility.getClassService();
            	String className = soapPackage;
            	if (className.indexOf(':') != -1)
            		className = className.substring(0, className.indexOf(':'));
            	className = className + ".ObjectFactory";
            	
            	Object object = classService.makeObjectFromClassName(className);
            	/*
            	ClassFinder classFinder = classService.getClassFinder(null);
            	Object bundle = classFinder.findBundle(null, null, className, null);

                if (bundle == null) {
                    Object resource = classFinder.deployThisResource(className, true, false);
                    if (resource != null)
                    {
                    	bundle = classFinder.findBundle(null, null, className, null);
                    	if (bundle == null)
                        	bundle = classFinder.findBundle(null, null, className, null);
                    }
                }
*/            	
            	ClassLoader classLoader = this.getClass().getClassLoader();
            	if (object != null)
            		classLoader = object.getClass().getClassLoader();
                JAXBContext jc = JAXBContext.newInstance(soapPackage, classLoader);
                jAXBContextHolder = new JAXBContextHolder(jc);
                this.put(soapPackage, jAXBContextHolder);
            }
            return jAXBContextHolder;
        }
    }
    /**
     * Get the shared unmarshaller for this context.
     * NOTE: Since this is shared, always synchronize on this object.
     * @return
     */
    public Unmarshaller getUnmarshaller(String soapPackage) throws JAXBException
    {
        JAXBContextHolder jAXBContextHolder = this.get(soapPackage);
        if (jAXBContextHolder == null)
            return null;    // Never
        return jAXBContextHolder.getUnmarshaller();
    }
    /**
     * Get the shared unmarshaller for this context.
     * NOTE: Since this is shared, always synchronize on this object.
     * @return
     */
    public Marshaller getMarshaller(String soapPackage) throws JAXBException
    {
        JAXBContextHolder jAXBContextHolder = this.get(soapPackage);
        if (jAXBContextHolder == null)
            return null;    // Never
        return jAXBContextHolder.getMarshaller();
    }
    /**
     * Get the global object
     * @return
     */
    public static JaxbContexts getJAXBContexts()
    {
        if (gJAXBContexts == null)
            gJAXBContexts = new JaxbContexts();
        return gJAXBContexts;
    }
    
    /**
     * Holds the marshaller and unmarshaller.
     * @author don
     */
    class JAXBContextHolder extends Object
    {
        private JAXBContext m_jc = null;
        
        private Unmarshaller m_unmarshaller = null;
        
        private Marshaller m_marshaller = null;
        
        /**
         * Constructor
         */
        public JAXBContextHolder()
        {
            super();
        }
        /**
         * Constructor
         */
        public JAXBContextHolder(JAXBContext jc)
        {
            this();
            this.init(jc);
        }
        /**
         * Constructor
         */
        public void init(JAXBContext jc)
        {
            m_jc = jc;
        }
        /**
         * Get the shared unmarshaller for this context.
         * NOTE: Since this is shared, always synchronize on this object.
         * @return
         */
        public Unmarshaller getUnmarshaller() throws JAXBException
        {
            if (m_unmarshaller == null)
            {
                m_unmarshaller = m_jc.createUnmarshaller();
            }
            return m_unmarshaller;
        }
        /**
         * Get the shared unmarshaller for this context.
         * NOTE: Since this is shared, always synchronize on this object.
         * @return
         */
        public Marshaller getMarshaller() throws JAXBException
        {
            if (m_marshaller == null)
            {
                m_marshaller = m_jc.createMarshaller();
            }
            return m_marshaller;
        }
    }
}
