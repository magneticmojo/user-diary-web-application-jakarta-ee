package usermanagement.verification.email;

import jakarta.mail.MessagingException;
import jakarta.mail.Transport;
import jakarta.servlet.ServletException;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Encapsulates the logic for processing and sending an email.
 * It reads the email configuration properties from a file, creates an EmailMessage,
 * and sends it using the Transport class from the Jakarta Mail API.
 * <p>
 * This class handles any MessagingException that may occur during the process
 * and rethrows them as ServletExceptions, to be consistent with the servlet framework's error handling.
 *
 * @author Björn Forsberg
 *
 * @see EmailMessage
 * @see EmailConfiguration
 */
public class EmailService {

    /**
     * Processes an email by reading the email configuration properties from a file, creating an EmailMessage, and sending it.
     * @param from the email address of the sender
     * @param to the email address of the recipient
     * @param cc the email address of the CC recipient
     * @param bcc the email address of the BCC recipient
     * @param subject the subject of the email
     * @param messageContent the content of the email
     * @throws ServletException if a servlet-specific error occurs during the email processing
     */
    public void processEmail(String from, String to, String cc, String bcc, String subject, String messageContent) throws ServletException {
        Properties configProperties = getEmailConfigProperties();
        EmailMessage emailMessage = getEmailMessage(from, to, cc, bcc, subject, messageContent, configProperties);
        sendEmailMessage(emailMessage);
    }

    /**
     * Sends an email message using the Transport class from the JavaMail API.
     * @param emailMessage the email message to send
     * @throws ServletException if a servlet-specific error occurs during the email sending
     */
    private void sendEmailMessage(EmailMessage emailMessage) throws ServletException {
        try {
            Transport.send(emailMessage.getMessage());
        } catch (MessagingException e) {
            throw new ServletException("Error sending email.", e);
        }
    }

    /**
     * Creates an EmailMessage.
     *
     * @param from               the email address of the sender
     * @param to                 the email address of the primary recipient
     * @param cc                 the email address of the carbon copy recipient
     * @param bcc                the email address of the blind carbon copy recipient
     * @param subject            the subject of the email
     * @param messageContent     the content of the email
     * @param configProperties   the configuration properties
     * @return                   the created EmailMessage
     * @throws ServletException if there is an error while creating the EmailMessage
     */
    private EmailMessage getEmailMessage(String from, String to, String cc, String bcc, String subject, String messageContent, Properties configProperties) throws ServletException {
        EmailConfiguration emailConfig = new EmailConfiguration(configProperties);
        EmailMessage emailMessage;
        try {
            emailMessage = new EmailMessage(emailConfig.configureEmailSession(), from, to, cc, bcc, subject, messageContent);
        } catch (MessagingException e) {
            throw new ServletException("Error creating email message.", e);
        }
        return emailMessage;
    }

    /**
     * Loads the email configuration properties from a file.
     * @return the email configuration properties
     * @throws ServletException if there is an error while reading the email configuration properties
     */
    private Properties getEmailConfigProperties() throws ServletException {
        Properties configProperties = new Properties();
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("config.properties")) {
            configProperties.load(input);
        } catch (IOException e) {
            throw new ServletException("Error loading email configuration.", e);
        }
        return configProperties;
    }
}
