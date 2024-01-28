package routingstrategies;

import constants.UserInteractionMessages;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

/**
 * Routing strategy that redirects the request to a specified URI, setting user interaction messages in the session attributes.
 * This strategy will redirect the request to the given URI and set a specific user interaction message
 * in the session attributes. It can be used to provide feedback messages to the user, such as validation,
 * login, registration, or verification messages.
 *
 * @author Björn Forsberg
 */
public class RedirectWithUserMessageStrategy implements RoutingStrategy {

    private final String uri;
    private final String userMessage;

    /**
     * Constructs a new RedirectWithUserMessageStrategy with the specified URI and user interaction message.
     *
     * @param uri          The URI to redirect to.
     * @param userMessage  The user interaction message to be set in the session attributes.
     */
    public RedirectWithUserMessageStrategy(String uri, String userMessage) {
        this.uri = uri;
        this.userMessage = userMessage;
    }

    /**
     * Handles the routing by redirecting to the specified URI, setting the appropriate user interaction message in the session attributes.
     *
     * @param request      The HttpServletRequest object containing the client's request.
     * @param response     The HttpServletResponse object containing the server's response.
     * @param contextPath  The context path of the web application.
     * @throws IOException if an I/O error occurs.
     */
    @Override
    public void handleRouting(HttpServletRequest request, HttpServletResponse response, String contextPath) throws IOException {
        setUserMessageSessionAttribute(request);
        response.sendRedirect(contextPath + uri);
    }

    /**
     * Sets the appropriate user interaction message as a session attribute based on the predefined categories (validation, login, registration, verification).
     *
     * @param request The HttpServletRequest object containing the client's request.
     */
    private void setUserMessageSessionAttribute(HttpServletRequest request) {
        HttpSession session = request.getSession();

        if (UserInteractionMessages.VALIDATION_MESSAGES.contains(userMessage)) {
            session.setAttribute("validationUserMessage", userMessage);
        }

        if (UserInteractionMessages.LOGIN_MESSAGES.contains(userMessage)) {
            session.setAttribute("loginUserMessage", userMessage);
        }

        if (UserInteractionMessages.REGISTRATION_MESSAGES.contains(userMessage)) {
            session.setAttribute("registrationUserMessage", userMessage);
        }

        if (UserInteractionMessages.VERIFICATION_MESSAGES.contains(userMessage)) {
            session.setAttribute("verificationUserMessage", userMessage);
        }
    }
}
