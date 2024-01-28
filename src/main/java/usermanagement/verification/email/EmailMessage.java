package usermanagement.verification.email;

import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

/**
 * The EmailMessage class is responsible for creating an email message using the Jakarta Mail API.
 * It uses provided parameters such as sender, recipient(s), subject, and message content to
 * construct a new instance of MimeMessage.
 *
 * @author Björn Forsberg
 */
public class EmailMessage {

    private final MimeMessage message;

    /**
     * Constructs a new EmailMessage object.
     *
     * @param session        the email session
     * @param from           the sender's email address
     * @param to             the recipient's email address
     * @param cc             the CC recipient's email address
     * @param bcc            the BCC recipient's email address
     * @param subject        the email subject
     * @param messageContent the email message content
     * @throws MessagingException if an error occurs when setting any fields of the message
     */
    public EmailMessage(Session session, String from, String to, String cc, String bcc, String subject, String messageContent) throws MessagingException {
        this.message = new MimeMessage(session);
        message.setFrom(new InternetAddress(from));
        message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
        if (cc != null && !cc.isEmpty()) {
            message.addRecipient(Message.RecipientType.CC, new InternetAddress(cc));
        }
        if (bcc != null && !bcc.isEmpty()) {
            message.addRecipient(Message.RecipientType.BCC, new InternetAddress(bcc));
        }
        message.setSubject(subject);
        message.setText(messageContent);
    }

    /**
     * Returns the MimeMessage instance associated with this EmailMessage.
     *
     * @return the MimeMessage instance
     */
    public MimeMessage getMessage() {
        return this.message;
    }
}

