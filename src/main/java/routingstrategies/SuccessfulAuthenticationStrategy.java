package routingstrategies;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

/**
 * Routing strategy for successful user authentication.
 * Redirects the authenticated user to a specified URI, configuring necessary session attributes.
 *
 * @author Björn Forsberg
 */
public class SuccessfulAuthenticationStrategy implements RoutingStrategy {

    private final String uri;
    private final String username;

    /**
     * Constructs a new SuccessfulAuthenticationStrategy with the specified URI and username.
     *
     * @param uri      The URI to redirect to after successful authentication.
     * @param username The authenticated username to be stored in the session attributes.
     */
    public SuccessfulAuthenticationStrategy(String uri, String username) {
        this.uri = uri;
        this.username = username;
    }

    /**
     * Handles the routing logic for successful user authentication.
     * Configures the necessary session attributes and redirects to the specified URI.
     *
     * @param request      The HttpServletRequest object containing the client's request.
     * @param response     The HttpServletResponse object containing the server's response.
     * @param contextPath  The context path of the web application.
     * @throws IOException       if an I/O error occurs during.
     * @throws ServletException  if a servlet exception occurs.
     */
    @Override
    public void handleRouting(HttpServletRequest request, HttpServletResponse response, String contextPath) throws IOException, ServletException {
        configureSessionAttributes(request);
        response.sendRedirect(contextPath + uri);
    }

    /**
     * Configures the session attributes needed for a successful user authentication.
     * Removes any existing user message attribute and sets the username and loggedIn attributes.
     *
     * @param request The HttpServletRequest object containing the client's request.
     */
    private void configureSessionAttributes(HttpServletRequest request) {
        HttpSession session = request.getSession();
        session.removeAttribute("userMessage");
        session.setAttribute("username", username);
        session.setAttribute("loggedIn", "loggedIn");
    }
}
