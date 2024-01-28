package routingstrategies;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * Routing strategy that redirects the request to a specified URI with additional request parameters.
 *
 * @author Björn Forsberg
 */
public class RedirectWithRequestParamsStrategy implements RoutingStrategy {

    private final String uri;

    private final String paramName;

    private final String paramValue;

    /**
     * Initializes a RedirectWithRequestParamsStrategy with the specified URI and request parameters.
     *
     * @param uri        The URI to redirect to.
     * @param paramName  The name of the request parameter to be included in the redirection URL.
     * @param paramValue The value of the request parameter to be included in the redirection URL.
     */
    public RedirectWithRequestParamsStrategy(String uri, String paramName, String paramValue) {
        this.uri = uri;
        this.paramName = paramName;
        this.paramValue = paramValue;
    }

    /**
     * Handles the routing by redirecting to the specified URI with additional request parameters.
     *
     * @param request      The HttpServletRequest object containing the client's request.
     * @param response     The HttpServletResponse object containing the server's response.
     * @param contextPath  The context path of the web application.
     * @throws IOException if an I/O error occurs during redirection.
     * @throws ServletException if an exception occurs during the handling of the servlet request.
     */
    @Override
    public void handleRouting(HttpServletRequest request, HttpServletResponse response, String contextPath) throws IOException, ServletException {
        response.sendRedirect(concatenateURL(contextPath, uri, paramName, paramValue));
    }

    /**
     * Concatenates the URL with the context path, URI, and request parameters.
     *
     * @param contextPath     The context path of the web application.
     * @param uri             The URI to redirect to.
     * @param paramName       The name of the request parameter.
     * @param paramValue      The value of the request parameter.
     * @return                The concatenated URL.
     */
    private String concatenateURL(String contextPath, String uri, String paramName, String paramValue) {
        return contextPath + uri + "?" + generateRequestParameter(paramName, paramValue);
    }

    /**
     * Generates the request parameter in the format of "name=value".
     *
     * @param parameterName   The name of the request parameter.
     * @param parameterValue  The value of the request parameter.
     * @return                The generated request parameter string.
     */
    private String generateRequestParameter(String parameterName, String parameterValue) {
        return parameterName + "=" + parameterValue;
    }
}
