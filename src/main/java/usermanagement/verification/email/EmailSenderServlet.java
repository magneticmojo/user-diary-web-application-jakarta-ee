package usermanagement.verification.email;

import constants.ServletURIs;
import constants.UserInteractionMessages;
import database.PasswordHashingUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import routingstrategies.RedirectWithUserMessageStrategy;
import routingstrategies.RoutingStrategy;
import usermanagement.verification.VerificationCodeGenerator;
import usermanagement.verification.VerificationCodeService;

import java.io.IOException;

/**
 * Responsible for handling email verification requests. Only POST requests are allowed.
 * This servlet is used to send verification codes to the user's email and handle requests for resending codes.
 *
 * @author Björn Forsberg
 */
@WebServlet(name = "EmailSenderServlet", urlPatterns = ServletURIs.EMAIL_SENDER)
public class EmailSenderServlet extends HttpServlet {

    /**
     * Handles the POST request to send a verification code or resend a new code to the user's email.
     * Subsequently, redirects the user to the verification page.
     *
     * @param request  The HttpServletRequest object containing client request information.
     * @param response The HttpServletResponse object to respond to the client.
     * @throws ServletException If a servlet error occurs during the request handling.
     * @throws IOException      If an input or output error occurs while processing the request.
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String email = (String) request.getSession().getAttribute("email");
        String userMessage = processRequest(request, email);
        redirectToVerification(userMessage, request, response);
    }

    /**
     * Processes the verification request.
     *
     * @param request The HttpServletRequest object.
     * @param email   The email address to which the verification code is sent.
     * @return A user interaction message indicating the outcome of the process.
     * @throws ServletException If a servlet error occurs during the request handling.
     */
    private String processRequest(HttpServletRequest request, String email) throws ServletException {
        VerificationCodeService verificationCodeService = new VerificationCodeService();
        String userMessage;

        if (verificationCodeService.doesEmailExist(email)) {
            if (isUserRequestingNewCode(request)) {
                verificationCodeService.deleteCode(email);
                boolean successfulProcess = processUserRequestForNewCode(email, verificationCodeService);
                if (successfulProcess) {
                    userMessage = UserInteractionMessages.NEW_VERIFICATION_CODE_SENT;
                } else {
                    userMessage = UserInteractionMessages.ERROR_SENDING_NEW_VERIFICATION_CODE;
                }
            } else {
                userMessage = UserInteractionMessages.PREVIOUSLY_NOT_ACTIVATED;
            }
        } else {
            boolean successfulProcess = processUserRequestForNewCode(email, verificationCodeService);
            if (successfulProcess) {
                userMessage = UserInteractionMessages.USER_NOT_ACTIVATED;
            } else {
                userMessage = UserInteractionMessages.ERROR_SENDING_VERIFICATION_CODE;
            }
        }

        return userMessage;
    }


    /**
     * Checks whether the user is requesting a new verification code.
     *
     * @param request The HttpServletRequest object.
     * @return true if the user is requesting a new code; false otherwise.
     */
    private boolean isUserRequestingNewCode(HttpServletRequest request) {
        return request.getParameter("sendNewCode") != null;
    }

    /**
     * Processes the user's request for a new verification code.
     *
     * @param email                   The email address to which the new verification code is sent.
     * @param verificationCodeService The service handling verification code operations.
     * @return true if the process is successful; false otherwise.
     * @throws ServletException If a servlet error occurs during the request handling.
     */
    private boolean processUserRequestForNewCode(String email, VerificationCodeService verificationCodeService) throws ServletException {
        String code = generateVerificationCode();
        String hashedCode = PasswordHashingUtil.hashPassword(code);
        boolean isCodeStored = verificationCodeService.storeCode(email, hashedCode);
        if (isCodeStored) {
            sendCodeByEmail(email, code);
        } else {
            return false;
        }
        return true;
    }

    /**
     * Generates a new verification code.
     *
     * @return The generated verification code as a String.
     */
    private String generateVerificationCode() {
        VerificationCodeGenerator codeGenerator = new VerificationCodeGenerator();
        return Integer.toString(codeGenerator.generateCode());
    }

    /**
     * Sends the verification code to the specified email address.
     *
     * @param email The email address to which the code is sent.
     * @param code  The verification code to be sent.
     * @throws ServletException If a servlet error occurs during email sending.
     */
    private void sendCodeByEmail(String email, String code) throws ServletException {
        EmailService emailService = new EmailService();
        emailService.processEmail("verification@mydairy.com", email, "", "", "MY DIARY : Your Verification Code", code);
    }

    /**
     * Redirects the user to the verification page with a specified user message.
     *
     * @param userMessage The user message to be displayed on the verification page.
     * @param request     The HttpServletRequest object.
     * @param response    The HttpServletResponse object.
     * @throws IOException      If an input or output error occurs while redirecting.
     * @throws ServletException If a servlet error occurs during redirecting.
     */
    private void redirectToVerification(String userMessage, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        RoutingStrategy strategy = new RedirectWithUserMessageStrategy(ServletURIs.VERIFICATION, userMessage);
        strategy.handleRouting(request, response, getServletContext().getContextPath());
    }
}