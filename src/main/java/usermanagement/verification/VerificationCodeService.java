package usermanagement.verification;

import com.mongodb.DuplicateKeyException;
import database.PasswordHashingUtil;
import org.bson.Document;

/**
 * This class manages operations related to the verification codes.
 *
 * @author Björn Forsberg
 */
public class VerificationCodeService {

    private VerificationCodeDatabaseManager databaseManager;

    /**
     * Constructs a new VerificationCodeService, initializing the database manager.
     */
    public VerificationCodeService() {
        databaseManager = new VerificationCodeDatabaseManager();
    }


    /**
     * Stores the email and verification code into the database.
     *
     * @param email the email to which the code was sent
     * @param hashedCode the hashed verification code
     * @return true if the code was stored successfully, false if a duplicate key error occurred
     */
    public boolean storeCode(String email, String hashedCode) {
        Document document = create(email, hashedCode);
        try {
            databaseManager.insertDocument(document);
            return true;
        } catch (DuplicateKeyException e) {
            return false;
        }
    }

    /**
     * Creates a Document object containing the email and hashed verification code.
     *
     * @param email the email to include in the document
     * @param hashedCode the hashed verification code to include in the document
     * @return the Document object containing the email and hashed verification code
     */
    private Document create(String email, String hashedCode) {
        return new Document()
                .append("email", email)
                .append("hashedCode", hashedCode);
    }

    /**
     * Checks if a document with the given email already exists in the database.
     *
     * @param email the email to check for existence in the database
     * @return true if a document with the given email exists, false otherwise
     */
    public boolean doesEmailExist(String email) {
        Document document = databaseManager.findDocument(email);
        return document != null;
    }

    /**
     * Verifies if the given code is valid for the given email. Deletes the document if the code is valid.
     *
     * @param email the email to verify the code for
     * @param code the hashed code to verify
     * @return true if the code is valid for the given email, false otherwise
     */
    public boolean isCodeValid(String email, String code) {
        Document document = databaseManager.findDocument(email);
        if (document != null) {
            return PasswordHashingUtil.verifyPassword(code, document.getString("hashedCode"));
        }
        return false;
    }

    /**
     * Deletes the verification code document for the given email.
     *
     * @param email the email for which to delete the verification code
     */
    public void deleteCode(String email) {
        databaseManager.deleteDocument(email);
    }

}
