package services;

import database.dao.UserDAO;
import entities.DiaryPost;
import entities.Image;
import entities.User;
import jakarta.servlet.http.Part;
import services.FileUploadService;
import services.UserInputService;

import java.io.IOException;
import java.util.Date;

/**
 * Handles diary post-related operations such as creating and persisting new posts.
 * It encapsulates the logic for sanitizing user input, handling image uploads, and associating diary posts with users.
 * This class works in conjunction with {@link UserDAO}, {@link FileUploadService}, and {@link UserInputService} to manage the underlying operations.
 *
 * @author Björn Forsberg
 */
public class DiaryPostService {

    private UserDAO userDAO;

    /**
     * Constructs a new DiaryPostService instance with the injected UserDAO instance.
     * The UserDAO is used for interacting with the database.
     *
     * @param userDAO The DAO responsible for user-related database interactions.
     */
    public DiaryPostService(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    /**
     * Persists a new diary post for a specific user. The post includes a title, content, optional image, and optional image caption.
     * The User instance is retrieved from the database using the username. A created diary post is then associated with the user and persisted to the database.
     *
     * @param username      The username of the user associated with the post.
     * @param title         The title of the diary post.
     * @param post          The content of the diary post.
     * @param image         The image associated with the diary post (optional).
     * @param imageCaption  The caption for the image (optional).
     * @throws IOException if there is an issue handling the image upload.
     */
    public void persistNewPostForUser(String username, String title, String post, Part image, String imageCaption) throws IOException {
        User user = userDAO.getUserWithDiaryPosts(username);
        DiaryPost diaryPost = createNewPost(title, post, image, imageCaption, user);
        user.getDiaryPosts().add(diaryPost);
        diaryPost.setUser(user);

        userDAO.update(user);
    }

    /**
     * Creates a new DiaryPost object with sanitized title, content, and image caption.
     * Also handles the image upload (if provided) by associating the image with the diary post and vice versa.
     *
     * @param title          The title of the diary post.
     * @param post           The content of the diary post.
     * @param imagePart      The image associated with the diary post (optional).
     * @param imageCaption   The caption for the image (optional).
     * @param user           The user associated with the diary post.
     * @return               The created DiaryPost object.
     * @throws IOException  if there is an issue handling the image upload.
     */
    private DiaryPost createNewPost(String title, String post, Part imagePart, String imageCaption, User user) throws IOException {
        String safeTitle = sanitizeInput(title);
        String safePost = sanitizeInput(post);
        String safeImageCaption = sanitizeInput(imageCaption);

        DiaryPost diaryPost = createDiaryPostInstance(safeTitle, safePost);

        FileUploadService fileUploadService = new FileUploadService();

        if (fileUploadService.isImageUploaded(imagePart)) {
            Image image = fileUploadService.getImage(imagePart);
            diaryPost.setImage(image);
            if (safeImageCaption.isEmpty()) {
                safeImageCaption = "Image uploaded by " + user.getUsername();
            }
            diaryPost.setImageCaption(safeImageCaption);
            image.setDiaryPost(diaryPost);
        }

        return diaryPost;
    }

    /**
     * Sanitizes user input to prevent malicious content.
     *
     * @param input  The user input to sanitize.
     * @return       The sanitized user input.
     */
    private String sanitizeInput(String input) {
        UserInputService inputService = new UserInputService();
        return inputService.sanitizeInput(input);
    }

    /**
     * Creates a DiaryPost instance with the provided title, content, and current timestamp.
     * Helper method to createNewPost().
     *
     * @param title  The title of the diary post.
     * @param post   The content of the diary post.
     * @return       The created DiaryPost object.
     */
    private DiaryPost createDiaryPostInstance(String title, String post) {
        Date timestamp = new Date();
        return new DiaryPost(title, post, timestamp);
    }
}


