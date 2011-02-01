/*
 * msgmultisendsample.java
 *
 * Created on October 22, 2001, 11:45 PM
 */

package org.jbundle.base.message.trx.transport.email.test;

import java.util.Date;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.jbundle.base.field.PasswordPropertiesField;
import org.jbundle.base.message.trx.message.internal.ManualMessage;
import org.jbundle.base.util.DBConstants;
import org.jbundle.main.msg.db.MessageInfoType;
import org.jbundle.main.msg.db.MessageStatus;
import org.jbundle.main.msg.db.MessageType;
/**
 * msgmultisendsample creates a simple multipart/mixed message and sends
 * it. Both body parts are text/plain.
 *
 * usage:java msgmultisendsample to from smtp true|false
 * where to and from are the destination and
 * origin email addresses, respectively, and smtp
 * is the hostname of the machine that has smtp server
 * running. The last parameter either turns on or turns off
 * debugging during sending.
 */

public class msgmultisendsample
{
    static String NEW_LINE = "\n";
    static String msgText1 = "This is a message body.\nHere s line two.";
    static String msgText2 = "This is the text in the message attachment.";
    
    static String msgHtml = "<html>" + NEW_LINE +
        "<head>" + NEW_LINE +
        "</head>" + NEW_LINE +
        "<body>" + NEW_LINE +
        "This is a <b>test</b>!" + NEW_LINE +
        "</body>" + NEW_LINE +
        "</html>";

    public static void main(String[] args)
    {
        if (args.length != 4)
        {
            System.out.println("usage: java msgmultisend <to> <from> <smtp> true|false");
            return;
        }
        String to = args[0];
        String from = args[1];
        String host = args[2];
        boolean debug = Boolean.valueOf(args[3]).booleanValue();
        
        // create some properties and get the default Session
        Properties props = new Properties();
        props.put("mail.smtp.host", host);
        
        props.put("smtp.username", "don");
//        props.put("mail.smtp.auth", "donwpp");;
  //      props.put("mail.smtp.starttls.enable", "donwpp");;
    //    props.put("sendTransport", "donwpp");
        String username = "don";
        props.put("smtp.username", "don");
        String password = "donwpp";
        props.put("smtp.password", "donwpp");

        
        Session session = Session.getDefaultInstance(props, null);
        session.setDebug(debug);
        
        try {
            // create a message
            MimeMessage msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress(from));
            InternetAddress[] address = {new InternetAddress(to)};
            msg.setRecipients(Message.RecipientType.TO, address);
            msg.setSubject("JavaMail APIs Multipart Test");
            msg.setSentDate(new Date());
            
            // create and fill the first message part
            MimeBodyPart mbp1 = new MimeBodyPart();
            mbp1.setText(msgText1);
            
            // create and fill the second message part
            MimeBodyPart mbp2 = new MimeBodyPart();
            
            // Use setText(text, charset), to show it off !
//            mbp2.setText(msgText2, "us-ascii");
            mbp2.setContent(msgHtml, "text/html");
            
            // create the Multipart and its parts to it
            
            Multipart mp = new MimeMultipart();
            mp.addBodyPart(mbp1);
            mp.addBodyPart(mbp2);
            
            // add the Multipart to the message
            msg.setContent(mp);
//            msg.setContent(msgHtml, "text/html");
            
            // send the message
//?            Transport.send(msg);
            
            
            
            Transport transport = null;
          

                msg.saveChanges();

                transport = session.getTransport("smtp");
                //         if (DBConstants.TRUE.equalsIgnoreCase(strAuth))
                transport.connect(host, username, password);
                // send the message
                transport.sendMessage(msg, msg.getAllRecipients());

            
            
        } catch (MessagingException mex) {
            mex.printStackTrace();
            Exception ex = null;
            if ((ex = mex.getNextException()) != null)
            {
                ex.printStackTrace();
            }
        }
    }
}
