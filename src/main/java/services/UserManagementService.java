package services;

import database.dao.UserDAO;
import entities.User;

/**
 * Service class responsible for managing user-related operations, such as retrieval, activation, and soft deletion.
 * Utilizes the {@link UserDAO} to interact with the database.
 *
 * @author Björn Forsberg
 */
public class UserManagementService {
    private final UserDAO userDAO;

    /**
     * Constructs the UserManagementService with the specified UserDAO.
     *
     * @param userDAO UserDAO instance to interact with the user database.
     */
    public UserManagementService(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    /**
     * Retrieves the email address associated with the given username.
     *
     * @param username The username for which the email is to be retrieved.
     * @return The email address associated with the username.
     * @throws IllegalArgumentException if the user with the given username is not found.
     */
    public String getEmail(String username) {
        User user = userDAO.findByUsername(username);
        if (user == null) {
            throw new IllegalArgumentException("User not found");
        }
        return user.getEmail();
    }

    /**
     * Retrieves a User object and the associated diary posts by username.
     *
     * @param username The username for which the user and posts are to be retrieved.
     * @return The User object associated with the username.
     * @throws IllegalArgumentException if the user with the given username is not found.
     */
    public User getUserAndPosts(String username) {
        return userDAO.getUserWithDiaryPosts(username);
    }

    /**
     * Performs a soft deletion on a user by setting the 'deleted' and 'active' flags to true and false, respectively.
     *
     * @param username The username of the user to be deleted.
     * @throws IllegalArgumentException if the user with the given username is not found.
     */
    public void softDeleteUser(String username) {
        User user = userDAO.findByUsername(username);
        if (user == null) {
            throw new IllegalArgumentException("User not found");
        }
        user.setDeleted(true);
        user.setActive(false);
        userDAO.update(user);
    }

    /**
     * Activates a user by setting the 'active' flag to true.
     *
     * @param email The email address of the user to be activated.
     * @throws IllegalArgumentException if the user with the given email is not found.
     */
    public void activateUser(String email) {
        User user = userDAO.findByEmail(email);
        if (user == null) {
            throw new IllegalArgumentException("User not found");
        }
        user.setActive(true);
        userDAO.update(user);
    }


}

