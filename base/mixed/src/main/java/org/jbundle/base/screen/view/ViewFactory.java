package org.jbundle.base.screen.view;

/**
 * @(#)ScreenField.java   0.00 12-Feb-97 Don Corley
 *
 * Copyright (c) 2009 tourapp.com. All Rights Reserved.
 *      don@tourgeek.com
 */
import java.util.Hashtable;
import java.util.Properties;

import org.jbundle.base.screen.model.ScreenField;
import org.jbundle.base.util.DBConstants;
import org.jbundle.util.osgi.finder.ClassServiceUtility;


/**
 * ScreenField - This is the information which tells the system about a field on the
 *  screen... where it is, what type it is, and the file and field position.
 */
public class ViewFactory extends Object
{
    /**
     * All current view factories. Entered by package.
     */
    protected static Hashtable<String,ViewFactory> m_htFactories = new Hashtable<String,ViewFactory>();
    /**
     * Should I cache the model lookups?
     */
    public static final boolean ENABLE_CACHE = true;        // Enable the factory name cache?
    /**
     * The screen package.
     */
    public static final String SCREEN_DIR = "base.screen.";
    /**
     * The full screen package.
     */
    public static final String SCREEN_PACKAGE = DBConstants.ROOT_PACKAGE + SCREEN_DIR;
    /**
     * The screen view package.
     */
    public static final String SCREEN_VIEW_PACKAGE = SCREEN_PACKAGE + "view.";
    /**
     * The screen model package.
     */
    public static final String SCREEN_MODEL_PACKAGE = SCREEN_PACKAGE + "model.";
    /**
     * The model to view lookup cache.
     */
    protected Properties m_classCache = new Properties();

    /**
     * The subpackage name (such as swing/xml/etc.).
     */
    protected String m_strSubPackage = null;
    /**
     * The prefix to add on the view classes.
     */
    protected char m_chPrefix = 'V';

    /**
     * Constructor.
     */
    public ViewFactory()
    {
        super();
    }
    /**
     * Constructor.
     * @param strSubPackage The subpackage name (such as swing/xml/etc.).
     * @param chPrefix The prefix to add on the view classes.
     */
    public ViewFactory(String strSubPackage, char chPrefix)
    {
        this();
        this.init(strSubPackage, chPrefix);
    }
    /**
     * Constructor.
     * @param strSubPackage The subpackage name (such as swing/xml/etc.).
     * @param chPrefix The prefix to add on the view classes.
     */
    public void init(String strSubPackage, char chPrefix)
    {
        m_strSubPackage = strSubPackage;
        m_chPrefix = chPrefix;
    }
    /**
     * Free.
     */
    public void free()
    {
//      if (m_screen != null)
        {
//          m_screen = null;
        }
    }
    /**
     * Create the field view.
     * @param model The model to set up a view for.
     * @param screenFieldClass The screen field Class
     * @param bEditableControl Should this view be editable?
     * @return The new view.
     */
    public ScreenFieldView setupScreenFieldView(ScreenField model, Class<?> screenFieldClass, boolean bEditableControl)
    {
        if (screenFieldClass == null)
            screenFieldClass = model.getClass();
        ScreenFieldView screenFieldView = this.getViewClassForModel(screenFieldClass);
        screenFieldView.init(model, bEditableControl);
        return screenFieldView;
    }
    /**
     * Create the field view.
     * @param screenFieldClass The screen field Class
     * @param The model to create a view for.
     * @return The new view for this model.
     */
    public ScreenFieldView getViewClassForModel(Class<?> screenFieldClass)
    {
        while (screenFieldClass != null)
        {
            String strModelClassName = screenFieldClass.getName();
            String strViewClassName = null;
            if (ENABLE_CACHE)
                strViewClassName = m_classCache.getProperty(strModelClassName);   // Name in cache?
            if (strViewClassName == null)
                strViewClassName = this.getViewClassNameFromModelClassName(strModelClassName);
            if (strViewClassName != null)
            {   // Great, found the class name. Try to instantiate the class.
            	ScreenFieldView view = (ScreenFieldView)ClassServiceUtility.getClassService().makeObjectFromClassName(strViewClassName, null, false);	// Ignore class not found
                if (view != null)
                {
                    if (ENABLE_CACHE)
                        m_classCache.setProperty(strModelClassName, strViewClassName);  // Success - cache the name for later
                    return view;
                }
            }
            screenFieldClass = screenFieldClass.getSuperclass();
        }
        return null;
    }
    /**
     * Give the model name, return the view name.
     * @param strModelClassName The model name.
     * @return The view name for this model.
     */
    public String getViewClassNameFromModelClassName(String strModelClassName)
    {
        if (!strModelClassName.startsWith(SCREEN_MODEL_PACKAGE))
            return null;    // Must be one of my native screen models. (Override to change!)
        String strPackagePrefix = DBConstants.BLANK;
        int iLastDot = strModelClassName.lastIndexOf('.');
        if (iLastDot != -1)
        {
            int iModelEnd = strModelClassName.lastIndexOf(SCREEN_MODEL_PACKAGE);
            if (iModelEnd != -1)
            {
                iModelEnd += SCREEN_MODEL_PACKAGE.length();
                if (iModelEnd < iLastDot)
                    strPackagePrefix = strModelClassName.substring(iModelEnd, iLastDot);
            }
            strModelClassName = strModelClassName.substring(iLastDot + 1);
        }
        return this.assembleViewClassName(strPackagePrefix, strModelClassName);
    }
    /**
     * Give the model name, return the view name.
     * @param strPackagePrefix The prefix package to place before the class name (if any).
     * @param strModelClassName The Model's class name.
     * @return The class name for the view.
     */
    public String assembleViewClassName(String strPackagePrefix, String strModelClassName)
    {
        if (strModelClassName.length() > 1)
            if (Character.isUpperCase(strModelClassName.charAt(1)))
                if (strModelClassName.charAt(0) == 'S')
                    strModelClassName = strModelClassName.substring(1);
        String strViewDir = this.getViewSubpackage();
        char chViewPrefix = this.getViewPrefix();
        if (strPackagePrefix.length() > 0)
            strPackagePrefix += '.';
        strModelClassName = SCREEN_VIEW_PACKAGE + strViewDir + '.' + strPackagePrefix + chViewPrefix + strModelClassName;
        return strModelClassName;
    }
    /**
     * Get the view subpackage name (such as swing/xml/etc.).
     * @return the subpackage name.
     */
    public String getViewSubpackage()
    {
        return m_strSubPackage;
    }
    /**
     * Get the first letter of the view's classes.
     * @return The view classes prefix.
     */
    public char getViewPrefix()
    {
        return m_chPrefix;
    }
    /**
     * Lookup a standard view factory.
     * @param strSubPackage The subpackage name (such as swing/xml/etc.).
     * @param chPrefix The prefix to add on the view classes.
     * @return The View Factory for this sub-package.
     */
    public static ViewFactory getViewFactory(String strSubPackage, char chPrefix)
    {
        ViewFactory viewFactory = null;
        viewFactory = (ViewFactory)m_htFactories.get(strSubPackage);
        if (viewFactory == null)
        {
            viewFactory = new ViewFactory(strSubPackage, chPrefix);
            m_htFactories.put(strSubPackage, viewFactory);
        }
        return viewFactory;
    }
}
