package usermanagement.verification;

import java.util.Random;

/**
 * Responsible for generating random verification codes.
 * The codes generated are six-digit numbers ranging from 100000 to 999999, inclusive.
 *
 * @author Björn Forsberg
 */
public class VerificationCodeGenerator {
    private Random random;
    private static final int LOWER_BOUND = 100000;
    private static final int UPPER_BOUND = 999999;

    /**
     * Constructs a new VerificationCodeGenerator, initializing a random number generator.
     */
    public VerificationCodeGenerator() {
        this.random = new Random();
    }

    /**
     * Generates and returns a random six-digit verification code within the specified range (from 100000 to 999999).
     *
     * @return A random six-digit integer verification code.
     */
    public synchronized int generateCode() {
        return random.nextInt(UPPER_BOUND - LOWER_BOUND + 1) + LOWER_BOUND;
    }
}

