package database;

import de.mkammerer.argon2.Argon2;
import de.mkammerer.argon2.Argon2Factory;

/**
 * Utility class providing functionality for hashing passwords using the Argon2 algorithm,
 * and verifying hashed passwords. The class is used in the authentication, verification, registration and email processes.
 *
 * @author Björn Forsberg
 */
public final class PasswordHashingUtil {

    private static final Argon2 argon2 = Argon2Factory.create();

    private PasswordHashingUtil() {
        throw new AssertionError("Cannot be instantiated");
    }

    /**
     * Hashes the given password using Argon2 algorithm.
     *
     * @param password The password to be hashed.
     * @return The hashed password.
     */
    public static String hashPassword(String password) {
        try {
            return argon2.hash(10, 65536, 1, password.toCharArray());
        } finally {
            argon2.wipeArray(password.toCharArray());
        }
    }

    /**
     * Verifies if the given password matches the hashed password using Argon2 algorithm.
     *
     * @param password The original password to be verified.
     * @param hashedPassword The hashed password to compare with.
     * @return true if the password matches the hashed password, false otherwise.
     */
    public static boolean verifyPassword(String password, String hashedPassword) {
        try {
            return argon2.verify(hashedPassword, password.toCharArray());
        } finally {
            argon2.wipeArray(password.toCharArray());
        }
    }
}
