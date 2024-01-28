package services;

import constants.UserInteractionMessages;
import org.apache.commons.text.StringEscapeUtils;

import java.util.Arrays;
import java.util.Objects;
/**
 * Handles user input validation and sanitation. The class has methods for checking various user input constraints such as empty fields,
 * invalid username, password, and email formats, and sanitizing potentially unsafe input.
 *
 * @author Björn Forsberg
 */
public class UserInputService {

    private static final String USER_NAME_PATTERN = "^[a-zA-Z0-9]{4,8}$";
    private static final String PASSWORD_PATTERN = "^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#$%^&*])[a-zA-Z0-9!@#$%^&*]{4,8}$";
    private static final String EMAIL_PATTERN = "^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$";

    /**
     * Validates login input for username and password.
     * Checks for empty fields, invalid username, and invalid password format.
     *
     * @param username The username input from the user.
     * @param password The password input from the user.
     * @return A string message representing the validation result.
     */
    public String validateLoginInput(String username, String password) {

        if (hasEmptyFields(username, password)) {
            return UserInteractionMessages.HAS_EMPTY_FIELDS;
        }

        if (isInvalidUsername(username)) {
            return UserInteractionMessages.INVALID_USERNAME_FORMAT;
        }

        if (isInvalidPassword(password)) {
            return UserInteractionMessages.INVALID_PASSWORD_FORMAT;
        }

        return  UserInteractionMessages.INPUT_VALIDATION_SUCCESSFUL;
    }

    /**
     * Validates registration input for username, password, and email.
     * Checks for empty fields and validates the format of the username, password, and email.
     *
     * @param username The username input from the user.
     * @param password The password input from the user.
     * @param email    The email input from the user.
     * @return A string message representing the validation result.
     */
    public String validateRegistrationInput(String username, String password, String email) {

        if (hasEmptyFields(username, password, email)) {
            return UserInteractionMessages.HAS_EMPTY_FIELDS;
        }

        if (isInvalidUsername(username)) {
            return UserInteractionMessages.INVALID_USERNAME_FORMAT;
        }

        if (isInvalidPassword(password)) {
            return UserInteractionMessages.INVALID_PASSWORD_FORMAT;
        }

        if (isInvalidEmail(email)) {
            return UserInteractionMessages.INVALID_EMAIL_FORMAT;
        }

        return UserInteractionMessages.INPUT_VALIDATION_SUCCESSFUL;
    }

    /**
     * Checks if any of the provided input fields are empty or null.
     *
     * @param inputs An array of input strings to be checked.
     * @return True if any of the provided inputs are empty or null, false otherwise.
     */
    private boolean hasEmptyFields(String... inputs) {
        return Arrays.stream(inputs)
                .anyMatch(input -> Objects.isNull(input) || input.isEmpty());
    }

    /**
     * Validates the format of a username against a pattern defined by USER_NAME_PATTERN.
     *
     * @param username The username string to be checked.
     * @return True if the username does not match the pattern, false otherwise.
     */
    private boolean isInvalidUsername(String username) {
        return !username.matches(USER_NAME_PATTERN);
    }

    /**
     * Validates the format of a password against a pattern defined by PASSWORD_PATTERN.
     *
     * @param password The password string to be checked.
     * @return True if the password does not match the pattern, false otherwise.
     */
    private boolean isInvalidPassword(String password) {
        return !password.matches(PASSWORD_PATTERN);
    }

    /**
     * Validates the format of an email address against a pattern defined by EMAIL_PATTERN.
     *
     * @param email The email string to be checked.
     * @return True if the email does not match the pattern, false otherwise.
     */
    private boolean isInvalidEmail(String email) {
        return !email.matches(EMAIL_PATTERN);
    }

    /**
     * Sanitizes the given input string by escaping HTML characters.
     *
     * @param input The input string to sanitize.
     * @return The sanitized input string.
     */
    public String sanitizeInput(String input) {
        return StringEscapeUtils.escapeHtml4(input);
    }

    /**
     * Checks if the given string exceeds the specified maximum length.
     *
     * @param str       The string to check.
     * @param maxLength The maximum allowed length for the string.
     * @return True if the string exceeds the maximum length, false otherwise.
     */
    public boolean exceedsMaxLength(String str, int maxLength) {
        if (str == null) {
            return false;
        }
        int strLength = str.codePointCount(0, str.length());
        return strLength > maxLength;
    }
}
