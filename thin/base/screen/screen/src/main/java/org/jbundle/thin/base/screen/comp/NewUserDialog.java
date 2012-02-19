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
public class NewUserDialog extends javax.swing.JDialog
{

	private static final long serialVersionUID = 1L;

    /**
     * Creates new form PrintDialog
     */
    public NewUserDialog(java.awt.Frame parent, boolean modal, String initialMessage, String initialUsername, LoginDialog loginDialog)
    {
        super(parent, modal);
        initComponents(initialMessage, initialUsername, loginDialog);
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
        char[] rgchPassword = passwordField.getPassword();
        return new String(rgchPassword);
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
    private void initComponents(String initialMessage, String initialUsername, LoginDialog loginDialog)
    {
    	this.loginDialog = loginDialog;
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

        userLabel = new javax.swing.JLabel();
        userLabel.setText("Email address");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        getContentPane().add(userLabel, gridBagConstraints);

        userField = new javax.swing.JTextField(20);
        userField.setText(initialUsername);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        getContentPane().add(userField, gridBagConstraints);

        passwordLabel = new javax.swing.JLabel();
        passwordLabel.setText("Password");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        getContentPane().add(passwordLabel, gridBagConstraints);
        
        passwordField = new javax.swing.JPasswordField(20);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        getContentPane().add(passwordField, gridBagConstraints);

        rePasswordLabel = new javax.swing.JLabel();
        rePasswordLabel.setText("Confirm password");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        getContentPane().add(rePasswordLabel, gridBagConstraints);
        
        rePasswordField = new javax.swing.JPasswordField(20);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        getContentPane().add(rePasswordField, gridBagConstraints);

        saveInfoCheckbox = new javax.swing.JCheckBox();
        saveInfoCheckbox.setText("Remember me?");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        getContentPane().add(saveInfoCheckbox, gridBagConstraints);

        loginButton = new javax.swing.JButton();
        loginButton.setText("Submit");
        loginButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                loginButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        getContentPane().add(loginButton, gridBagConstraints);
        getRootPane().setDefaultButton(loginButton);
        
        cancelButton = new javax.swing.JButton();
        cancelButton.setText("Cancel");
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
        Frame frame = ScreenUtil.getFrame(this);
        frame.remove(this);
        if (frame != null)
            ScreenUtil.centerDialogInFrame(loginDialog, frame);
        loginDialog.setVisible(true);
        doClose(JOptionPane.CANCEL_OPTION);
    }//GEN-LAST:event_cancelButtonActionPerformed
    
    private void loginButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
        char[] rgchPassword = passwordField.getPassword();
        char[] rgchRePassword = rePasswordField.getPassword();
        if (new String(rgchPassword).length() > 0)
        	if (!new String(rgchPassword).equalsIgnoreCase(new String(rgchRePassword)))
        	{
        		userMessage.setText("Passwords do not match - try again");
        		return;		// Try again
        	}
        loginDialog.setPassword(new String(rgchPassword));
        loginDialog.setUserName(userField.getText());
        loginDialog.setSaveInfo(saveInfoCheckbox.isSelected());
        returnStatus = LoginDialog.NEW_USER_OPTION;
        
        doClose(LoginDialog.NEW_USER_OPTION);
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
    private javax.swing.JLabel userMessage;
    private javax.swing.JLabel userLabel;
    private javax.swing.JLabel passwordLabel;
    private javax.swing.JLabel rePasswordLabel;
    private javax.swing.JCheckBox saveInfoCheckbox;
    private javax.swing.JTextField userField;
    private javax.swing.JPasswordField passwordField;
    private javax.swing.JPasswordField rePasswordField;
    // End of variables declaration//GEN-END:variables
    LoginDialog loginDialog = null;
    
    private int returnStatus = JOptionPane.OK_OPTION;
    
}
