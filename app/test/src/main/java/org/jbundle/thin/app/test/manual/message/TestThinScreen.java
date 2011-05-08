package org.jbundle.thin.app.test.manual.message;

/**
 * OrderEntry.java:   Applet
 *  Copyright � 1997 jbundle.org. All rights reserved.
 *  
 *  @author Don Corley don@tourgeek.com
 *  @version 1.0.0.
 */
import java.awt.Component;
import java.awt.event.FocusEvent;

import javax.swing.JComponent;
import javax.swing.text.JTextComponent;

import org.jbundle.thin.app.test.test.db.TestTable;
import org.jbundle.thin.base.db.FieldInfo;
import org.jbundle.thin.base.db.FieldList;
import org.jbundle.thin.base.screen.JScreen;
import org.jbundle.thin.base.screen.JScreenToolbar;
import org.jbundle.thin.base.util.ThinMessageManager;


/**
 * Main Class for applet OrderEntry
 */
public class TestThinScreen extends JScreen
{
    private static final long serialVersionUID = 1L;

    /**
     * OrderEntry Class Constructor.
     */
    public TestThinScreen() {
        super();
    }

    /**
     * OrderEntry Class Constructor.
     */
    public TestThinScreen(Object parent, Object obj) {
        this();
        this.init(parent, obj);
    }

    /**
     * The init() method is called by the AWT when an applet is first loaded or
     * reloaded. Override this method to perform whatever initialization your
     * applet needs, such as initializing data structures, loading images or
     * fonts, creating frame windows, setting the layout manager, or adding UI
     * components.
     */
    public void init(Object parent, Object obj) {
        // m_parent = parent;
        // FieldList fieldList = this.buildFieldList();
        // RemoteSession remoteSession =
        // this.getBaseApplet().makeRemoteSession(null,
        // ".test.manual.optcode.thinmessage.remote.TestSession");
        // this.getBaseApplet().linkRemoteSessionTable(remoteSession, fieldList,
        // false);

        super.init(parent, obj);

        
        ThinMessageManager.createScreenMessageListener(this.getFieldList(), this);
    }
    /**
     * Cleanup.
     */
    public void free()
    {
        ThinMessageManager.freeScreenMessageListeners(this);
        
        super.free();
    }

    /**
     * Build the list of fields that make up the screen.
     */
    public FieldList buildFieldList() {
        return new TestTable(null); // If overriding class didn't set this
    }

    /**
     * When a control loses focus, move the field to the data area.
     */
    public void focusLost(FocusEvent e) {
        super.focusLost(e);
        m_componentNextFocus = null;
        Component component = (Component) e.getSource();
        if (component instanceof JTextComponent) {
            String string = component.getName();
            FieldInfo field = this.getFieldList().getField(string); // Get the fieldInfo for this component
            if (field != null)
                if ("TestCode".equals(string)) {
                    FieldList fieldList = this.getFieldList();
                    fieldList.setKeyArea("TestCode");
                    this.readKeyed(field);
                }
        }
    }

    /**
     * Add the toolbars?
     */
    public JComponent createToolbar() {
        return new JScreenToolbar(this, null);
    }
}
