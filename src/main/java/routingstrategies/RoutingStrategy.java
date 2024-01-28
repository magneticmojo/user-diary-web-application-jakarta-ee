package routingstrategies;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * Interface representing a routing strategy within the web application.
 * Provides a single method to handle the routing logic based on specific conditions or use cases.
 * Implementing classes define the concrete routing behavior.
 *
 * @author Björn Forsberg
 */
public interface RoutingStrategy {

    /**
     * Handles the routing logic for a specific use case or condition.
     *
     * @param request      The HttpServletRequest object containing the client's request.
     * @param response     The HttpServletResponse object containing the server's response.
     * @param contextPath  The context path of the web application.
     * @throws IOException       if an I/O error occurs during routing.
     * @throws ServletException  if a servlet exception occurs during routing.
     */
    void handleRouting(HttpServletRequest request, HttpServletResponse response, String contextPath) throws IOException, ServletException;
}
