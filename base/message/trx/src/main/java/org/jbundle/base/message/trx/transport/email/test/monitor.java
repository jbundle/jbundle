/*
 * monitor.java
 *
 * Created on October 23, 2001, 5:42 AM

 * Copyright © 2011 jbundle.org. All rights reserved.
 */
package org.jbundle.base.message.trx.transport.email.test;

import java.util.Properties;

import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Store;

public class monitor 
{
    public static void main(String argv[])
    { 
        if (argv.length != 5)
        {
            System.out.println("Usage: monitor <host> <user> <password> <mbox> <freq>");
            System.exit(1);
        }
        System.out.println("\nTesting monitor\n");
        
        try
        {
            Properties props = System.getProperties();
            
            // Get a Session object
            Session session = Session.getDefaultInstance(props, null);
            session.setDebug(true);
            
            // Get a Store object
//            Store store = session.getStore("imap");
            Store store = session.getStore("pop3");
            // Connect 
            store.connect(argv[0], argv[1], argv[2]);
            
            // Open a Folder
            Folder folder = store.getFolder(argv[3]);
            if (folder == null || !folder.exists()) 
            {
                System.out.println("Invalid folder");
                System.exit(1); 
            }
            folder.open(Folder.READ_WRITE);

            // Check mail once in "freq" MILLIseconds
//?            int freq = Integer.parseInt(argv[4]);
            int iCount = folder.getMessageCount();

            if (iCount > 0)
            {
                int msgnum = 0;
                Message[] messages = folder.getMessages();
                Message message = messages[msgnum];
                String strSubject = message.getSubject();
                System.out.println("Subject: " + strSubject);
                System.out.println("Content type: " + message.getContentType());
                System.out.println("content: " + message.getContent());
                
                folder.expunge();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
