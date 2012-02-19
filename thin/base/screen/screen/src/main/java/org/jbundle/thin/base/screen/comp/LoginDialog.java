/*
 * PrintDialog.java
 *
 * Created on August 22, 2005, 3:32 AM

 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.thin.base.screen.comp;

import java.awt.Frame;

import javax.swing.JOptionPane;

import org.jbundle.thin.base.screen.landf.ScreenUtil;

/**
 *
 * @author  don
 */
public class LoginDialog extends javax.swing.JDialog
{

	private static final long serialVersionUID = 1L;
	
	public static final int NEW_USER_OPTION = 3;

    /**
     * Creates new form PrintDialog
     */
    public LoginDialog(java.awt.Frame parent, boolean modal, String initialMessage, String initialUsername)
    {
        super(parent, modal);
        initComponents(initialMessage, initialUsername);
    }
    /**
     * Get the return status.
     * @return the return status of this dialog - one of RET_OK or RET_CANCEL
     */
    public int getReturnStatus()
    {
        return returnStatus;
    }
    /**
     * Get the user name that was typed in.
     */
    public String getUserName()
    {
        return userField.getText();
    }
    /**
     * Get the password that was typed in.
     */
    public String getPassword()
    {
    	if ((password != null) && (password.length() > 0))
    		return password;	// External override
        char[] rgchPassword = passwordField.getPassword();
        return new String(rgchPassword);
    }    
    /**
     * Get the user name that was typed in.
     */
    public void setUserName(String string)
    {
        userField.setText(string);
    }
    /**
     * Set the save checkbox.
     * @param isSelected
     */
    public void setSaveInfo(boolean isSelected)
    {
    	saveInfoCheckbox.setSelected(isSelected);
    }
    /**
     * Get the password that was typed in.
     */
    public void setPassword(String password)
    {
        this.password = password;
    }    
    /**
     * Get the state of the "Save Info" checkbox.
     */
    public boolean getSaveState()
    {
        boolean bState = saveInfoCheckbox.isSelected();
        return bState;
    }
    /**
     *  This method is called from within the constructor to
     * initialize the form.
     */
    private void initComponents(String initialMessage, String initialUsername)
    {
        java.awt.GridBagConstraints gridBagConstraints;

        getContentPane().setLayout(new java.awt.GridBagLayout());

        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                closeDialog(evt);
            }
        });

        userMessage = new javax.swing.JLabel();
        userMessage.setText(initialMessage);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        getContentPane().add(userMessage, gridBagConstraints);

        userLabel = new javax.swing.JLabel("Email address");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        getContentPane().add(userLabel, gridBagConstraints);

        userField = new javax.swing.JTextField(20);
        userField.setText(initialUsername);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        getContentPane().add(userField, gridBagConstraints);

        passwordLabel = new javax.swing.JLabel("Password");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        getContentPane().add(passwordLabel, gridBagConstraints);
        
        passwordField = new javax.swing.JPasswordField(20);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        getContentPane().add(passwordField, gridBagConstraints);

        saveInfoCheckbox = new javax.swing.JCheckBox("Remember me?");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        getContentPane().add(saveInfoCheckbox, gridBagConstraints);

        loginButton = new javax.swing.JButton("Login");
        loginButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                loginButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        getContentPane().add(loginButton, gridBagConstraints);
        getRootPane().setDefaultButton(loginButton);
        
        newUserButton = new javax.swing.JButton("Create new user");
        newUserButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                newUserButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        getContentPane().add(newUserButton, gridBagConstraints);
        
        cancelButton = new javax.swing.JButton("Cancel");
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        getContentPane().add(cancelButton, gridBagConstraints);

        pack();
    }
    // </editor-fold>//GEN-END:initComponents
        
    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
        doClose(JOptionPane.CANCEL_OPTION);
    }//GEN-LAST:event_cancelButtonActionPerformed
    
    private void loginButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
        doClose(JOptionPane.OK_OPTION);
    }//GEN-LAST:event_cancelButtonActionPerformed
    
    private void newUserButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
        //doClose(JOptionPane.CANCEL_OPTION);
        // Display new user dialog:
        Frame frame = ScreenUtil.getFrame(this);
        frame.remove(this);
        String strDisplay = "Create new user";
        String strUserName = userField.getText();
        NewUserDialog dialog = new NewUserDialog(frame, true, strDisplay, strUserName, this);
        if (frame != null)
            ScreenUtil.centerDialogInFrame(dialog, frame);
        dialog.setVisible(true);
        doClose(dialog.getReturnStatus());	// Pass new user dialog up
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
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton cancelButton;
    private javax.swing.JButton loginButton;
    private javax.swing.JButton newUserButton;
    private javax.swing.JLabel userMessage;
    private javax.swing.JLabel userLabel;
    private javax.swing.JLabel passwordLabel;
    private javax.swing.JCheckBox saveInfoCheckbox;
    private javax.swing.JTextField userField;
    private javax.swing.JPasswordField passwordField;
    // End of variables declaration//GEN-END:variables
    
    private String password = null;
    
    private int returnStatus = JOptionPane.OK_OPTION;
    
}
