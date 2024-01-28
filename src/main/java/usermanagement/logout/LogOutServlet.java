package usermanagement.logout;

import constants.ServletURIs;
import constants.UserInteractionMessages;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import routingstrategies.RedirectWithRequestParamsStrategy;
import routingstrategies.RedirectWithUserMessageStrategy;

import java.io.IOException;

/**
 * Responsible for handling user logout operations, either for an active user or for a deletion process.
 * When a user logs out, this servlet invalidates the session and redirects to the login page with a logout success message
 * or a deletion message if the user has deleted its account. The routing is handled by the {@link RedirectWithRequestParamsStrategy}
 * class which passes the message as a request parameter.
 *
 * @author Björn Forsberg
 */
@WebServlet(name = "LogOutServlet", urlPatterns = ServletURIs.LOG_OUT)
public class LogOutServlet extends HttpServlet {

    /**
     * Handles the POST request to log out a user or to log out a user that has deleted its account.
     * The method invalidates the user's session and redirects to the login page with a deletion or log out message.
     *
     * @param request  The HttpServletRequest object containing client request information.
     * @param response The HttpServletResponse object to send the response back to the client.
     * @throws IOException      If an input or output error occurs during the handling of the request.
     * @throws ServletException If the request could not be handled.
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        HttpSession session = request.getSession(false);
        RedirectWithRequestParamsStrategy strategy;

        if (session.getAttribute("deletedAccount") != null) {
            strategy = new RedirectWithRequestParamsStrategy(ServletURIs.LOGIN, "logoutMessage", UserInteractionMessages.ACCOUNT_DELETION_SUCCESSFUL);
        } else {
            strategy = new RedirectWithRequestParamsStrategy(ServletURIs.LOGIN, "logoutMessage", UserInteractionMessages.LOGOUT_SUCCESSFUL);
        }

        session.invalidate();
        strategy.handleRouting(request, response, getServletContext().getContextPath());
    }
}

