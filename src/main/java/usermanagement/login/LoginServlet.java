package usermanagement.login;

import constants.*;
import database.dao.UserDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import services.UserAuthenticationService;
import services.UserInputService;
import services.UserManagementService;
import routingstrategies.RoutingStrategy;
import routingstrategies.RedirectWithUserMessageStrategy;
import routingstrategies.AccountNotActivatedStrategy;
import routingstrategies.SuccessfulAuthenticationStrategy;
import viewbuilder.ViewBuilder;

import java.io.IOException;

/**
 * Responsible for handling user login operations. A GET request renders the login view. A POST request
 * validates user inputs, authenticates users, and routes to appropriate views based on authentication results.
 * These operations are handled internally by the service classes {@link UserAuthenticationService}, {@link UserInputService},
 * and {@link UserManagementService}. Routing is handled by the {@link RoutingStrategy} interface and its implementations.
 * <p>
 * This servlet uses a {@link ViewBuilder} to render the login view.
 *
 * @author Björn Forsberg
 */
@WebServlet(name = "LoginServlet", urlPatterns = ServletURIs.LOGIN)
public class LoginServlet extends HttpServlet {

    private static ViewBuilder viewBuilder = null;

    /**
     * Initializes the servlet with a viewBuilder if not already initialized.
     */
    @Override
    public void init() {
        if (viewBuilder == null) {
            viewBuilder = new ViewBuilder(getServletContext().getContextPath(), getServletContext().getRealPath("/"));
        }
    }

    /**
     * Handles HTTP GET requests to render the login view.
     *
     * @param request  The HttpServletRequest object.
     * @param response The HttpServletResponse object.
     * @throws IOException If an input or output error is detected when handling the GET request.
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        respondWithLoginView(request, response);
    }

    /**
     * Handles HTTP POST requests to authenticate the user login.
     *
     * @param request  The HttpServletRequest object.
     * @param response The HttpServletResponse object.
     * @throws IOException      If an input or output error is detected when handling the POST request.
     * @throws ServletException If the request could not be handled.
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        authenticateUserLogin(request, response);
    }

    /**
     * Responds with the HTML content of the login view.
     *
     * @param request  The HttpServletRequest object.
     * @param response The HttpServletResponse object.
     * @throws IOException If an input or output error occurs during writing the response.
     */
    private void respondWithLoginView(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String renderedHTML = viewBuilder.getRenderedLoginView(getUserMessage(request));
        response.setContentType("text/html;charset=UTF-8");
        response.getWriter().print(renderedHTML);
    }

    /**
     * Retrieves a user message based on session attributes or a logout message parameter.
     *
     * @param request The HttpServletRequest object containing the session or request parameters.
     * @return The user message to be displayed.
     */
    private String getUserMessage(HttpServletRequest request) {
        HttpSession session = request.getSession();
        String validationUserMessage = (String) session.getAttribute("validationUserMessage");
        session.removeAttribute("validationUserMessage");
        String loginUserMessage = (String) session.getAttribute("loginUserMessage");
        session.removeAttribute("loginUserMessage");
        String userMessage = validationUserMessage != null ? validationUserMessage : loginUserMessage;

        String paramValue = request.getParameter("logoutMessage");
        if (isLogoutMessageValid(paramValue)) {
            userMessage = paramValue;
        }
        return userMessage;
    }

    /**
     * Validates if the provided logout message parameter value is valid.
     *
     * @param paramValue The value of the logout message parameter.
     * @return true if the parameter value is valid, false otherwise.
     */
    private boolean isLogoutMessageValid(String paramValue) {
        return paramValue != null && (paramValue.equals(UserInteractionMessages.LOGOUT_SUCCESSFUL) || paramValue.equals(UserInteractionMessages.USER_NOT_LOGGED_IN) || paramValue.equals(UserInteractionMessages.ACCOUNT_DELETION_SUCCESSFUL));
    }

    /**
     * Authenticates the user login based on the provided username and password in the request parameters.
     * Executes routing strategies based on the authentication result.
     *
     * @param request  The HttpServletRequest object containing the login details.
     * @param response The HttpServletResponse object to write the response.
     * @throws IOException      If an input or output error is detected during the authentication process.
     * @throws ServletException If the request could not be handled.
     */
    private void authenticateUserLogin(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        String userInputStatus = validate(username, password);

        if (isInvalidInput(userInputStatus)) {
            executeRoutingStrategy(userInputStatus, username, request, response);
        } else {
            String authenticationStatus = authenticate(username, password);
            executeRoutingStrategy(authenticationStatus, username, request, response);
        }
    }

    /**
     * Validates the provided username and password for login.
     *
     * @param username The username to validate.
     * @param password The password to validate.
     * @return A status message indicating the validation result.
     */
    private String validate(String username, String password) {
        UserInputService userInputService = new UserInputService();
        return userInputService.validateLoginInput(username, password);
    }

    /**
     * Checks if the user input status is invalid.
     *
     * @param userInputStatus The status of the user input validation.
     * @return true if the input is invalid, false otherwise.
     */
    private boolean isInvalidInput(String userInputStatus) {
        return !userInputStatus.equals(UserInteractionMessages.INPUT_VALIDATION_SUCCESSFUL);
    }

    /**
     * Executes the appropriate routing strategy based on the provided status.
     *
     * @param status   The status determining the routing strategy.
     * @param username The username of the user.
     * @param request  The HttpServletRequest object.
     * @param response The HttpServletResponse object.
     * @throws IOException      If an input or output error occurs during routing.
     * @throws ServletException If the request could not be handled.
     */
    private void executeRoutingStrategy(String status, String username, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        RoutingStrategy strategy = getRoutingStrategyBasedOnStatus(status, username);
        strategy.handleRouting(request, response, getServletContext().getContextPath());
    }

    /**
     * Gets the appropriate routing strategy based on the provided status and username.
     * Default strategy is RedirectWithUserMessageStrategy, used when authentication failed,
     * which redirects back to the login page with the given status.
     *
     * @param status   The status determining the routing strategy.
     * @param username The username of the user.
     * @return An instance of a RoutingStrategy based on the given status.
     */
    private RoutingStrategy getRoutingStrategyBasedOnStatus(String status, String username) {
        return switch (status) {
            case UserInteractionMessages.AUTHENTICATION_SUCCESSFUL -> new SuccessfulAuthenticationStrategy(ServletURIs.USER_PAGE, username);
            case UserInteractionMessages.USER_NOT_ACTIVATED -> new AccountNotActivatedStrategy(ServletURIs.EMAIL_SENDER, getEmail(username));
            default -> new RedirectWithUserMessageStrategy(ServletURIs.LOGIN, status);
        };
    }

    /**
     * Authenticates the provided username and password.
     *
     * @param username The username to authenticate.
     * @param password The password to authenticate.
     * @return A status message indicating the authentication result.
     */
    private String authenticate(String username, String password) {
        UserAuthenticationService userAuthenticationService = new UserAuthenticationService(new UserDAO(), new UserInputService());
        return userAuthenticationService.authenticateUser(username, password);
    }

    /**
     * Retrieves the email address associated with the given username.
     *
     * @param username The username for which to retrieve the email.
     * @return The email address associated with the username.
     */
    private String getEmail(String username) {
        UserManagementService userManagementService = new UserManagementService(new UserDAO());
        return userManagementService.getEmail(username);
    }

}
