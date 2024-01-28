package services;

import constants.UserInteractionMessages;
import database.PasswordHashingUtil;
import database.dao.UserDAO;
import entities.User;

/**
 * Service class responsible for registering new users. Handles user input validation,
 * username and email availability checking, and persisting new users in the database.
 * Utilizes the {@link UserDAO} to interact with the database and {@link UserInputService}.
 *
 * @author Björn Forsberg
 */
public class UserRegistrationService {
    private final UserDAO userDAO;

    private final UserInputService userInputService;

    /**
     * Constructs the UserRegistrationService with the injected UserDAO and UserInputService instances.
     *
     * @param userDAO          Data Access Object to interact with the user database.
     * @param userInputService Service to handle input sanitization and validation.
     */
    public UserRegistrationService(UserDAO userDAO, UserInputService userInputService) {
        this.userDAO = userDAO;
        this.userInputService = userInputService;
    }

    /**
     * Registers a new user with the given username, email, and password.
     *
     * @param username The username of the new user.
     * @param email    The email address of the new user.
     * @param password The password of the new user.
     * @return A message indicating success or an error message if the username or email is unavailable.
     */
    public String registerNewUser(String username, String email, String password) {

        if (!isUsernameAvailable(username) || !isEmailAvailable(email)) {
            return UserInteractionMessages.UNAVAILABLE_USERNAME_OR_EMAIL;
        }

        persistNewUser(username, password, email);

        return UserInteractionMessages.REGISTRATION_SUCCESSFUL;
    }


    /**
     * Checks if the given username is available.
     *
     * @param username The username to check.
     * @return true if the username is available, false otherwise.
     */
    private boolean isUsernameAvailable(String username) {
        return userDAO.findByUsername(username) == null;
    }

    /**
     * Checks if the given email address is available.
     *
     * @param email The email to check.
     * @return true if the email is available, false otherwise.
     */
    private boolean isEmailAvailable(String email) {
        return userDAO.findByEmail(email) == null;
    }

    /**
     * Persists a new user with the specified username, password, and email.
     * The method handles input sanitization and password hashing.
     *
     * @param username The username of the new user.
     * @param password The password of the new user.
     * @param email    The email address of the new user.
     */
    private void persistNewUser(String username, String password, String email) {
        username = userInputService.sanitizeInput(username);
        password = userInputService.sanitizeInput(password);
        email = userInputService.sanitizeInput(email);

        String hashedPassword = PasswordHashingUtil.hashPassword(password);
        User user = createUser(username, hashedPassword, email);
        userDAO.insert(user);
    }

    /**
     * Creates a new User object with the specified username, hashed password, and email.
     * The new user is marked as inactive and not deleted.
     *
     * @param username       The username of the new user.
     * @param hashedPassword The hashed password of the new user.
     * @param email          The email address of the new user.
     * @return A User object representing the new user.
     */
    private User createUser(String username, String hashedPassword, String email) {
        User user = new User();
        user.setUsername(username);
        user.setPassword(hashedPassword);
        user.setEmail(email);
        user.setActive(false);
        user.setDeleted(false);
        return user;
    }
}

