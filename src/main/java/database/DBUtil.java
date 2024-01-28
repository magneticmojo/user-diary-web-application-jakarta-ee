package database;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

/**
 * This class provides utility for working with the entity manager factory.
 * It provides a singleton access to the EntityManagerFactory for database interactions,
 * facilitating connections to the database for the DAO classes within the application,
 * {@link database.dao.GenericDAO}, {@link database.dao.ImageDAO}, and {@link database.dao.UserDAO}.
 *
 * @author Björn Forsberg
 */
public class DBUtil {

    private static final EntityManagerFactory emfInstance = Persistence.createEntityManagerFactory("user");

     /**
     * Private constructor to prevent instantiation of this utility class.
     */
    private DBUtil() {
        throw new AssertionError("Cannot be instantiated");
    }

    /**
     * Retrieves the singleton instance of the EntityManagerFactory.
     *
     * @return The EntityManagerFactory instance for the "user" persistence unit
     */
    public static EntityManagerFactory getEMFInstance() {
        return emfInstance;
    }
}
