package usermanagement.registration;

import constants.ServletURIs;
import constants.UserInteractionMessages;
import database.dao.UserDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import services.UserAuthenticationService;
import services.UserInputService;
import services.UserRegistrationService;
import routingstrategies.RedirectWithUserMessageStrategy;
import routingstrategies.AccountNotActivatedStrategy;
import routingstrategies.RoutingStrategy;
import viewbuilder.ViewBuilder;

import java.io.IOException;

/**
 * Servlet responsible for handling user registration operations. A GET request renders the registration view.
 * A POST request includes input validation, user creation, registration status handling and consequent routing.
 * These operations are handled internally by the service classes {@link UserRegistrationService} and {@link UserInputService}.
 * Routing is handled by the {@link RoutingStrategy} interface and its implementations.
 *
 * @author Björn Forsberg
 */
@WebServlet(name = "RegistrationServlet", urlPatterns = ServletURIs.REGISTRATION)
public class RegistrationServlet extends HttpServlet {

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
     * Handles the GET request to display the registration view.
     *
     * @param request  The HttpServletRequest object containing client request information.
     * @param response The HttpServletResponse object to send the response back to the client.
     * @throws IOException If an input or output error occurs during the handling of the request.
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        respondWithRegistrationView(request, response);
    }

    /**
     * Handles the POST request to register a new user.
     *
     * @param request  The HttpServletRequest object containing client request information.
     * @param response The HttpServletResponse object to send the response back to the client.
     * @throws ServletException If the request could not be handled.
     * @throws IOException      If an input or output error occurs during the handling of the request.
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        registerNewUser(request, response);
    }

    /**
     * Generates the registration view and writes it to the response.
     *
     * @param request  The HttpServletRequest object containing client request information.
     * @param response The HttpServletResponse object to send the response back to the client.
     * @throws IOException If an input or output error occurs during the handling of the request.
     */
    private void respondWithRegistrationView(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String renderedHTML = viewBuilder.getRenderedRegistrationView(getUserMessage(request));
        response.setContentType("text/html;charset=UTF-8");
        response.getWriter().print(renderedHTML);
    }

    /**
     * Retrieves the user message from the session if it exists.
     *
     * @param request The HttpServletRequest object containing client request information.
     * @return The user message retrieved from the session.
     */
    private String getUserMessage(HttpServletRequest request) {
        HttpSession session = request.getSession();
        String validationUserMessage = (String) session.getAttribute("validationUserMessage");
        session.removeAttribute("validationUserMessage");
        String registrationUserMessage = (String) request.getSession().getAttribute("registrationUserMessage");
        session.removeAttribute("registrationUserMessage");
        return validationUserMessage != null ? validationUserMessage : registrationUserMessage;
    }

    /**
     * Registers a new user account by first validating the user input and then registering the user.
     * The routing strategy based is based on the return value of the validation or return value of registration.
     *
     * @param request  The HttpServletRequest object containing client request information.
     * @param response The HttpServletResponse object to send the response back to the client.
     * @throws IOException      If an input or output error occurs.
     * @throws ServletException If the request could not be handled.
     */
    private void registerNewUser(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String email = request.getParameter("email");

        String userInputStatus = validate(username, password, email);

        if (isInvalidInput(userInputStatus)) {
            executeRoutingStrategy(userInputStatus, email, request, response);
        } else {
            String registrationStatus = register(username, email, password);
            executeRoutingStrategy(registrationStatus, email, request, response);
        }
    }

    /**
     * Executes a routing strategy based on the status.
     *
     * @param status   The status message.
     * @param email    The email address of the user.
     * @param request  The HttpServletRequest object containing client request information.
     * @param response The HttpServletResponse object to send the response back to the client.
     * @throws ServletException If the request could not be handled.
     * @throws IOException      If an input or output error occurs.
     */
    private void executeRoutingStrategy(String status, String email, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        RoutingStrategy strategy = getRoutingStrategyBasedOnStatus(status, email);
        strategy.handleRouting(request, response, getServletContext().getContextPath());
    }


    /**
     * Determines the appropriate routing strategy based on the status.
     *
     * @param status The status message.
     * @param email  The email address of the user.
     */
    private RoutingStrategy getRoutingStrategyBasedOnStatus(String status, String email) {
        return UserInteractionMessages.AUTHENTICATION_SUCCESSFUL.equals(status) ?
                new AccountNotActivatedStrategy(ServletURIs.EMAIL_SENDER, email) :
                new RedirectWithUserMessageStrategy(ServletURIs.REGISTRATION, status);
    }

    /**
     * Validates the input provided for user registration.
     *
     * @param username The username provided by the user.
     * @param password The password provided by the user.
     * @param email    The email address provided by the user.
     * @return A status message indicating the validation result.
     */
    private String validate(String username, String password, String email) {
        UserInputService userInputService = new UserInputService();
        return userInputService.validateRegistrationInput(username, password, email);
    }

    /**
     * Determines if the input validation was unsuccessful.
     *
     * @param userInputStatus The status of the user input validation.
     * @return {@code true} if the input is invalid, {@code false} otherwise.
     */
    private boolean isInvalidInput(String userInputStatus) {
        return !userInputStatus.equals(UserInteractionMessages.INPUT_VALIDATION_SUCCESSFUL);
    }

    /**
     * Registers a new user using the provided username, email, and password.
     *
     * @param username The username for the new user.
     * @param email    The email address for the new user.
     * @param password The password for the new user.
     * @return A status message indicating the registration result.
     */
    private String register(String username, String email, String password) {
        UserRegistrationService userService = new UserRegistrationService(new UserDAO(), new UserInputService());
        return userService.registerNewUser(username, email, password);
    }
}
