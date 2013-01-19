/*
 * ScreenDialog.java
 *
 * Created on August 23, 2005, 3:15 AM

 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.thin.base.screen.landf;

import java.awt.Color;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.SystemColor;
import java.awt.Toolkit;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.LookAndFeel;
import javax.swing.UIManager;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.metal.MetalTheme;
import javax.swing.plaf.metal.OceanTheme;

import org.jbundle.thin.base.screen.landf.theme.BlueTheme;
import org.jbundle.thin.base.screen.landf.theme.GrayTheme;
import org.jbundle.thin.base.screen.landf.theme.RedTheme;


/**
 *
 * @author  don
 */
public class ScreenDialog extends javax.swing.JDialog
{
	private static final long serialVersionUID = 1L;

    private Map<String,Object> properties = null;
    private Map<String,String> mapLAndF = null;
    private Map<String,String> mapThemes = new HashMap<String,String>();
    
    public static final String DIALOG_BUNDLE = "org/jbundle/res/thin/base/screen/DialogBundleResources";

    /** Creates new form ScreenDialog */
    public ScreenDialog(java.awt.Frame parent, Map<String,Object> properties)
    {
        super(parent, true);
        this.setProperties(properties);
        initComponents();
        this.setSampleStyle();
    }
    
    public void setProperties(Map<String,Object> properties)
    {
        this.properties = properties;
    }
    
    public Map<String,Object> getProperties()
    {
        return properties;
    }
    /**
     * @return the return status of this dialog - one of RET_OK or RET_CANCEL
     */
    public int getReturnStatus()
    {
        return returnStatus;
    }
    /**
     * Reset all the preferences to the default.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        lookAndFeelLabel = new javax.swing.JLabel();
        lookAndFeelComboBox = new javax.swing.JComboBox();
        // Look and Feel
        String landfCurrent = (String)properties.get(ScreenUtil.LOOK_AND_FEEL);
        if (landfCurrent == null)
        landfCurrent = ScreenUtil.DEFAULT;

        mapLAndF = new HashMap<String,String>();
        ScreenDialog.addLookAndFeelItem(mapLAndF, java.util.ResourceBundle.getBundle(DIALOG_BUNDLE).getString(ScreenUtil.DEFAULT), ScreenUtil.DEFAULT, landfCurrent, lookAndFeelComboBox);
        ScreenDialog.addLookAndFeelItem(mapLAndF, java.util.ResourceBundle.getBundle(DIALOG_BUNDLE).getString(ScreenUtil.SYSTEM), ScreenUtil.SYSTEM, landfCurrent, lookAndFeelComboBox);

        UIManager.LookAndFeelInfo[] rgLandFInfo = UIManager.getInstalledLookAndFeels();
        for (int i = 0; i < rgLandFInfo.length; i++)
        {
        	ScreenDialog.addLookAndFeelItem(mapLAndF, rgLandFInfo[i].getName(), rgLandFInfo[i].getClassName(), landfCurrent, lookAndFeelComboBox);
        }
        LookAndFeel[] rgLandF = UIManager.getAuxiliaryLookAndFeels();
        if (rgLandF != null)
        {
            for (int i = 0; i < rgLandF.length; i++)
            {
            	ScreenDialog.addLookAndFeelItem(mapLAndF, rgLandF[i].getName(), rgLandF[i].getClass().getName(), landfCurrent, lookAndFeelComboBox);
            }
        }

        themeLabel = new javax.swing.JLabel();
        themeComboBox = new javax.swing.JComboBox();
        String currentThemeName = (String)properties.get(ScreenUtil.THEME);
        if (currentThemeName == null)
        currentThemeName = ScreenUtil.DEFAULT;
        mapThemes = new HashMap<String,String>();

        ScreenDialog.addThemeItem(mapThemes, java.util.ResourceBundle.getBundle(DIALOG_BUNDLE).getString(ScreenUtil.DEFAULT), ScreenUtil.DEFAULT, null, currentThemeName, themeComboBox);
        ScreenDialog.addThemeItem(mapThemes, null, null, new BlueTheme(), currentThemeName, themeComboBox);
        ScreenDialog.addThemeItem(mapThemes, null, null, new GrayTheme(), currentThemeName, themeComboBox);
        ScreenDialog.addThemeItem(mapThemes, null, null, new RedTheme(), currentThemeName, themeComboBox);
        ScreenDialog.addThemeItem(mapThemes, null, null, new OceanTheme(), currentThemeName, themeComboBox);

        fontLabel = new javax.swing.JLabel();
        fontComboBox = new javax.swing.JComboBox();
        // Font
        String currentFontName = (String)properties.get(ScreenUtil.FONT_NAME);
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        fontComboBox.addItem(this.getDefaultText());
        fontComboBox.setSelectedIndex(0);
        if (toolkit != null)
        {
            String[] astrFont = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();    //toolkit.getFontList();
            for (int i = 0; i < astrFont.length; i++)
            {
                fontComboBox.addItem(astrFont[i]);
                if (astrFont[i].equals(currentFontName))
                fontComboBox.setSelectedItem(astrFont[i]);      // To select the correct object
            }
        }

        sizeLabel = new javax.swing.JLabel();
        sizeComboBox = new javax.swing.JComboBox();
        // Size
        String currentFontSize = (String)properties.get(ScreenUtil.FONT_SIZE);
        String rgSizes[] = {"6", "8", "10", "12", "14", "18", "22", "28"};
        sizeComboBox.addItem(getDefaultText());
        sizeComboBox.setSelectedIndex(0);
        for (int i = 0; i < rgSizes.length; i++)
        {
            sizeComboBox.addItem(rgSizes[i]);
            if (rgSizes[i].equals(currentFontSize))
            sizeComboBox.setSelectedItem(rgSizes[i]);   // To select the correct object
        }

        colorPanel = new javax.swing.JPanel();
        textColorButton = new javax.swing.JButton();
        controlColorButton = new javax.swing.JButton();
        backgroundButton = new javax.swing.JButton();
        samplePanel = new javax.swing.JPanel();
        sampleLabel = new javax.swing.JLabel();
        sampleTextField = new javax.swing.JTextField();
        buttonPanel = new javax.swing.JPanel();
        okButton = new javax.swing.JButton();
        cancelButton = new javax.swing.JButton();
        resetButton = new javax.swing.JButton();

        getContentPane().setLayout(new java.awt.GridBagLayout());

        setTitle(java.util.ResourceBundle.getBundle(DIALOG_BUNDLE).getString("title"));
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                closeDialog(evt);
            }
        });

        lookAndFeelLabel.setLabelFor(lookAndFeelComboBox);
        lookAndFeelLabel.setText(java.util.ResourceBundle.getBundle(DIALOG_BUNDLE).getString("landf"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        getContentPane().add(lookAndFeelLabel, gridBagConstraints);

        lookAndFeelComboBox.setEditable(true);
        lookAndFeelComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                updateLookAndFeel(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        getContentPane().add(lookAndFeelComboBox, gridBagConstraints);

        themeLabel.setLabelFor(themeComboBox);
        themeLabel.setText("Theme:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        getContentPane().add(themeLabel, gridBagConstraints);
        themeLabel.getAccessibleContext().setAccessibleName(java.util.ResourceBundle.getBundle(DIALOG_BUNDLE).getString("theme"));

        themeComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                themeComboBoxActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        getContentPane().add(themeComboBox, gridBagConstraints);

        fontLabel.setLabelFor(fontComboBox);
        fontLabel.setText("Font:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        getContentPane().add(fontLabel, gridBagConstraints);
        fontLabel.getAccessibleContext().setAccessibleName(java.util.ResourceBundle.getBundle(DIALOG_BUNDLE).getString("font"));

        fontComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fontComboBoxActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        getContentPane().add(fontComboBox, gridBagConstraints);

        sizeLabel.setLabelFor(sizeComboBox);
        sizeLabel.setText("Size:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        getContentPane().add(sizeLabel, gridBagConstraints);
        sizeLabel.getAccessibleContext().setAccessibleName(java.util.ResourceBundle.getBundle(DIALOG_BUNDLE).getString("size"));

        sizeComboBox.setEditable(true);
        sizeComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sizeComboBoxActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        getContentPane().add(sizeComboBox, gridBagConstraints);

        colorPanel.setLayout(new java.awt.GridBagLayout());

        textColorButton.setText(java.util.ResourceBundle.getBundle(DIALOG_BUNDLE).getString("textcolor"));
        textColorButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                textColorButtonActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        colorPanel.add(textColorButton, gridBagConstraints);

        controlColorButton.setText(java.util.ResourceBundle.getBundle(DIALOG_BUNDLE).getString("controlcolor"));
        controlColorButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                controlColorButtonActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        colorPanel.add(controlColorButton, gridBagConstraints);

        backgroundButton.setText(java.util.ResourceBundle.getBundle(DIALOG_BUNDLE).getString("backgroundcolor"));
        backgroundButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                backgroundButtonActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        colorPanel.add(backgroundButton, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        getContentPane().add(colorPanel, gridBagConstraints);

        samplePanel.setLayout(new java.awt.GridBagLayout());

        samplePanel.setBorder(new javax.swing.border.TitledBorder(java.util.ResourceBundle.getBundle(DIALOG_BUNDLE).getString("sample")));
        sampleLabel.setText(java.util.ResourceBundle.getBundle(DIALOG_BUNDLE).getString("label"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        samplePanel.add(sampleLabel, gridBagConstraints);

        sampleTextField.setColumns(10);
        sampleTextField.setText(java.util.ResourceBundle.getBundle(DIALOG_BUNDLE).getString("control"));
        sampleTextField.setMinimumSize(new java.awt.Dimension(50, 19));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        samplePanel.add(sampleTextField, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.gridheight = java.awt.GridBagConstraints.RELATIVE;
        gridBagConstraints.weightx = 0.5;
        getContentPane().add(samplePanel, gridBagConstraints);

        buttonPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT));

        okButton.setText(java.util.ResourceBundle.getBundle(DIALOG_BUNDLE).getString("ok"));
        okButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okButtonActionPerformed(evt);
            }
        });

        buttonPanel.add(okButton);

        cancelButton.setText(java.util.ResourceBundle.getBundle(DIALOG_BUNDLE).getString("cancel"));
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });

        buttonPanel.add(cancelButton);

        resetButton.setText(java.util.ResourceBundle.getBundle(DIALOG_BUNDLE).getString("reset"));
        resetButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                resetButtonActionPerformed(evt);
            }
        });

        buttonPanel.add(resetButton);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTH;
        getContentPane().add(buttonPanel, gridBagConstraints);

        pack();
    }
    // </editor-fold>//GEN-END:initComponents

    private void resetButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_resetButtonActionPerformed
        properties = new Hashtable<String,Object>();
        lookAndFeelComboBox.setSelectedItem(this.getDefaultText());
        sizeComboBox.setSelectedItem(this.getDefaultText());
        fontComboBox.setSelectedItem(this.getDefaultText());
        themeComboBox.setSelectedItem(this.getDefaultText());

        ScreenUtil.updateLookAndFeel(this, null, properties);
        this.setSampleStyle();
    }//GEN-LAST:event_resetButtonActionPerformed

    private void sizeComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sizeComboBoxActionPerformed
        String fontSize = (String)sizeComboBox.getSelectedItem();
        if (!this.getDefaultText().equalsIgnoreCase(fontSize))
            properties.put(ScreenUtil.FONT_SIZE, fontSize);
        else
            properties.remove(ScreenUtil.FONT_SIZE);

        ScreenUtil.updateLookAndFeel(this, null, properties);
        this.setSampleStyle();
    }//GEN-LAST:event_sizeComboBoxActionPerformed

    private void fontComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fontComboBoxActionPerformed
        String fontName = (String)fontComboBox.getSelectedItem();
        if (!this.getDefaultText().equalsIgnoreCase(fontName))
            properties.put(ScreenUtil.FONT_NAME, fontName);
        else
            properties.remove(ScreenUtil.FONT_NAME);

        ScreenUtil.updateLookAndFeel(this, null, properties);
        this.setSampleStyle();
    }//GEN-LAST:event_fontComboBoxActionPerformed

    private void themeComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_themeComboBoxActionPerformed
        String themeName = (String)themeComboBox.getSelectedItem();
        String themeClassName = (String)mapThemes.get(themeName);
        if ((themeClassName != null) && (!ScreenUtil.DEFAULT.equalsIgnoreCase(themeClassName)))
            properties.put(ScreenUtil.THEME, themeClassName);
        else
            properties.remove(ScreenUtil.THEME);

        ScreenUtil.updateLookAndFeel(this, null, properties);
        this.setSampleStyle();
    }//GEN-LAST:event_themeComboBoxActionPerformed

    private void backgroundButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_backgroundButtonActionPerformed
        Color color = ScreenUtil.getColor(ScreenUtil.BACKGROUND_COLOR, null, properties);
        color = JColorChooser.showDialog(this, java.util.ResourceBundle.getBundle(DIALOG_BUNDLE).getString("choosebackgroundcolor"), color);
        if (color != null)
            ScreenUtil.setColor(ScreenUtil.BACKGROUND_COLOR, new ColorUIResource(color), null, properties);
        else
            properties.remove(ScreenUtil.BACKGROUND_COLOR);
        
        ScreenUtil.updateLookAndFeel(this, null, properties);
        this.setSampleStyle();
    }//GEN-LAST:event_backgroundButtonActionPerformed

    private void controlColorButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_controlColorButtonActionPerformed
        Color color = ScreenUtil.getColor(ScreenUtil.CONTROL_COLOR, null, properties);
        color = JColorChooser.showDialog(this, java.util.ResourceBundle.getBundle(DIALOG_BUNDLE).getString("choosecontrolcolor"), color);
        if (color != null)
            ScreenUtil.setColor(ScreenUtil.CONTROL_COLOR, new ColorUIResource(color), null, properties);
        else
            properties.remove(ScreenUtil.CONTROL_COLOR);
        
        ScreenUtil.updateLookAndFeel(this, null, properties);
        this.setSampleStyle();
    }//GEN-LAST:event_controlColorButtonActionPerformed

    private void textColorButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_textColorButtonActionPerformed
        Color color = ScreenUtil.getColor(ScreenUtil.TEXT_COLOR, null, properties);
        color = JColorChooser.showDialog(this, java.util.ResourceBundle.getBundle(DIALOG_BUNDLE).getString("choosetextcolor"), color);
        if (color != null)
            ScreenUtil.setColor(ScreenUtil.TEXT_COLOR, new ColorUIResource(color), null, properties);
        else
            properties.remove(ScreenUtil.TEXT_COLOR);
        
        ScreenUtil.updateLookAndFeel(this, null, properties);
        this.setSampleStyle();
    }//GEN-LAST:event_textColorButtonActionPerformed

    private void updateLookAndFeel(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_updateLookAndFeel
        String lookAndFeelName = (String)lookAndFeelComboBox.getSelectedItem();
        String lookAndFeelClassName = (String)mapLAndF.get(lookAndFeelName);
        if ((lookAndFeelClassName != null) && (!ScreenUtil.DEFAULT.equalsIgnoreCase(lookAndFeelClassName)))
            properties.put(ScreenUtil.LOOK_AND_FEEL, lookAndFeelClassName);
        else
            properties.remove(ScreenUtil.LOOK_AND_FEEL);

        ScreenUtil.updateLookAndFeel(this, null, properties);
        this.setSampleStyle();
    }//GEN-LAST:event_updateLookAndFeel
    
    private void okButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_okButtonActionPerformed
        doClose(JOptionPane.OK_OPTION);
    }//GEN-LAST:event_okButtonActionPerformed
    
    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
        doClose(JOptionPane.CANCEL_OPTION);
    }//GEN-LAST:event_cancelButtonActionPerformed
    
    /** Closes the dialog */
    private void closeDialog(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_closeDialog
        doClose(JOptionPane.CANCEL_OPTION);
    }//GEN-LAST:event_closeDialog
    
    private void doClose(int retStatus) {
        returnStatus = retStatus;
        setVisible(false);
        dispose();
    }
    
    private String getDefaultText()
    {
        return java.util.ResourceBundle.getBundle(DIALOG_BUNDLE).getString(ScreenUtil.DEFAULT);
    }
    
    public static void addThemeItem(Map<String,String> mapThemes, String name, String className, MetalTheme theme, String defaultThemeClassName, JComboBox comboBox)
    {
        if (theme != null)
        {
            name = theme.getName();
            className = theme.getClass().getName();
        }
        mapThemes.put(name, className);
        if (comboBox != null)
        {
            comboBox.addItem(name);
            if (className.equalsIgnoreCase(defaultThemeClassName))
                comboBox.setSelectedItem(name);
        }
    }

    public static void addLookAndFeelItem(Map<String,String> mapLAndF, String name, String className, String defaultClassName, JComboBox comboBox)
    {
        mapLAndF.put(name, className);
        if (comboBox != null)
        {
            comboBox.addItem(name);
            if (className.equals(defaultClassName))
                comboBox.setSelectedItem(name);        // To select the correct object
        }
    }
    /**
     * Handle the button actions.
     */
    public void setSampleStyle()
    {
        Color colorBackground = ScreenUtil.getColor(ScreenUtil.BACKGROUND_COLOR, null, properties);
        Color colorControl = ScreenUtil.getColor(ScreenUtil.CONTROL_COLOR, null, properties);
        Color colorText = ScreenUtil.getColor(ScreenUtil.TEXT_COLOR, null, properties);
        samplePanel.setBackground(colorBackground);
        if (colorBackground == null)
            samplePanel.setBackground(SystemColor.window);
        Font font = ScreenUtil.getFont(null, properties, true);
        sampleLabel.setFont(font);
        sampleTextField.setFont(font);
        sampleTextField.setBackground(colorControl);
        if (colorControl == null)
            sampleTextField.setBackground(SystemColor.text);
        sampleLabel.setForeground(colorText);
        if (colorText == null)
            sampleLabel.setForeground(SystemColor.textText);
        sampleTextField.setForeground(colorText);
        if (colorText == null)
            sampleTextField.setForeground(SystemColor.textText);

        if (colorBackground != null)
            backgroundButton.setBackground(colorBackground);
        if (colorControl != null)
            controlColorButton.setBackground(colorControl);
        if (colorText != null)
            textColorButton.setBackground(colorText);

        this.invalidate();
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton backgroundButton;
    private javax.swing.JPanel buttonPanel;
    private javax.swing.JButton cancelButton;
    private javax.swing.JPanel colorPanel;
    private javax.swing.JButton controlColorButton;
    private javax.swing.JComboBox fontComboBox;
    private javax.swing.JLabel fontLabel;
    private javax.swing.JComboBox lookAndFeelComboBox;
    private javax.swing.JLabel lookAndFeelLabel;
    private javax.swing.JButton okButton;
    private javax.swing.JButton resetButton;
    private javax.swing.JLabel sampleLabel;
    private javax.swing.JPanel samplePanel;
    private javax.swing.JTextField sampleTextField;
    private javax.swing.JComboBox sizeComboBox;
    private javax.swing.JLabel sizeLabel;
    private javax.swing.JButton textColorButton;
    private javax.swing.JComboBox themeComboBox;
    private javax.swing.JLabel themeLabel;
    // End of variables declaration//GEN-END:variables
    
    private int returnStatus = JOptionPane.CANCEL_OPTION;
}
