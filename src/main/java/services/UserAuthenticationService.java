package services;

import constants.UserInteractionMessages;
import database.PasswordHashingUtil;
import database.dao.UserDAO;
import entities.User;
import org.apache.commons.text.StringEscapeUtils;

/**
 * Responsible for authenticating user credentials. This class utilizes {@link UserInputService}
 * to sanitize inputs and {@link UserDAO} to validate user credentials. This includes checking if a user is deleted or inactive.
 *
 * @author Björn Forsberg
 */
public class UserAuthenticationService {

    private final UserDAO userDAO;
    private final UserInputService userInputService;

    /**
     * Constructs a UserAuthenticationService with an injected UserDAO and UserInputService.
     *
     * @param userDAO          UserDAO instance to handle user-related database operations.
     * @param userInputService UserInputService instance to sanitize user inputs.
     */
    public UserAuthenticationService(UserDAO userDAO, UserInputService userInputService) {
        this.userDAO = userDAO;
        this.userInputService = userInputService;

    }


    /**
     * Authenticates the user's credentials by verifying the username and password.
     * Sanitizes the input and performs checks for deleted or inactive accounts.
     *
     * @param username The username input from the user.
     * @param password The password input from the user.
     * @return         A string message representing the authentication result.
     */
    public String authenticateUser(String username, String password) {
        String sanitizedUsername = userInputService.sanitizeInput(username);
        String sanitizedPassword = userInputService.sanitizeInput(password);

        User user = userDAO.findByUsername(sanitizedUsername);

        if (user != null) {
            if (PasswordHashingUtil.verifyPassword(sanitizedPassword, user.getPassword())) {

                if (isDeleted(sanitizedUsername)) {
                    return UserInteractionMessages.DELETED_ACCOUNT;
                }

                if (!isActivated(sanitizedUsername)) {
                    return UserInteractionMessages.USER_NOT_ACTIVATED;
                }

                return UserInteractionMessages.AUTHENTICATION_SUCCESSFUL;
            } else {
                return UserInteractionMessages.FAILED_AUTHENTICATION;
            }
        }
        return UserInteractionMessages.FAILED_AUTHENTICATION;
    }

    /**
     * Checks if the user associated with the given username is activated.
     *
     * @param username The username to check for activation status.
     * @return         True if the user is activated, false otherwise.
     * @throws IllegalArgumentException if the user is not found.
     */
    private boolean isActivated(String username) {
        User user = userDAO.findByUsername(username);
        if (user == null) {
            throw new IllegalArgumentException("User not found");
        }
        return user.getActive();
    }

    /**
     * Checks if the user associated with the given username is deleted.
     *
     * @param username The username to check for deletion status.
     * @return         True if the user is deleted, false otherwise.
     * @throws IllegalArgumentException if the user is not found.
     */
    private boolean isDeleted(String username) {
        User user = userDAO.findByUsername(username);
        if (user == null) {
            throw new IllegalArgumentException("User not found");
        }
        return user.getDeleted();
    }
}
