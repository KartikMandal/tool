package com.kartik.tools.cobranding;

import java.util.Properties;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

public class SendInlineImagesInEmails {
	
	public void sendMail(String data){
		 // Recipient's email ID needs to be mentioned.
	      String to = "kmandal@gmail.com";
	     // String to = "kartik.cse43@gmail.com";
	      // Sender's email ID needs to be mentioned
	      String from = "kmandal@yoahoo.com";
	      final String username = "kmandal";//change accordingly
	      final String password = "XXXXXXXXX@1234";//change accordingly

	      // Assuming you are sending email through relay.jangosmtp.net
	      String host = "XXXXXX"; //plz mention what host u need

	      Properties props = new Properties();
	      props.put("mail.smtp.auth", "true");
	      props.put("mail.smtp.starttls.enable", "true");
	      props.put("mail.smtp.host", host);
	      props.put("mail.smtp.port", "25");

	      Session session = Session.getInstance(props,
	         new javax.mail.Authenticator() {
	            protected PasswordAuthentication getPasswordAuthentication() {
	               return new PasswordAuthentication(username, password);
	            }
	         });
		
		try {

	         // Create a default MimeMessage object.
	         Message message = new MimeMessage(session);

	         // Set From: header field of the header.
	         message.setFrom(new InternetAddress(from));

	         // Set To: header field of the header.
	         message.setRecipients(Message.RecipientType.TO,
	            InternetAddress.parse(to));

	         // Set Subject: header field
	         message.setSubject("Weekly financial snapshot");

	         // This mail has 2 part, the BODY and the embedded image
	       //  MimeMultipart multipart = new MimeMultipart("related");

	         // first part (the html)
	        // BodyPart messageBodyPart = new MimeBodyPart();
	        
	         //String htmlText = data;
	         message.setContent(data, "text/html");
	         //messageBodyPart.setContent(htmlText, "text/HTML");
	         // add it
	         //multipart.addBodyPart(messageBodyPart);

	         // second part (the image)
	         //messageBodyPart = new MimeBodyPart();
	         /*DataSource fds = new FileDataSource("D:/PieChart.png");
	         
	         messageBodyPart.setDataHandler(new DataHandler(fds));
	         messageBodyPart.setHeader("Content-ID", "<image1>");*/

	         // add image to the multipart
	        // multipart.addBodyPart(messageBodyPart);
	         
	         
	         
	         
	         // second part (the image)
	        /* messageBodyPart = new MimeBodyPart();
	         DataSource fds1 = new FileDataSource(
	            "D:/RingChart.png");

	         messageBodyPart.setDataHandler(new DataHandler(fds1));
	         messageBodyPart.setHeader("Content-ID", "<image>");

	         // add image to the multipart
	         multipart.addBodyPart(messageBodyPart);
	         */

	         // put everything together
	         //message.setContent(multipart);
	         // Send message
	         Transport.send(message);

	         System.out.println("Sent message successfully....");

	      } catch (Exception e) {
	    	  e.printStackTrace();
	         throw new RuntimeException(e);
	      }
	}
	
   public static void main(String[] args) {
     
   }
}
