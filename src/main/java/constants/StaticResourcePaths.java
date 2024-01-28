package constants;

/**
 * Defines a collection of constants representing the paths to various static resources used in the web application.
 * These constants are utilized to reference HTML views, CSS files, and other future static content required by the application.
 * <p>
 * This class contains constants for paths to specific views such as login, registration, user page, settings, account deletion,
 * as well as a path to the CSS file for the user page.
 *
 * @author Björn Forsberg
 */
public final class StaticResourcePaths {

    public static final String LOGIN_VIEW = "views/login.html";
    public static final String VERIFICATION_VIEW = "views/verification.html";
    public static final String REGISTRATION_VIEW = "views/registration.html";
    public static final String USER_PAGE_VIEW = "views/user-page.html";
    public static final String USER_PAGE_CSS = "/static/css/user-page.css";
    public static final String SETTINGS_VIEW = "views/settings.html";
    public static final String ACCOUNT_DELETION_VIEW = "views/account-deletion.html";
}
