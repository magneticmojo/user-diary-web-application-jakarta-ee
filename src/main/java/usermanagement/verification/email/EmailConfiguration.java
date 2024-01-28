package usermanagement.verification.email;

import jakarta.mail.Authenticator;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;

import java.util.Properties;

/**
 * Encapsulates the configuration of an email session. It uses properties
 * read from a provided Properties object to create and configure an instance of the Session class.
 * The session is configured to use Gmail's SMTP server with the necessary settings.
 *
 * @author Björn Forsberg
 */
public class EmailConfiguration {

    private final Properties configProperties;

    /**
     * Constructs a new EmailConfiguration object.
     *
     * @param configProperties the properties for configuring the email session
     */
    public EmailConfiguration(Properties configProperties) {
        this.configProperties = configProperties;
    }

    /**
     * Configures and returns an email session.
     *
     * @return the configured email session
     */
    public Session configureEmailSession() {
        String emailAddress = configProperties.getProperty("email.address");
        String emailPassword = configProperties.getProperty("email.password");

        Properties properties = getProperties();
        return Session.getDefaultInstance(properties, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(emailAddress, emailPassword);
            }
        });
    }

    /**
     * Gets the email properties required for the session configuration.
     *
     * @return the properties
     */
    private Properties getProperties() {
        Properties properties = new Properties();
        String host = "smtp.gmail.com";
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.host", host);
        properties.put("mail.smtp.port", "587");
        properties.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        return properties;
    }
}
