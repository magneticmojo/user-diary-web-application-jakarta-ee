package usermanagement.verification;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.Indexes;
import org.bson.Document;

/**
 * This class manages the database operations like inserting a document and fetching all documents.
 * It uses MongoDB as the database. The class is used for the verification code functionality.
 *
 * @author Björn Forsberg
 */
public class VerificationCodeDatabaseManager {
    private final MongoDatabase database;
    private final MongoCollection<Document> collection;

    /**
     * Constructs a new VerificationCodeDatabaseManager, initializing a connection to the MongoDB database "codeverification".
     * It also ensures that the collection has a unique index on the email field.
     */
    public VerificationCodeDatabaseManager() {
        MongoClient mongoClient = MongoClients.create();
        this.database = mongoClient.getDatabase("codeverification");
        this.collection = database.getCollection("verifications");
        createUniqueEmailIndex();
    }

    /**
     * Inserts a new document into the collection in the MongoDB database.
     *
     * @param document The Document object to be inserted into the database.
     */
    public void insertDocument(Document document) {
        collection.insertOne(document);
    }

    /**
     * Finds and returns a document matching the specified email from the collection in the MongoDB database.
     *
     * @param email The email used to find the corresponding document.
     * @return The Document object matching the email or null if not found.
     */
    public Document findDocument(String email) {
        return collection.find(Filters.eq("email", email)).first();
    }

    /**
     * Deletes a document matching the specified email from the collection in the MongoDB database.
     *
     * @param email The email used to find and delete the corresponding document.
     */
    public void deleteDocument(String email) {
        collection.deleteOne(Filters.eq("email", email));
    }

    /**
     * Create a unique index on the email field in the collection, ensuring that no two documents in the collection can have the same email.
     */
    private void createUniqueEmailIndex() {
        collection.createIndex(Indexes.ascending("email"), new IndexOptions().unique(true));
    }
}



