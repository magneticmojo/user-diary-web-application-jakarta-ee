package usermanagement.userpage;

import constants.ServletURIs;
import database.dao.UserDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import routingstrategies.RoutingStrategy;
import services.UserManagementService;
import routingstrategies.ForwardRequestRoutingStrategy;
import viewbuilder.ViewBuilder;

import java.io.IOException;

/**
 * Responsible for handling user account deletion operations. A GET request renders the account deletion view.
 * A POST request handles the account deletion process by soft deleting the user account.
 * The deletion process is handled internally by the service class {@link UserManagementService}.
 * Routing is handled by the {@link RoutingStrategy} interface and its implementations.
 *
 * @author Björn Forsberg
 */
@WebServlet(name = "AccountDeletionServlet", urlPatterns = ServletURIs.DELETE_ACCOUNT)
public class AccountDeletionServlet extends HttpServlet {

    private static ViewBuilder viewBuilder = null;

    /**
     * Initializes this servlet with a viewBuilder if not already initialized.
     */
    @Override
    public void init() {
        if (viewBuilder == null) {
            viewBuilder = new ViewBuilder(getServletContext().getContextPath(), getServletContext().getRealPath("/"));
        }
    }

    /**
     * Handles the GET request for the account deletion page, rendering the account deletion view.
     *
     * @param request  The HttpServletRequest object containing client request information.
     * @param response The HttpServletResponse object to send the response back to the client.
     * @throws IOException If an input or output error occurs.
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        respondWithAccountDeletionView(response);
    }

    /**
     * Handles the POST request for user account deletion, processing the deletion and forwarding to log out.
     *
     * @param request  The HttpServletRequest object containing client request information.
     * @param response The HttpServletResponse object to send the response back to the client.
     * @throws ServletException If the request could not be handled.
     * @throws IOException      If an input or output error occurs.
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        processUserAccountDeletionRequest(request, response);
    }

    /**
     * Responds with the HTML view of the account deletion page.
     *
     * @param response The HttpServletResponse object to send the response back to the client.
     * @throws IOException If an input or output error occurs.
     */
    private void respondWithAccountDeletionView(HttpServletResponse response) throws IOException {
        String renderedHTML = viewBuilder.getRenderedAccountDeletionView();
        response.setContentType("text/html;charset=UTF-8");
        response.getWriter().print(renderedHTML);
    }

    /**
     * Processes the user account deletion request, including deleting the user and executing the routing strategy.
     *
     * @param request  The HttpServletRequest object containing client request information.
     * @param response The HttpServletResponse object to send the response back to the client.
     * @throws ServletException If the request could not be handled.
     * @throws IOException      If an input or output error occurs.
     */
    private void processUserAccountDeletionRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        deleteUser(getUsername(request));
        setDeletedAccountAttribute(request);
        executeRoutingStrategy(request, response);
    }

    /**
     * Sets the "deletedAccount" attribute in the user's session. This is used to indicate that the user's account
     * has been deleted, allowing for appropriate handling by in the logout process.
     *
     * @param request The HttpServletRequest object, which provides access to the user's session.
     */
    private void setDeletedAccountAttribute(HttpServletRequest request) {
        HttpSession session = request.getSession();
        session.setAttribute("deletedAccount", "deletedAccount");
    }

    /**
     * Retrieves the username from the current user session to identify the user to be deleted.
     *
     * @param request The HttpServletRequest object containing client request information.
     * @return A string containing the username.
     */
    private String getUsername(HttpServletRequest request) {
        HttpSession session = request.getSession();
        return (String) session.getAttribute("username");
    }

    /**
     * Deletes the user with the specified username by performing a soft delete.
     *
     * @param username The username of the user to delete.
     */
    private void deleteUser(String username) {
        UserManagementService userService = new UserManagementService(new UserDAO());
        userService.softDeleteUser(username);
    }

    /**
     * Executes the routing strategy, forwarding the request to logout.
     *
     * @param request  The HttpServletRequest object containing client request information.
     * @param response The HttpServletResponse object to send the response back to the client.
     * @throws ServletException If the request could not be handled.
     * @throws IOException      If an input or output error occurs.
     */
    private void executeRoutingStrategy(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        new ForwardRequestRoutingStrategy(ServletURIs.LOG_OUT).handleRouting(request, response, getServletContext().getContextPath());
    }
}
