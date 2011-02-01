package org.jbundle.base.message.trx.transport.email.test;

import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class SmtpAuthTest
{
   public static void main(String[] args)
   {
      String host = "tourdemo.com";
      
      String username = "don";
      String password = "donwpp";

      String recipientEmailAddress = "don@tourgeek.com";
      
      Transport transport = null;
      
      try
      {
         final InternetAddress sender    = new InternetAddress(username);
         final InternetAddress recipient = new InternetAddress(recipientEmailAddress);
         
         Properties properties = new Properties();
         properties.put("mail.smtp.auth", "true");
         
         Session session = Session.getDefaultInstance(properties);
         session.setDebug(true);
         
         MimeMessage message = new MimeMessage(session);
         message.setSubject("test subject");
         message.setText("test body");
         message.setFrom(sender);
         message.addRecipient(Message.RecipientType.TO, recipient);
         message.saveChanges();
         
         transport = session.getTransport("smtp");
         transport.connect(host, sender.getAddress(), password);
         transport.sendMessage(message, message.getAllRecipients());
         transport.close();
      }
      catch (MessagingException e)
      {
         e.printStackTrace();
      }
      finally
      {
         try
         {
            transport.close();
         }
         catch (Exception e)
         {}
      }
   }
}
