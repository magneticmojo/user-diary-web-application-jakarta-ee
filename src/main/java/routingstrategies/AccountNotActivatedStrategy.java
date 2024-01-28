package routingstrategies;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * Routing strategy for handling accounts that are not activated.
 * Forwards the request to a specified URI, setting the necessary session attributes.
 *
 * @author Björn Forsberg
 */
public class AccountNotActivatedStrategy implements RoutingStrategy {

    private final String uri;
    private final String username;

    /**
     * Constructs a new AccountNotActivatedStrategy with the specified URI and username.
     *
     * @param uri      The URI to forward to if the account is not activated.
     * @param username The username to be stored in the session attributes.
     */
    public AccountNotActivatedStrategy(String uri, String username) {
        this.uri = uri;
        this.username = username;
    }

    /**
     * Handles the routing logic for accounts that are not activated.
     * Sets the email session attribute in helper method and forwards the request to the specified URI.
     *
     * @param request      The HttpServletRequest object containing the client's request.
     * @param response     The HttpServletResponse object containing the server's response.
     * @param contextPath  The context path of the web application.
     * @throws IOException       if an I/O error occurs.
     * @throws ServletException  if a servlet exception.
     */
    @Override
    public void handleRouting(HttpServletRequest request, HttpServletResponse response, String contextPath) throws IOException, ServletException {
        setEmailSessionAttribute(request, username);
        request.getRequestDispatcher(uri).forward(request, response);
    }

    /**
     * Sets the email session attribute for the given request.
     *
     * @param request The HttpServletRequest object containing the client's request.
     * @param email   The email address to be stored in the session attributes.
     */
    private void setEmailSessionAttribute(HttpServletRequest request, String email) {
        request.getSession().setAttribute("email", email);
    }

}
