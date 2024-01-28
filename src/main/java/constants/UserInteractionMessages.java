package constants;

import java.util.Set;

/**
 * Defines a collection of constants representing messages that are used for user interaction within the web application.
 * These constants provide standardized and centralized messages for various aspects such as validation, login, registration,
 * verification, user page activities, logout, and system messages.
 * <p>
 * The class also contains sets that group related messages together. The sets are used by {@link routingstrategies.RedirectWithUserMessageStrategy}
 * to determine which session attribute to be set based on the user message.
 *
 * @author Björn Forsberg
 */

public final class UserInteractionMessages {

    public static final String USER_NOT_LOGGED_IN = "Please log in to use the application.";

    // ********************************** VALIDATION (LOGIN & REGISTRATION) MESSAGES *************************
    public static final String HAS_EMPTY_FIELDS = "Please fill in all fields.";
    public static final String INVALID_USERNAME_FORMAT = "Username must be between 4 and 8 characters long and can only include uppercase and lowercase letters, and digits.";
    public static final String INVALID_PASSWORD_FORMAT = "Password must be between 4 and 8 characters long, and must include at least one uppercase letter, one lowercase letter, one digit, and one special character from this set: !@#$%^&*.";

    // ********************************** LOGIN MESSAGES *****************************************************
    public static final String FAILED_AUTHENTICATION = "Incorrect username or password. New user?";
    public static final String DELETED_ACCOUNT = "This account has been deactivated. If you think this is a mistake, please contact support.";
    public static final String ACCOUNT_ACTIVATED = "Account activated! Please log in.";

    // ********************************** REGISTRATION MESSAGES **********************************************
    public static final String INVALID_EMAIL_FORMAT = "Email format invalid.";
    public static final String UNAVAILABLE_USERNAME_OR_EMAIL = "Username or email already taken.";


    // ********************************** VERIFICATION MESSAGES **********************************************
    public static final String USER_NOT_ACTIVATED = "A verification code was sent to your registered email. Please enter code below to verify your account.";
    public static final String PREVIOUSLY_NOT_ACTIVATED = "Your account is not yet verified. Check your registered email for verification code. Please enter code below to verify your account.";

    public static final String INVALID_VERIFICATION_CODE = "Invalid Verification Code - Try again or send new code.";
    public static final String NEW_VERIFICATION_CODE_SENT = "Verification code sent to email.";
    public static final String ERROR_SENDING_VERIFICATION_CODE = "Error sending verification code. Please try again.";
    public static final String ERROR_SENDING_NEW_VERIFICATION_CODE = "Error sending new verification code. Please try again.";

    // ********************************** USER PAGE MESSAGES *************************************************
    public static final String TITLE_MAX_LENGTH_EXCEEDED = "Title exceeds maximum length of 200 characters.";
    public static final String IMAGE_CAP_MAX_LENGTH_EXCEEDED = "Image caption exceeds maximum length of 200 characters.";
    public static final String POST_MAX_LENGTH_EXCEEDED = "Post exceeds maximum length of 20.000 characters.";
    public static final String FILE_INVALID_CONTENT_TYPE = "Invalid content type";
    public static final String FILE_INVALID_EXTENSION = "Invalid file extension";
    public static final String FILE_TOO_LARGE = "File size exceeds the limit";

    // ********************************** LOGOUT MESSAGES ****************************************************
    public static final String LOGOUT_SUCCESSFUL = "Successful logout.";

    // ********************************** ACCOUNT DELETION MESSAGES *************************************************

    public static final String ACCOUNT_DELETION_SUCCESSFUL = "Account deletion successful.";

    // ********************************** SYSTEM MESSAGES ****************************************************
    public static final String INPUT_VALIDATION_SUCCESSFUL = "SUCCESS";
    public static final String AUTHENTICATION_SUCCESSFUL = "SUCCESS";
    public static final String REGISTRATION_SUCCESSFUL = "SUCCESS";

    public static final Set<String> VALIDATION_MESSAGES = Set.of(
            HAS_EMPTY_FIELDS, INVALID_USERNAME_FORMAT, INVALID_PASSWORD_FORMAT
    );

    public static final Set<String> LOGIN_MESSAGES = Set.of(
            FAILED_AUTHENTICATION, DELETED_ACCOUNT, USER_NOT_LOGGED_IN, ACCOUNT_ACTIVATED
    );

    public static final Set<String> REGISTRATION_MESSAGES = Set.of(
            INVALID_EMAIL_FORMAT, UNAVAILABLE_USERNAME_OR_EMAIL
    );

    public static final Set<String> VERIFICATION_MESSAGES = Set.of(
            USER_NOT_ACTIVATED, PREVIOUSLY_NOT_ACTIVATED, INVALID_VERIFICATION_CODE, NEW_VERIFICATION_CODE_SENT, ERROR_SENDING_VERIFICATION_CODE, ERROR_SENDING_NEW_VERIFICATION_CODE
    );
}
