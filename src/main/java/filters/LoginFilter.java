package filters;

import constants.ServletURIs;
import constants.UserInteractionMessages;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import routingstrategies.RedirectWithRequestParamsStrategy;
import services.DiaryPostService;

import java.io.IOException;

/**
 * Web filter that checks if a user is logged in before accessing certain protected resources.
 * <p>
 * The filter is applied to URL patterns specified in the {@link ServletURIs#USER_PATTERN} constant.
 * If the user is logged in (indicated by the "loggedIn" session attribute), the request continues to the targeted resource.
 * If the user is not logged in, they are redirected to the login page that displays a prompt to log in. The redirection uses
 * the {@link RedirectWithRequestParamsStrategy} class which passes the user message as a request parameter to avoid using session attributes.
 *
 * @author Björn Forsberg
 */
@WebFilter(urlPatterns = {
        ServletURIs.USER_PATTERN,
})
public class LoginFilter implements Filter {

    /**
     * Checks if the user is logged in by examining the "loggedIn" session attribute.
     * If the user is logged in, the request continues along the filter chain.
     * If the user is not logged in, redirection to the login page is executed.
     *
     * @param req   The ServletRequest object contains the client's request.
     * @param res   The ServletResponse object contains the filter's response.
     * @param chain The FilterChain for invoking the next filter or the resource.
     * @throws IOException      if an I/O error occurs during this filter's processing of the request.
     * @throws ServletException if the processing fails for any other reason.
     */
    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        HttpSession session = request.getSession(false);

        if (session.getAttribute("loggedIn") != null) {
            chain.doFilter(req, res);
        } else {
            RedirectWithRequestParamsStrategy strategy = new RedirectWithRequestParamsStrategy(ServletURIs.LOGIN, "logoutMessage", UserInteractionMessages.USER_NOT_LOGGED_IN);
            strategy.handleRouting(request, response, request.getContextPath());
        }
    }
}
