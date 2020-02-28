package me.winterroberts.ajax.email;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.json.JSONException;
import org.json.JSONObject;

import net.aionstudios.api.aos.AOSInfo;
import net.aionstudios.api.service.ResponseServices;

public class EmailUtils {
	
	private String fromEmail;
	private String fromName;
	private String fromPassword;
	private String smtpHost;
	private String smtpSslPort;
	
	private static EmailUtils self;
	
	private boolean available;
	public static final String CONFIG_EMAILS = "./config/email.json";
	private static JSONObject emailConfig;
	
	private EmailUtils() {
		emailConfig = ResponseServices.getLinkedJsonObject();
		available = false;
		File ce = new File(CONFIG_EMAILS);
		if(!ce.exists()) {
			ce.getParentFile().mkdirs();
			try {
				ce.createNewFile();
				emailConfig.put("from_email", "johndoe@example.com");
				emailConfig.put("from_name", "John Doe");
				emailConfig.put("password", "password");
				emailConfig.put("smtp_host", "smtp.example.com");
				emailConfig.put("smtp_ssl_host", "465");
				AOSInfo.writeConfig(emailConfig, ce);
			} catch (IOException e) {
				e.printStackTrace();
			} catch (JSONException e) {
				e.printStackTrace();
			}
		} else {
			try {
				emailConfig = AOSInfo.readConfig(ce);
				fromEmail = emailConfig.getString("from_email");
				fromName = emailConfig.getString("from_name");
				fromPassword = emailConfig.getString("password");
				smtpHost = emailConfig.getString("smtp_host");
				smtpSslPort = emailConfig.getString("smtp_ssl_host");
				if(!fromEmail.equals("johndoe@example.com")) {
					available = true;
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if(!available) {
			System.err.println("Email service not available. please change configuration.");
		}
	}
	
	public void sendEmail(String subject, String body, String toEmail, String replyEmail) {
		if(!available) {
			System.err.println("Could not send email! Service not available.");
		}
		Properties props = new Properties();
		props.put("mail.smtp.host", smtpHost); //SMTP Host
		props.put("mail.smtp.socketFactory.port", smtpSslPort); //SSL Port
		props.put("mail.smtp.socketFactory.class",
				"javax.net.ssl.SSLSocketFactory"); //SSL Factory Class
		props.put("mail.smtp.auth", "true"); //Enabling SMTP Authentication
		props.put("mail.smtp.port", smtpSslPort); //SMTP Port
		
		Authenticator auth = new Authenticator() {
			//override the getPasswordAuthentication method
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(fromEmail, fromPassword);
			}
		};
		
		Session session = Session.getInstance(props, auth);
		sendEmail(session, replyEmail, toEmail, subject, body);
	}
	
	/**
	 * Utility method to send simple HTML email
	 * @param session
	 * @param toEmail
	 * @param subject
	 * @param body
	 */
	private void sendEmail(Session session, String replyEmail, String toEmail, String subject, String body){
		try
	    {
	      MimeMessage msg = new MimeMessage(session);
	      //set message headers
	      msg.addHeader("Content-type", "text/html; charset=utf-8");
	      msg.addHeader("format", "flowed");
	      msg.addHeader("Content-Transfer-Encoding", "8bit");

	      msg.setFrom(new InternetAddress(fromEmail, fromName));

	      msg.setReplyTo(InternetAddress.parse(replyEmail, false));

	      msg.setSubject(subject, "utf-8");

	      msg.setText(body, "utf-8", "html");

	      msg.setSentDate(new Date());

	      msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail, false));
    	  Transport.send(msg);  
	    }
	    catch (Exception e) {
	      e.printStackTrace();
	    }
	}
	
	public static EmailUtils getInstance() {
		if(self==null) {
			self=new EmailUtils();
		}
		return self;
	}

}
