package routingstrategies;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * Routing strategy that implements the Post-Redirect-Get pattern.
 * Redirects the request to the original request URI.
 *
 * @author Björn Forsberg
 */
public class PostRedirectGetRoutingStrategy implements RoutingStrategy{

    /**
     * Handles the routing logic by redirecting the request to the original request URI.
     * This implementation follows the Post-Redirect-Get pattern.
     *
     * @param request      The HttpServletRequest object containing the client's request.
     * @param response     The HttpServletResponse object containing the server's response.
     * @param contextPath  The context path of the web application. This parameter is not used in this implementation.
     * @throws IOException if an I/O error occurs during redirection.
     */
    @Override
    public void handleRouting(HttpServletRequest request, HttpServletResponse response, String contextPath) throws IOException {
        response.sendRedirect(request.getRequestURI());
    }
}
