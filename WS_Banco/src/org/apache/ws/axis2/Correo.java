package org.apache.ws.axis2;

import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class Correo {
	// https://myaccount.google.com/lesssecureapps?pli=1
	private static String usuario = "proyectoast2017@gmail.com";
	private static String contra = "pppppppp";

	public void enviar(String destinatario, String contenido) {
		Properties props = new Properties();
		props.put("mail.smtp.host", "smtp.gmail.com");
		props.put("mail.smtp.socketFactory.port", "465");
		props.put("mail.smtp.socketFactory.class","javax.net.ssl.SSLSocketFactory");
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.port", "465");

		Session session = Session.getDefaultInstance(props,new javax.mail.Authenticator() {
				protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(usuario,contra);
				}
			});
		System.out.println("[Clase Correo] Sesion creada.");

		try {
			Message message = new MimeMessage(session);
			message.setRecipients(Message.RecipientType.TO,InternetAddress.parse(destinatario));
			message.setSubject("Informacion de pago");
			message.setText(contenido);
			System.out.println("[Clase Correo] Mensaje creado.");

			Transport.send(message);
			System.out.println("[Clase Correo] Mensaje enviado.");
		} catch (MessagingException e) {
			throw new RuntimeException(e);
		}
	}
}
