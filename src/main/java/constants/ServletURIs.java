package constants;

/**
 * Defines a collection of constants representing the various URI patterns used throughout the web application.
 * These URIs are utilized to map the specific servlets, providing a centralized way to manage the URI paths.
 * <p>
 * This class contains constants for paths related to user operations such as login, registration, account settings,
 * email sending, image handling, and others.
 *
 * @author Björn Forsberg
 */
public final class ServletURIs {

    public static final String LOGIN = "/login";
    public static final String REGISTRATION = "/registration";
    public static final String VERIFICATION = "/verification";
    public static final String EMAIL_SENDER = "/email-sender";
    public static final String USER_PAGE = "/user/diary";
    public static final String IMAGE = "/user/image";
    public static final String LOG_OUT = "/user/logout";
    public static final String SETTINGS = "/user/settings";
    public static final String DELETE_ACCOUNT = "/user/account-deletion";
    public static final String USER_PATTERN = "/user/*";

}