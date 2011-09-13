/*
 * PrintDialog.java
 *
 * Created on August 22, 2005, 3:32 AM

 * Copyright © 2011 jbundle.org. All rights reserved.
 */
package org.jbundle.thin.base.screen.comp;

import javax.swing.JOptionPane;

/**
 *
 * @author  don
 */
public class ChangePasswordDialog extends javax.swing.JDialog
{

	private static final long serialVersionUID = 1L;

    /**
     * Creates new form PrintDialog
     */
    public ChangePasswordDialog(java.awt.Frame parent, boolean modal, String initialMessage, String initialUsername)
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
     * Get the user name.
     */
    public String getUserName()
    {
        return userField.getText();
    }
    /**
     * Get the current password.
     */
    public String getCurrentPassword()
    {
        char[] rgchPassword = currentPasswordField.getPassword();
        return new String(rgchPassword);
    }    
    /**
     * Compare the new and confirm password and return the new password.
     */
    public String getNewPassword()
    {
        char[] rgchNewPassword = newPasswordField.getPassword();
        char[] rgchConfirmPassword = confirmPasswordField.getPassword();
        String strNewPassword = new String(rgchNewPassword);
        String strConfirmPassword = new String(rgchConfirmPassword);
        if (strNewPassword != null)
            if (!strNewPassword.equals(strConfirmPassword))
                return null;
        return strNewPassword;
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

        currentPasswordLabel = new javax.swing.JLabel();
        currentPasswordLabel.setText("Current password");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        getContentPane().add(currentPasswordLabel, gridBagConstraints);

        currentPasswordField = new javax.swing.JPasswordField(20);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        getContentPane().add(currentPasswordField, gridBagConstraints);

        newPasswordLabel = new javax.swing.JLabel();
        newPasswordLabel.setText("New password");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        getContentPane().add(newPasswordLabel, gridBagConstraints);

        newPasswordField = new javax.swing.JPasswordField(20);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        getContentPane().add(newPasswordField, gridBagConstraints);

        confirmPasswordLabel = new javax.swing.JLabel();
        confirmPasswordLabel.setText("Confirm password");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        getContentPane().add(confirmPasswordLabel, gridBagConstraints);

        confirmPasswordField = new javax.swing.JPasswordField(20);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        getContentPane().add(confirmPasswordField, gridBagConstraints);

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

        loginButton = new javax.swing.JButton();
        loginButton.setText("Change password");
        loginButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                loginButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        getContentPane().add(loginButton, gridBagConstraints);


        pack();
    }
    // </editor-fold>//GEN-END:initComponents
        
    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
        doClose(JOptionPane.CANCEL_OPTION);
    }//GEN-LAST:event_cancelButtonActionPerformed
    
    private void loginButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
        doClose(JOptionPane.OK_OPTION);
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
    private javax.swing.JLabel newPasswordLabel;
    private javax.swing.JLabel currentPasswordLabel;
    private javax.swing.JLabel confirmPasswordLabel;
    private javax.swing.JTextField userField;
    private javax.swing.JPasswordField currentPasswordField;
    private javax.swing.JPasswordField newPasswordField;
    private javax.swing.JPasswordField confirmPasswordField;
    // End of variables declaration//GEN-END:variables
    
    private int returnStatus = JOptionPane.OK_OPTION;
    
}
