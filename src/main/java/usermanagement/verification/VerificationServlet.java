package usermanagement.verification;

import constants.ServletURIs;
import constants.UserInteractionMessages;
import database.dao.UserDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import services.UserInputService;
import services.UserManagementService;
import routingstrategies.RedirectWithUserMessageStrategy;
import routingstrategies.RoutingStrategy;
import viewbuilder.ViewBuilder;

import java.io.IOException;
import java.util.Enumeration;
/**
 * Responsible for handling user verification operations. A GET request renders the verification view, allowing the user
 * to input the verification code or requesting a new one to be sent. A POST request validates the entered verification code, activates the user's account,
 * and routes to appropriate views based on the verification results. An invalid verification code will result in a redirect to the verification view with an error message.
 * Operations are handled internally by the service classes {@link VerificationCodeService}, {@link UserInputService},
 * and {@link UserManagementService}. Routing is handled by the {@link RedirectWithUserMessageStrategy}.
 *
 * @author Björn Forsberg
 */
@WebServlet(name = "VerificationServlet", urlPatterns = ServletURIs.VERIFICATION)
public class VerificationServlet extends HttpServlet {

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
     * Handles GET requests by responding with the verification view.
     *
     * @param request  the HttpServletRequest object
     * @param response the HttpServletResponse object
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        respondWithVerificationView(request, response);
    }

    /**
     * Handles POST requests by verifying the user.
     *
     * @param request  the HttpServletRequest object
     * @param response the HttpServletResponse object
     * @throws IOException, ServletException if an I/O error or servlet error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        verifyUser(request, response);
    }

    /**
     * Sends the verification view as the response.
     *
     * @param request  the HttpServletRequest object
     * @param response the HttpServletResponse object
     * @throws IOException if an I/O error occurs
     */
    private void respondWithVerificationView(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String renderedHTML = viewBuilder.getRenderedVerificationView(getUserMessage(request));
        response.setContentType("text/html;charset=UTF-8");
        response.getWriter().print(renderedHTML);
    }

    /**
     * Retrieves the user message from the HTTP session.
     *
     * @param request the HttpServletRequest object
     * @return the user message
     */
    private static String getUserMessage(HttpServletRequest request) {
        HttpSession session = request.getSession();
        String userMessage = (String) session.getAttribute("verificationUserMessage");
        session.removeAttribute("verificationUserMessage");
        return userMessage;
    }

    /**
     * Verifies the user by checking the provided verification code.
     *
     * @param request  the HttpServletRequest object
     * @param response the HttpServletResponse object
     * @throws ServletException, IOException if an error occurs during the process
     */
    private void verifyUser(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        String email = (String) session.getAttribute("email");
        String code = request.getParameter("verificationCode");

        UserInputService userInputService = new UserInputService();
        code = userInputService.sanitizeInput(code);

        VerificationCodeService verificationCodeService = new VerificationCodeService();

        if (code != null && verificationCodeService.isCodeValid(email, code)) {
            activateUser(email);
            cleanUpAfterVerificationProcess(verificationCodeService, session, email);
            executeRoutingStrategy(ServletURIs.LOGIN, UserInteractionMessages.ACCOUNT_ACTIVATED, request, response);
        } else {
            executeRoutingStrategy(ServletURIs.VERIFICATION, UserInteractionMessages.INVALID_VERIFICATION_CODE, request, response);
        }
    }

    /**
     * Removes the verification code and email attributes in the database after successful verification.
     *
     * @param verificationCodeService the VerificationCodeService object
     * @param session                 the HttpSession object
     * @param email                   the email of the user
     */
    private void cleanUpAfterVerificationProcess(VerificationCodeService verificationCodeService, HttpSession session, String email) {
        verificationCodeService.deleteCode(email);
        session.removeAttribute("email");
    }

    /**
     * Activates the user account associated with the given email.
     *
     * @param email the email of the user
     */
    private void activateUser(String email) {
            UserManagementService userService = new UserManagementService(new UserDAO());
            userService.activateUser(email);
    }

    /**
     * Executes the routing strategy with the given URI and user message.
     *
     * @param uri         the URI to redirect to
     * @param userMessage the user message to be displayed
     * @param request     the HttpServletRequest object
     * @param response    the HttpServletResponse object
     * @throws ServletException, IOException if an error occurs during the routing
     */
    private void executeRoutingStrategy(String uri, String userMessage, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        RoutingStrategy strategy = getRoutingStrategy(uri, userMessage);
        strategy.handleRouting(request, response, getServletContext().getContextPath());
    }

    /**
     * Creates and returns a RedirectWithUserMessageStrategy with the given URI and user message.
     *
     * @param uri         the URI to redirect to
     * @param userMessage the user message to be displayed
     * @return a RedirectWithUserMessageStrategy object
     */
    private RoutingStrategy getRoutingStrategy(String uri, String userMessage) {
        return new RedirectWithUserMessageStrategy(uri, userMessage);
    }
}


