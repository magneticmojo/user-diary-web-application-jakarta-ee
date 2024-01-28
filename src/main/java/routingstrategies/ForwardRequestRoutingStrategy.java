package routingstrategies;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * Routing strategy that forwards the request to a specified URI.
 *
 * @author Björn Forsberg
 */
public class ForwardRequestRoutingStrategy implements RoutingStrategy {

    private final String uri;

    /**
     * Constructs a new ForwardRequestRoutingStrategy with the specified URI to forward to.
     *
     * @param uri The URI to which the request will be forwarded.
     */
    public ForwardRequestRoutingStrategy(String uri) {
        this.uri = uri;
    }

    /**
     * Handles the routing logic by forwarding the request to the specified URI.
     *
     * @param request      The HttpServletRequest object containing the client's request.
     * @param response     The HttpServletResponse object containing the server's response.
     * @param contextPath  The context path of the web application.
     * @throws IOException       if an I/O error occurs during.
     * @throws ServletException  if a servlet exception occurs.
     */
    @Override
    public void handleRouting(HttpServletRequest request, HttpServletResponse response, String contextPath) throws IOException, ServletException {
        request.getRequestDispatcher(uri).forward(request, response);
    }
}
