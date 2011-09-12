/*
 * Copyright © 2011 jbundle.org. All rights reserved.
 */
package org.jbundle.base.message.trx.message.external.convert.jibx;

import java.util.Hashtable;

import org.jbundle.util.osgi.finder.ClassServiceUtility;
import org.jibx.runtime.BindingDirectory;
import org.jibx.runtime.IBindingFactory;
import org.jibx.runtime.IMarshallingContext;
import org.jibx.runtime.IUnmarshallingContext;
import org.jibx.runtime.JiBXException;

public class JibxContexts extends Hashtable<String,JibxContexts.JIBXContextHolder> {

    public static JibxContexts gJAXBContexts = null;
	public static final String DEFAULT_BINDING_NAME = "binding";

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /**
     * 
     */
    public JibxContexts()
    {
        super();
    }
    /**
     * Get the context holder.
     * Typically you would not call this directly.
     * @param packageName
     * @param bindingName The JiBX binding name (defaults to 'binding')
     * @return
     */
    public JIBXContextHolder get(String packageName, String bindingName) throws JiBXException
    {
        synchronized(this)
        {
            JIBXContextHolder jAXBContextHolder = super.get(packageName);
            if (jAXBContextHolder == null)
            {
            	if (bindingName == null)
            		bindingName = DEFAULT_BINDING_NAME;
            	ClassLoader classLoader = null;
            	try {
					classLoader = ClassServiceUtility.getClassService().getBundleClassLoader(packageName);
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
    			IBindingFactory jc = BindingDirectory.getFactory(bindingName, packageName, classLoader);
                jAXBContextHolder = new JIBXContextHolder(jc);
                this.put(packageName, jAXBContextHolder);
            }
            return jAXBContextHolder;
        }
    }
    /**
     * Get the shared unmarshaller for this context.
     * NOTE: Since this is shared, always synchronize on this object.
     * @param bindingName The JiBX binding name (defaults to 'binding')
     * @return
     */
    public IUnmarshallingContext getUnmarshaller(String packageName, String bindingName) throws JiBXException
    {
        JIBXContextHolder jAXBContextHolder = this.get(packageName, bindingName);
        if (jAXBContextHolder == null)
            return null;    // Never
        return jAXBContextHolder.getUnmarshaller();
    }
    /**
     * Get the shared unmarshaller for this context.
     * NOTE: Since this is shared, always synchronize on this object.
     * @param bindingName The JiBX binding name (defaults to 'binding')
     * @return
     */
    public IMarshallingContext getMarshaller(String packageName, String bindingName) throws JiBXException
    {
        JIBXContextHolder jAXBContextHolder = this.get(packageName, bindingName);
        if (jAXBContextHolder == null)
            return null;    // Never
        return jAXBContextHolder.getMarshaller();
    }
    /**
     * Get the global object
     * @return
     */
    public static JibxContexts getJIBXContexts()
    {
        if (gJAXBContexts == null)
            gJAXBContexts = new JibxContexts();
        return gJAXBContexts;
    }
    
    /**
     * Holds the marshaller and unmarshaller.
     * @author don
     */
    class JIBXContextHolder extends Object
    {
        private IBindingFactory m_jc = null;
        
        private IUnmarshallingContext m_unmarshaller = null;
        
        private IMarshallingContext m_marshaller = null;
        
        /**
         * Constructor
         */
        public JIBXContextHolder()
        {
            super();
        }
        /**
         * Constructor
         */
        public JIBXContextHolder(IBindingFactory jc)
        {
            this();
            this.init(jc);
        }
        /**
         * Constructor
         */
        public void init(IBindingFactory jc)
        {
            m_jc = jc;
        }
        /**
         * Get the shared unmarshaller for this context.
         * NOTE: Since this is shared, always synchronize on this object.
         * @return
         */
        public IUnmarshallingContext getUnmarshaller() throws JiBXException
        {
            if (m_unmarshaller == null)
            {
                m_unmarshaller = m_jc.createUnmarshallingContext();
            }
            return m_unmarshaller;
        }
        /**
         * Get the shared unmarshaller for this context.
         * NOTE: Since this is shared, always synchronize on this object.
         * @return
         */
        public IMarshallingContext getMarshaller() throws JiBXException
        {
            if (m_marshaller == null)
            {
                m_marshaller = m_jc.createMarshallingContext();
            }
            return m_marshaller;
        }
        /**
         * 
         * @return
         */
        public IBindingFactory getFactory()
        {
        	return m_jc;
        }
    }
}
